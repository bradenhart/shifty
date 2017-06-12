package io.bradenhart.shifty.util;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

/**
 * Utility methods for the application.
 *
 * @author bradenhart
 */
public class Utils {

    /**
     * A short hand method for showing a toast.
     * @param context the context for the toast to use
     * @param message the message for the toast to display
     * @param duration the duration of the toast
     */
    public static void makeToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static Animation getAnim(Context context, int anim) {
        return AnimationUtils.loadAnimation(context, anim);
    }

}
