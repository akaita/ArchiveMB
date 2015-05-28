package com.akaita.fda;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mikel on 28/05/2015.
 */
public class PreferencesManager {
    public static final int KEY_LAST_MODIFIED_DATE = R.string.pref_last_modified_time;
    public static final int KEY_CACHE_IMAGE_ENABLE = R.string.pref_cache_image_enable;
    public static final int KEY_CACHE_IMAGE_INDICATOR = R.string.pref_cache_image_indicator;
    public static final int KEY_DB_URL = R.string.pref_db_url;
    public static final int KEY_PAGE_SIZE = R.string.pref_page_size;

    private SharedPreferences mSettings;
    private Context mContext;

    public PreferencesManager(Context context) {
        this.mContext = context;
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isCacheImageEnabled() {
        return this.mSettings.getBoolean(this.mContext.getString(KEY_CACHE_IMAGE_ENABLE), true);
    }

    public boolean isCacheImageIndicatorEnabled() {
        return this.mSettings.getBoolean(this.mContext.getString(KEY_CACHE_IMAGE_INDICATOR), false);
    }

    public long getLastModifiedDate() {
        return Long.valueOf(this.mSettings.getString(this.mContext.getString(KEY_LAST_MODIFIED_DATE), "0"));
    }

    public String getDatabaseUrl() {
        return this.mSettings.getString(this.mContext.getString(KEY_DB_URL), "http://i.img.co/data/data.json");
    }

    public int getPageSize() {
        return Integer.valueOf(this.mSettings.getString(this.mContext.getString(KEY_PAGE_SIZE), "10"));
    }

    public void setLastModifiedDate(long date) {
        SharedPreferences.Editor editor = this.mSettings.edit();
        editor.putString(mContext.getString(KEY_LAST_MODIFIED_DATE), String.valueOf(date));
        editor.commit();
    }
}
