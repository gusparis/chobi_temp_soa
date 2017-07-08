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

public class BurningFireAnimation implements LeonidsAnimation {

    List<ParticleSystem> particles = new ArrayList<>();
    int[] gravities = {Gravity.CENTER | Gravity.CENTER, Gravity.CENTER | Gravity.CENTER};

    public BurningFireAnimation(Activity activity) {

        {
            Drawable firespark = activity.getResources().getDrawable(R.drawable.flare_small);
            DrawableCompat.setTint(firespark, activity.getResources().getColor(R.color.lightFirespark));

            ParticleSystem particleSystem = new ParticleSystem(activity, 20, firespark, 1000)
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 240, 280)
                    .setRotationSpeed(100)
                    .setScaleRange(0.1f, 0.8f)
                    .setAcceleration(0.00005f, 10);
            particles.add(particleSystem);
        }

        {
            Drawable firespark = activity.getResources().getDrawable(R.drawable.flare);
            DrawableCompat.setTint(firespark, activity.getResources().getColor(R.color.darkFirespark));

            ParticleSystem particleSystem = new ParticleSystem(activity, 80, firespark, 1500)
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 220, 260)
                    .setRotationSpeed(42)
                    .setScaleRange(0.3f,1)
                    .setAcceleration(0.00005f, 5);
            particles.add(particleSystem);
        }
    }

    @Override
    public void startAnimation(View view) {
        int i = 0;
        for(ParticleSystem particle : particles) {
            particle.emitWithGravity(view, gravities[i], 12);
            i++;
        }
    }

    @Override
    public void endAnimation() {
        for(ParticleSystem particle : particles) {
            particle.stopEmitting();
        }
    }
}
