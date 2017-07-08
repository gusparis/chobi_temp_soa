package soa.unlam.edu.ar.chobitemp.sensor.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

import soa.unlam.edu.ar.chobitemp.MainActivity;
import soa.unlam.edu.ar.chobitemp.sensor.listener.HandWaveDetector;
import soa.unlam.edu.ar.chobitemp.sensor.listener.ShakeDetector;

/**
 * Created by mcurrao on 05/07/17.
 */

public class ChobiAppSensorsManager {

    private MainActivity associatedActivity;
    private SensorManager sensorsManager;
    private List<SensorEventListener> activeListeners = new ArrayList<>();

    public ChobiAppSensorsManager(MainActivity activity) {
        this.associatedActivity = activity;
        this.sensorsManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerSensors() {
        initHandWaveSensor();
        initShaker();
    }

    protected void initShaker() {
        Sensor accelerometer = sensorsManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeDetector mShakeDetector = new ShakeDetector(associatedActivity);
        sensorsManager.registerListener(mShakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        activeListeners.add(mShakeDetector);
    }

    protected void initHandWaveSensor() {
        Sensor proximitySensor = sensorsManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        HandWaveDetector handWaveDetector = new HandWaveDetector(associatedActivity);
        sensorsManager.registerListener(handWaveDetector, proximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterSensors() {
        for(SensorEventListener listener : activeListeners) {
            sensorsManager.unregisterListener(listener);
        }
    }
}
