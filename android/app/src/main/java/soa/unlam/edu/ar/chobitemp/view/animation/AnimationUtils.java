package soa.unlam.edu.ar.chobitemp.view.animation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import soa.unlam.edu.ar.chobitemp.temp.TempStatus;
import soa.unlam.edu.ar.chobitemp.temp.TempStatusFactory;

/**
 * Created by mcurrao on 17/06/17.
 */

public class AnimationUtils {

    public static void animateTemperatureUpdate(float initialValue, final TempStatus finalStatus, final TextView textview, final View parent, final Context context) {
        float finalValue = finalStatus.getTemperature().floatValue();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(800);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatorTemperature = (float) valueAnimator.getAnimatedValue();
                BigDecimal currentTemperature = BigDecimal.valueOf(((Float) animatorTemperature).doubleValue());

                int color = TempStatusFactory.calculateColor(currentTemperature, context);
                parent.setBackgroundColor(color);

                textview.setText(currentTemperature.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            }
        });
        valueAnimator.start();
    }

    public static void animateUpdate(final TextView textView, BigDecimal finalValue) {

        if(finalValue == null) {
            textView.setText(null);
            return;
        }

        String currentText = textView.getText().toString();
        BigDecimal currentValue = BigDecimal.ZERO;
        try {
            currentValue = new BigDecimal(currentText);
        } catch (NullPointerException | NumberFormatException e) {}

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentValue.floatValue(), finalValue.floatValue());
        valueAnimator.setDuration(600);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                BigDecimal currentValue = BigDecimal.valueOf(((Float) animatedValue).doubleValue());

                textView.setText(currentValue.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            }
        });
        valueAnimator.start();
    }
}
