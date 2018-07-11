package com.refugeye.helper;

import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionHelper {

    /**
     * Convert device independent pixels to actual pixels
     *
     * @param context Context
     * @param dp int
     *
     * @return int
     */
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
