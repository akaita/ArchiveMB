package com.akaita.fda;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.akaita.fda.imagedownload.SetImage;

import java.util.Date;

/**
 * Created by mikel on 28/05/2015.
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    public static class PreferencesFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            PreferencesManager preferencesManager = new PreferencesManager(getActivity());

            Preference lastModified = findPreference(getResources().getString(R.string.pref_last_modified_time));
            lastModified.setSummary(new Date(preferencesManager.getLastModifiedDate()).toString());
            Preference dbUrl = findPreference(getResources().getString(R.string.pref_db_url));
            dbUrl.setSummary(preferencesManager.getDatabaseUrl());
            Preference cache = findPreference(getResources().getString(R.string.pref_cache_image_enable));
            cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SetImage.setMethod(((CheckBoxPreference)preference).isChecked() ? SetImage.Method.CACHE : SetImage.Method.ASYNCTASK);
                    return true;
                }
            });
            Preference indicator = findPreference(getResources().getString(R.string.pref_cache_image_indicator));
            indicator.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SetImage.setCacheIndicatorEnabled(((CheckBoxPreference)preference).isChecked());
                    return true;
                }
            });
        }
    }

}