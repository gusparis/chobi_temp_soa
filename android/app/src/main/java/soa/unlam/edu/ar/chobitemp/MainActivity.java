package soa.unlam.edu.ar.chobitemp;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import soa.unlam.edu.ar.chobitemp.connector.VolleyConnector;
import soa.unlam.edu.ar.chobitemp.intent.RefreshTimer;
import soa.unlam.edu.ar.chobitemp.intent.TimedRefreshListener;
import soa.unlam.edu.ar.chobitemp.lights.LightStatus;
import soa.unlam.edu.ar.chobitemp.sensor.listener.HandWaveDetector;
import soa.unlam.edu.ar.chobitemp.sensor.listener.ShakeDetector;
import soa.unlam.edu.ar.chobitemp.sensor.manager.ChobiAppSensorsManager;
import soa.unlam.edu.ar.chobitemp.settings.AlertDialogBuilder;
import soa.unlam.edu.ar.chobitemp.temp.CurrentLocalStatus;
import soa.unlam.edu.ar.chobitemp.temp.RemoteStatus;
import soa.unlam.edu.ar.chobitemp.temp.TempStatusFactory;
import soa.unlam.edu.ar.chobitemp.temp.TemperatureSource;
import soa.unlam.edu.ar.chobitemp.view.animation.AnimationUtils;
import soa.unlam.edu.ar.chobitemp.view.animation.ChobiAnimationsManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ShakeDetector.OnShakeListener, HandWaveDetector.OnHandWaveListener, TimedRefreshListener<RemoteStatus> {

    public static final String SAVED_CURRENT_STATUS = "currentStatus";
    private boolean isCurrentlyRefreshing = false;

    private ChobiAppSensorsManager sensorsManager;
    private ChobiAnimationsManager animationsManager;
    private RefreshTimer statusRefreshTimer;

    private CurrentLocalStatus currentStatus;
    private float currentTemperatureOnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // FINISHED DRAWER AND TOOLBAR SETUP INITIALIZATION

        // REFRESH ON SWYPE
        ((SwipeRefreshLayout) findViewById(R.id.swipe_container)).setOnRefreshListener(this);

        // RELOAD DATA IF ALREADY PRESENT. OLD BACKGROUND APP.
        if (savedInstanceState != null) {
            this.currentStatus = (CurrentLocalStatus) savedInstanceState.getSerializable(SAVED_CURRENT_STATUS);
            if(this.currentStatus != null) {
                if(this.currentStatus.getTempStatus() != null)
                currentTemperatureOnDisplay = this.currentStatus.getTempStatus().getTemperature().floatValue();
            } else {
                this.currentStatus = TempStatusFactory.buildDefaultStatus(this);
            }
        } else {
            this.currentStatus = TempStatusFactory.buildDefaultStatus(this);
        }

        setTemperatureSourceOnView(this.currentStatus.getSource());

        this.sensorsManager = new ChobiAppSensorsManager(this);
        this.statusRefreshTimer = new RefreshTimer(this, this);
        this.animationsManager = new ChobiAnimationsManager(this);

        reactToCurrentStatus();
    }

    /**
     * Al volver, registro el timer y los eventos de sensores. Esto también se ejecuta al iniciar la
     * app por primera vez
     */
    @Override
    public void onResume() {
        super.onResume();
        statusRefreshTimer.schedule();
        onRefresh();
        sensorsManager.registerSensors();
    }

    @Override
    public void onPause() {
        statusRefreshTimer.unschedule();
        sensorsManager.unregisterSensors();
        animationsManager.killAllAnimations();
        VolleyConnector.cancelAll();

        super.onPause();
    }

    @Override
    public void onShake(int count) {
        TemperatureSource ts = currentStatus.getSource() == TemperatureSource.CHOBI ? TemperatureSource.CLOUD : TemperatureSource.CHOBI;
        setTemperatureSourceOnView(ts);
        onRefresh();
    }

    @Override
    public void onHandWave() {
        if(currentStatus.getLightStatus() != null) {
            if(!currentStatus.getLightStatus().getLightsOn()) {
                Toast.makeText(MainActivity.this
                        , "Lumos...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this
                        , "Nox...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this
                    , "The force is strong with you...", Toast.LENGTH_SHORT).show();
        }
        switchLightStatus();
    }

    private void switchLightStatus() {
        if(this.currentStatus.getLightStatus() == null)
            this.currentStatus.setLightStatus(new LightStatus());
        this.currentStatus.getLightStatus().setDesiredSwitch(true);
        ((SwipeRefreshLayout) MainActivity.this.findViewById(R.id.swipe_container)).setRefreshing(true);
        refreshStatusFromRemoteSource();
    }

    private void setTemperatureSourceOnView(TemperatureSource temperatureSource) {
        TempStatusFactory.fillDataBySource(currentStatus, temperatureSource, this);
        ((NavigationView) findViewById(R.id.nav_view)).setCheckedItem(temperatureSource.getDrawerId());
        ((AppCompatImageView) findViewById(R.id.curent_source_indicator)).setImageResource(temperatureSource.drawableResource);
        ((TextView) findViewById(R.id.current_source_text_display)).setText(getText(temperatureSource.description()));
    }

    @Override
    protected void onDestroy() {
        SharedPreferences prefs = getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        prefs.edit().putString(ChobiConstants.SharedPreferences.TEMPERATURE_SOURCE, this.currentStatus.getSource().name()).commit();
        super.onDestroy();
    }

    /**
     * Al pasar la aplicacion a segundo plano, guardo los elementos
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_CURRENT_STATUS, this.currentStatus);
        super.onSaveInstanceState(outState);
    }

    /**
     * Boton de volver. Cierra el menu si esta abierto, o la aplicacion si estaba cerrado.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    /**
     * Método que se ejecuta al seleccionar un item del menu del costado. Esto puede ser tanto
     * un cambio de temperatureSource, como un item con accion asociada (configuracion).
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // HANDLE DRAWER ITEM SELECTION
        int id = item.getItemId();
        TemperatureSource temperatureSource = TemperatureSource.findByDrawerId(id);

        if (temperatureSource != null) {
            this.currentStatus.setSource(temperatureSource);
            setTemperatureSourceOnView(temperatureSource);
            ((SwipeRefreshLayout) MainActivity.this.findViewById(R.id.swipe_container)).setRefreshing(true);
            refreshStatusFromRemoteSource();
        } else if (id == R.id.server_setup) {
            AlertDialogBuilder.serverSetup(this, new AlertDialogBuilder.OnConfirmListener() {
                public void onConfirm(DialogInterface dialog, Context context, String input) {
                    updateServerEndpoint(input);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateServerEndpoint(String input) {
        SharedPreferences prefs = getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        prefs.edit().putString(ChobiConstants.SharedPreferences.CHOBI_SERVICE_ENDPOINT, input).commit();
        Snackbar.make(findViewById(R.id.parent_temperature_container), "Preferencias de servidor actualizadas", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Inicia el fetch de la temperatura en 2do plano. Y al volver, actualiza sobre la vista la temperatura.
     */
    @Override
    public void onRefresh() {
        refreshStatusFromRemoteSource();
    }

    private void refreshStatusFromRemoteSource() {
        statusRefreshTimer.callNow();
    }

    @Override
    public boolean isRefreshing() {
        return isCurrentlyRefreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.isCurrentlyRefreshing = refreshing;

        // Agregarle un if aca para solo hacerlo si es nulo. Eso si no quiero molestar cada 5 segundos.
        if(!refreshing)
            ((SwipeRefreshLayout) MainActivity.this.findViewById(R.id.swipe_container)).setRefreshing(refreshing);
    }

    @Override
    public void onRefresh(RemoteStatus remoteStatus) {
        // TOMO EL SOURCE DE MI ESTADO ANTERIOR
        // LO INICIALIZO AL ABRIR LA APP
        this.currentStatus = TempStatusFactory.buildCurrentStatus(remoteStatus, currentStatus.getSource(), this);
        reactToCurrentStatus();
        statusRefreshTimer.schedule();
    }

    @Override
    public void onRefreshError(Exception error) {
        statusRefreshTimer.unschedule();
        Toast.makeText(MainActivity.this, getResources().getText(R.string.connection_error), Toast.LENGTH_SHORT).show();
        currentStatus = TempStatusFactory.buildErrorStatus(currentStatus);
        reactToCurrentStatus();
    }

    @Override
    public CurrentLocalStatus getRefreshSource() {
        return currentStatus;
    }

    private void reactToCurrentStatus() {

        final TextView temperatureStatusViewer = (TextView) findViewById(R.id.temperatureDisplay);
        final View background = findViewById(R.id.parent_temperature_container);
        final ImageView temperatureIndicatorIcon = (ImageView) findViewById(R.id.temperature_indicator_icon);

        final ImageView humidityIndicatorIcon = (ImageView) findViewById(R.id.rain_drop_indicator_icon);
        final TextView humidityStatusViewer = (TextView) findViewById(R.id.humidityDisplay);

        final AppCompatImageView lightsIndicator = (AppCompatImageView) findViewById(R.id.contextual_lights_status_indicator);

        int lightsVisibility = View.INVISIBLE;
        int humidityVisibility = View.INVISIBLE;
        int temperatureIndicatorVisibility = View.VISIBLE;

        if(currentStatus.hasData()) {

            AnimationUtils.animateTemperatureUpdate(currentTemperatureOnDisplay, currentStatus.getTempStatus(), temperatureStatusViewer, background, this);
            animationsManager.setAnimationsByTemperature(currentStatus.getTempStatus());
            currentTemperatureOnDisplay = currentStatus.getTempStatus().getTemperature().floatValue();

            if(currentStatus.getHumidity() != null) {
                humidityVisibility = View.VISIBLE;
                AnimationUtils.animateUpdate(humidityStatusViewer, currentStatus.getHumidity());
            }

            if(currentStatus.getLightStatus() != null && currentStatus.getLightStatus().getLightsOn() != null) {
                lightsIndicator.setImageResource(currentStatus.getLightStatus().getLightsOn() ? R.drawable.lightbulb : R.drawable.lightbulb_off);
                lightsVisibility = View.VISIBLE;
            }

            if(currentStatus.getCurrentSourceDescription() != null)
                ((TextView) findViewById(R.id.current_source_text_display)).setText(currentStatus.getCurrentSourceDescription());

        } else {

            if(currentStatus.isError()) {
                animationsManager.killAllAnimations();
                temperatureStatusViewer.setText(getText(R.string.temperature_unobtainable_placeholder));
                //background.setBackgroundColor(getResources().getColor(R.color.color_unknownTemp));
                this.currentTemperatureOnDisplay = 0;
                temperatureIndicatorVisibility = View.INVISIBLE;
                background.setBackgroundResource(R.drawable.network_red_rot);
            }

        }

        lightsIndicator.setVisibility(lightsVisibility);
        temperatureIndicatorIcon.setVisibility(temperatureIndicatorVisibility);
        humidityStatusViewer.setVisibility(humidityVisibility);
        humidityIndicatorIcon.setVisibility(humidityVisibility);
    }

}
