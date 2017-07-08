package soa.unlam.edu.ar.chobitemp.sensor.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Se utiliza para detectar el evento de mover la mano sobre el sensor de proximidad
 * Created by mcurrao on 02/07/17.
 */
public class HandWaveDetector implements SensorEventListener {

    private static final long SWIPE_SLOP_TIME_MS = 300;
    private static final long SWIPE_COUNT_RESET_TIME_MS = 5000;
    private static final int TRIGGER_TRESHOLD = 2;

    private long swipeTimestamp;
    private int swipeCount;

    private Boolean previousState;
    private OnHandWaveListener onHandWaveListener;

    public static interface OnHandWaveListener {
        public void onHandWave();
    }

    public HandWaveDetector(OnHandWaveListener onHandWaveListener) {
        this.onHandWaveListener = onHandWaveListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (onHandWaveListener != null) {

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                boolean isClose = event.values[0] >= event.sensor.getMaximumRange();
                if(previousState == null)
                    previousState = isClose;

                // if distance changed, but still close, is not a change
                if(previousState == isClose)
                    return;

                previousState = isClose;

                final long now = System.currentTimeMillis();
                // ignore wave events too close to each other (500ms)
                if (swipeTimestamp + SWIPE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no swipes
                if (swipeTimestamp + SWIPE_COUNT_RESET_TIME_MS < now) {
                    swipeCount = 0;
                }

                swipeTimestamp = now;
                swipeCount++;

                if (swipeCount >= TRIGGER_TRESHOLD) {
                    swipeCount = 0;
                    onHandWaveListener.onHandWave();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }
}
