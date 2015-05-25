package com.akaita.fda;

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
}
