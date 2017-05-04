package io.bradenhart.shifty.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bradenhart on 4/05/17.
 */

public class Utils {

    public static void makeToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void makeToast(Context context, String message) {
        makeToast(context, message, Toast.LENGTH_SHORT);
    }



}
