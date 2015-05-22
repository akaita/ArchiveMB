package com.akaita.fda;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.akaita.fda.database.PropertyManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mikel on 20/05/2015.
 */
public class UpdateDatabaseTask extends AsyncTask<URL, Integer, Long> {
    public UpdateDatabaseResponse delegate;

    public UpdateDatabaseTask (UpdateDatabaseResponse updateDatabaseResponse){
        this.delegate = updateDatabaseResponse;
    }

    protected Long doInBackground(URL... urls) {
        int count = urls.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            try {
                updateData(urls[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Progress " + progress[0]);
    }

    protected void onPostExecute(Long result) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Downloaded " + result + " URL");
        delegate.updateDatabaseFinish();
    }

    public boolean updateData(URL u) throws SQLException {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);

            long lastModifiedStored = PropertyManager.getLastModifiedDate();
            long lastModifiedOnline = c.getLastModified();
            if ( lastModifiedOnline != 0
                    && lastModifiedStored != lastModifiedOnline ) {
                updateData2(c);
                PropertyManager.setLastModifiedDate(lastModifiedOnline);
                return true;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
                    e.printStackTrace();
                }
        }
    }

    public interface UpdateDatabaseResponse {
        void updateDatabaseFinish();
    }
}
