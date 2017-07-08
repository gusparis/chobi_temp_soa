package soa.unlam.edu.ar.chobitemp.view.animation.leonids;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.View;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

import soa.unlam.edu.ar.chobitemp.R;

/**
 * Created by mcurrao on 27/06/17.
 */

public class SnowflakesAnimation implements LeonidsAnimation {

    List<ParticleSystem> particles = new ArrayList<>();
    int[] gravities = {Gravity.TOP | Gravity.LEFT, Gravity.TOP | Gravity.CENTER, Gravity.TOP | Gravity.RIGHT};

    public SnowflakesAnimation(Activity activity) {

        Drawable snowflake = activity.getResources().getDrawable(R.drawable.snowflake);
        DrawableCompat.setTint(snowflake, activity.getResources().getColor(R.color.snowflakeColor));

        {
            ParticleSystem particleSystem = new ParticleSystem(activity, 20, R.drawable.snowflake, 10000)
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 25, 90)
                    .setRotationSpeed(144)
                    .setScaleRange(0.5f,1.5f)
                    .setAcceleration(0.00005f, 20);
            particles.add(particleSystem);
        }

        {
            ParticleSystem particleSystem = new ParticleSystem(activity, 20, R.drawable.snowflake, 10000)
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 60, 95)
                    .setRotationSpeed(144)
                    .setScaleRange(0.5f,1.5f)
                    .setAcceleration(0.00005f, 10);
            particles.add(particleSystem);
        }

        /*{
            ParticleSystem particleSystem = new ParticleSystem(activity, 20, R.drawable.snowflake, 10000)
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 100, 120)
                    .setRotationSpeed(120)
                    .setAcceleration(0.00005f, 15);
            particles.add(particleSystem);
        }*/

    }

    @Override
    public void startAnimation(View view) {
        int i = 0;
        for(ParticleSystem particle : particles) {
            particle.emitWithGravity(view, gravities[i], 2);
            i++;
        }
    }

    @Override
    public void endAnimation() {
        for(ParticleSystem particle : particles) {
            //particle.stopEmitting();
            particle.cancel();
        }
    }
}
