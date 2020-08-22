package com.agenda.diario.Util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by portatile on 09/08/2016.
 */
public class AlphaManager {
    private AlphaManager(){}

    @SuppressLint("NewApi")
    public static void setAlpha(View v, float alpha) {
        if (v != null) {
            if (Build.VERSION.SDK_INT < 11) {
                final AlphaAnimation animation = new AlphaAnimation(1F, alpha);
                animation.setFillAfter(true);
                v.startAnimation(animation);
            } else {
                v.setAlpha(alpha);
            }
        }
    }
}
