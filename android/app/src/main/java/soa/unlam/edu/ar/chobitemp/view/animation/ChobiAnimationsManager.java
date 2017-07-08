package soa.unlam.edu.ar.chobitemp.view.animation;

import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soa.unlam.edu.ar.chobitemp.R;
import soa.unlam.edu.ar.chobitemp.temp.TempStatus;
import soa.unlam.edu.ar.chobitemp.temp.Weather;
import soa.unlam.edu.ar.chobitemp.view.animation.leonids.LeonidsAnimation;
import soa.unlam.edu.ar.chobitemp.view.animation.leonids.SnowflakesAnimation;

/**
 * Created by mcurrao on 06/07/17.
 */

public class ChobiAnimationsManager {

    private Map<Weather, LeonidsAnimation> activeAnimations = new HashMap<>();

    private AppCompatActivity relatedActivity;

    public ChobiAnimationsManager(AppCompatActivity activity) {
        this.relatedActivity = activity;
    }

    public void setAnimationsByTemperature(TempStatus currentTemperature) {
        if(currentTemperature != null) {
            for(Weather w : currentTemperature.getWeatherTypes()) {
                if(!activeAnimations.containsKey(w)) {
                    // SI LA ANIMACION NO ESTA ACTUALMENTE ACTIVA, LA CREO
                    switch (w) {
                        case COLD: {
                            LeonidsAnimation snowFlakes = new SnowflakesAnimation(relatedActivity);
                            activeAnimations.put(w, snowFlakes);
                            snowFlakes.startAnimation(relatedActivity.findViewById(R.id.parent_temperature_container));
                            break;
                        }
//                        case HOT: {
//                            LeonidsAnimation fireStorm = new BurningFireAnimation(relatedActivity);
//                            activeAnimations.put(w, fireStorm);
//                            fireStorm.startAnimation(relatedActivity.findViewById(R.id.temperatureDisplay));
//                            break;
//                        }
                    }
                }
            }

            for(Iterator<Map.Entry<Weather, LeonidsAnimation>> it = activeAnimations.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Weather, LeonidsAnimation> animationEntry = it.next();

                // LOS CLIMAS ACTIVOS QUE YA NO APLIQUEN LOS REMUEVO
                if(!currentTemperature.getWeatherTypes().contains(animationEntry.getKey())) {
                    animationEntry.getValue().endAnimation();
                    it.remove();
                }
            }
        }
    }

    public void killAllAnimations() {
        for(Iterator<Map.Entry<Weather, LeonidsAnimation>> it = activeAnimations.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Weather, LeonidsAnimation> animationEntry = it.next();
            animationEntry.getValue().endAnimation();
            it.remove();
        }
    }
}
