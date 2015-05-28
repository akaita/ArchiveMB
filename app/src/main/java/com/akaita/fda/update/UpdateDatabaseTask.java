package com.akaita.fda.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.akaita.fda.PreferencesManager;
import com.akaita.fda.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by mikel on 20/05/2015.
 */
public class UpdateDatabaseTask extends AsyncTask<URL, Integer, Boolean> {
    private PreferencesManager mPreferencesManager;
    public OnUpdateDatabaseFinishListener mOnUpdateDatabaseFinishListener;

    public UpdateDatabaseTask (OnUpdateDatabaseFinishListener onUpdateDatabaseFinishListener, PreferencesManager preferencesManager){
        this.mOnUpdateDatabaseFinishListener = onUpdateDatabaseFinishListener;
        this.mPreferencesManager = preferencesManager;
    }

    protected Boolean doInBackground(URL... urls) {
        boolean updatesHappened = false;
        int count = urls.length;
        for (int i = 0; i < count; i++) {
            try {
                updatesHappened = updatesHappened || updateData(urls[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return updatesHappened;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.i(getClass().toString(), "Progress: " + progress[0]);
    }

    protected void onPostExecute(Boolean result) {
        Log.i(getClass().toString(), result ? "New data downloaded" : "No new data to download");
        mOnUpdateDatabaseFinishListener.onUpdateDatabaseFinish(result);
    }

    /**
     * Check provided URL and try to update the data stored in the database<br>
     * Data won't be updated if the content of the URL hasn't changed since the last update
     *
     * @param url
     * @return true when data updated, false when data not updated
     * @throws SQLException
     */
    private boolean updateData(URL url) throws SQLException {
        Log.d(getClass().toString(), "Connect with online database: " + url.toString());
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);

            long lastModifiedStored =  this.mPreferencesManager.getLastModifiedDate();
            long lastModifiedOnline = c.getLastModified();
            if ( lastModifiedOnline != 0
                    && lastModifiedStored != lastModifiedOnline ) {
                Log.d(getClass().toString(), "Online database: NEW version");
                Log.d(getClass().toString(), "Online database timestamp: " + String.valueOf(lastModifiedOnline));
                updateData2(c);
                this.mPreferencesManager.setLastModifiedDate(lastModifiedOnline);
                return true;
            } else {
                Log.d(getClass().toString(), "Online database: OLD version");
            }
        } catch (IOException ex) {
            Log.e(getClass().toString(), "IOException: " + ex.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Log.e(getClass().toString(), "Connection error when disconnecting: " + ex.getMessage());
                }
            }
        }
        return false;
    }

    private void updateData2(HttpURLConnection c) throws IOException {
        c.connect();
        int status = c.getResponseCode();
        switch (status) {
            case 200:
            case 201:
                ParseAndStore parseAndStore = new ParseAndStore(new InputStreamReader(c.getInputStream()));
                try {
                    parseAndStore.artistAndAlbums();
                } catch (SQLException e) {
                    Log.e(getClass().toString(), "SQLException (" +e.getSQLState()+ "): " + e.getMessage());
                }
                break;
            default:
                Log.e(getClass().toString(), "Unhandled response code: " + String.valueOf(status));
                break;
        }
    }

    public interface OnUpdateDatabaseFinishListener {
        void onUpdateDatabaseFinish(boolean newData);
    }
}
