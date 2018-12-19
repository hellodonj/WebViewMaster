package com.galaxyschool.app.wawaschool.common;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by E450 on 2016/11/2.
 */

public class AnimationUtil {

    public static void setAnimation(ImageView imageView ){
        PropertyValuesHolder pvhW = PropertyValuesHolder.ofFloat("scaleX",
                (float) 0.8, (float) 1.0, (float) 0.8);
        PropertyValuesHolder pvhH = PropertyValuesHolder.ofFloat("scaleY",
                (float) 0.8, (float) 1.0, (float) 0.8);
        ObjectAnimator animator = ObjectAnimator
                .ofPropertyValuesHolder(imageView, pvhW, pvhH);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();

    }

}
