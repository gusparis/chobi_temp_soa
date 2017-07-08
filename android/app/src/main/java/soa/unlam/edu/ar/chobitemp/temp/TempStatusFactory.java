package soa.unlam.edu.ar.chobitemp.temp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import soa.unlam.edu.ar.chobitemp.ChobiConstants;
import soa.unlam.edu.ar.chobitemp.lights.LightStatus;

import static soa.unlam.edu.ar.chobitemp.temp.TempStatusFactory.Colors.COLD;
import static soa.unlam.edu.ar.chobitemp.temp.TempStatusFactory.Colors.HOT;

/**
 * Created by mcurrao on 04/06/17.
 */

public class TempStatusFactory {

    public static CurrentLocalStatus buildCurrentStatus(RemoteStatus remoteStatus, TemperatureSource source, Context context) {
        CurrentLocalStatus status = new CurrentLocalStatus();

        status.setRemoteStatus(remoteStatus);
        fillDataBySource(status, source, context);

        return status;
    }

    public static void fillDataBySource(CurrentLocalStatus status, TemperatureSource source, Context context) {
        RemoteStatus remoteStatus = status.getRemoteStatus();

        status.setSource(source);

        TempStatus temperatureStatus = null;
        LightStatus lightStatus = new LightStatus();
        BigDecimal humidity = null;
        String location = null;

        if(remoteStatus != null) {

            RemoteStatus.RemoteSourceData remoteSource;
            if(source == TemperatureSource.CHOBI)
                remoteSource = remoteStatus.getSensor();
            else
                remoteSource = remoteStatus.getCloud();

            if(remoteSource != null) {
                temperatureStatus = buildTemp(remoteSource.getTemperature(), context);
                humidity = remoteSource.getHumidity();
                location = remoteSource.getLocation();
                if(remoteStatus.getLight() != null)
                    lightStatus.setLightsOn(remoteStatus.getLight());
            }

        }

        status.setLightStatus(lightStatus);
        status.setCurrentSourceDescription(location);
        status.setHumidity(humidity);
        status.setTempStatus(temperatureStatus);
    }

    private static TemperatureSource defaultTemperatureSource(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        String temperatureSourceName = prefs.getString(ChobiConstants.SharedPreferences.TEMPERATURE_SOURCE, TemperatureSource.CHOBI.name());
        return TemperatureSource.valueOf(temperatureSourceName);
    }

    public static CurrentLocalStatus buildDefaultStatus(Context context) {
        CurrentLocalStatus status = new CurrentLocalStatus();

        TemperatureSource temperatureSource = defaultTemperatureSource(context);
        status.setSource(temperatureSource);

        return status;
    }

    public static CurrentLocalStatus buildErrorStatus(CurrentLocalStatus previousStatus) {
        CurrentLocalStatus status = new CurrentLocalStatus();
        status.setSource(previousStatus.getSource());
        status.setError(true);
        return status;
    }

    public static interface Colors {
        public final static int COLD = Color.rgb(111, 165, 252);
        public final static int HOT = Color.rgb(211, 33, 33);
    }

    public static TempStatus buildTemp(double temperature, Context context) {
        BigDecimal actualTemp = BigDecimal.valueOf(temperature);
        return buildTemp(actualTemp, context);
    }

    public static TempStatus buildTemp(String result, Context context) {
        BigDecimal actualTemp = new BigDecimal(result);
        return buildTemp(actualTemp, context);
    }

    public static TempStatus buildTemp(BigDecimal temperature, Context context) {
        if(temperature == null)
            return null;

        TempStatus s = new TempStatus();
        s.setTemperature(temperature);

        int resultColor = calculateColor(temperature, context);
        s.setColor(resultColor);

        s.setWeatherTypes(deduceWeatherTypes(temperature, context));
        return s;
    }

    private static List<Weather> deduceWeatherTypes(BigDecimal temperature, Context context) {
        List<Weather> temperatureTypes = new ArrayList<>();

        if(temperature.compareTo(fetchHotLimit(context)) >= 0)
            temperatureTypes.add(Weather.HOT);
        else if(temperature.compareTo(fetchColdLimit(context)) <= 0)
            temperatureTypes.add(Weather.COLD);

        return temperatureTypes;
    }

    public static int calculateColor(BigDecimal temperature, Context context) {
        double ratio = calculateRatio(temperature, context);

        int red = (int)Math.abs((ratio * Color.red(HOT)) + ((1 - ratio) * Color.red(COLD)));
        int green = (int)Math.abs((ratio * Color.green(HOT)) + ((1 - ratio) * Color.green(COLD)));
        int blue = (int)Math.abs((ratio * Color.blue(HOT)) + ((1 - ratio) * Color.blue(COLD)));

        int resultColor = Color.rgb(red, green, blue);
        return resultColor;
    }

    private static double calculateRatio(BigDecimal actualTemp, Context context) {
        BigDecimal lower_temperature = fetchColdLimit(context);
        BigDecimal higher_temperature = fetchHotLimit(context);

        actualTemp = actualTemp.subtract(lower_temperature);
        BigDecimal proportion = higher_temperature.subtract(lower_temperature);

        if(actualTemp.compareTo(BigDecimal.ZERO) <= 0 || proportion.equals(BigDecimal.ZERO))
            return 0;

        BigDecimal ratio = actualTemp.divide(proportion);
        if(ratio.compareTo(BigDecimal.ZERO) <= 0)
            return 0;
        if(ratio.compareTo(BigDecimal.ONE) >= 0)
            return 1;
        return ratio.doubleValue();
    }

    private static BigDecimal fetchLimit(String limitToGet, float defaultValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        return new BigDecimal(sp.getFloat(limitToGet, defaultValue));
    }

    private static BigDecimal fetchHotLimit(Context context) {
        return fetchLimit(ChobiConstants.SharedPreferences.CHOBI_TEMPERATURE_MAX, ChobiConstants.DEFAULT_MAX_TEMP, context);
    }

    private static BigDecimal fetchColdLimit(Context context) {
        return fetchLimit(ChobiConstants.SharedPreferences.CHOBI_TEMPERATURE_MIN, ChobiConstants.DEFAULT_MIN_TEMP, context);
    }
}
