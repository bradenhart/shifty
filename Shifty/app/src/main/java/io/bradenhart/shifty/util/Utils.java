package io.bradenhart.shifty.util;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

/**
 * @author bradenhart
 */
public class Utils {

    public static void makeToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void makeToast(Context context, String message) {
        makeToast(context, message, Toast.LENGTH_SHORT);
    }

    public static Animation getAnim(Context context, int anim) {
        return AnimationUtils.loadAnimation(context, anim);
    }

}
