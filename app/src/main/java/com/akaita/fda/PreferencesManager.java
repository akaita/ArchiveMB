package com.akaita.fda;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mikel on 28/05/2015.
 */
public class PreferencesManager {
    public static final int KEY_LAST_MODIFIED_DATE = R.string.pref_last_modified_time;
    public static final int KEY_CACHE_IMAGE_ENABLE = R.string.pref_cache_image_enable;
    public static final int KEY_CACHE_IMAGE_INDICATOR = R.string.pref_cache_image_indicator;

    private SharedPreferences mSettings;
    private Context mContext;

    public PreferencesManager(Context context) {
        this.mContext = context;
        this.mSettings = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
    }

    public boolean isCacheImageEnabled() {
        return mSettings.getBoolean(mContext.getString(KEY_CACHE_IMAGE_ENABLE), true);
    }

    public boolean isCacheImageIndicatorEnabled() {
        return mSettings.getBoolean(mContext.getString(KEY_CACHE_IMAGE_INDICATOR), false);
    }

    public long getLastModifiedDate() {
        return Long.valueOf(mSettings.getString(mContext.getString(KEY_LAST_MODIFIED_DATE), "0"));
    }

    public void setLastModifiedDate(long date) {
        SharedPreferences.Editor editor = this.mSettings.edit();
        editor.putString(mContext.getString(KEY_LAST_MODIFIED_DATE), String.valueOf(date));
        editor.commit();
    }
}
