package com.akaita.fda;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by mikel on 25/05/2015.
 */
public class JavaUtils {
    private JavaUtils() {

    }

    public static int findMax(int[] array) {
        int max_value = Integer.MIN_VALUE;
        for ( int value : array ) {
            if ( value > max_value ) {
                max_value = value;
            }
        }
        return max_value;
    }

    public static int getColumns(Context context, float columnSizeInches) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        int width = metrics.widthPixels;
        float dp = (int)(width / dpi);
        return (int) (dp / columnSizeInches);
    }
}
