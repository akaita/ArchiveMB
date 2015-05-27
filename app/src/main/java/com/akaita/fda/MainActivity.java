package com.akaita.fda;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.objects.Artist;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements ArtistFragment.OnArtistSelectedListener, ArtistFragment.OnArtistListUpdatedListener {
    public static final String URL_1 = "http://i.img.co/data/data.json";
    public static final String TAG_MAIN_FRAGMENT = "main_fragment";
    public static final String TAG_ALBUM_FRAGMENT = "album_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showUpdateInstructions(!isDataAvailable());
        checkNetworkAccess();

        showFragmentArtists();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistSelected(Artist artist) {
        showFragmentAlbum(artist);
    }

    private void showFragmentArtists() {
        // Create new fragment and transaction
        MainActivityFragment newFragment = new MainActivityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment, newFragment, TAG_MAIN_FRAGMENT);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void showFragmentAlbum(Artist artist) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundl = new Bundle();
        bundl.putLong(AlbumFragment.EXTRA_ARTIST_ID, artist.getId());

        // Create new fragment and transaction
        AlbumFragment newFragment = new AlbumFragment();
        newFragment.setArguments(bundl);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment, newFragment, TAG_ALBUM_FRAGMENT);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onArtistListUpdated() {
        if (isDataAvailable()) {
            showUpdateInstructions(false);
            MainActivityFragment mainActivityFragment = (MainActivityFragment)
                    getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);

            if (mainActivityFragment != null) {
                mainActivityFragment.updateSlidingTabs();
            }
        } else {
            showUpdateFailed(true);
        }
    }

    private boolean isDataAvailable() {
        //Beware: can't use the lastModifiedDate property.
        //Receiving malformed JSON would set lastModifiedDate, but we still would have no data
        boolean dataAvailable = false;
        try {
            dataAvailable = (DaoFactory.getInstance().getArtistDao().countOf() > 0);
        } catch (SQLException e) {
            Log.e(getClass().toString(), "SQLException (" + e.getSQLState() + "): " + e.getMessage());
        }
        return dataAvailable;
    }

    private void showUpdateInstructions(boolean show) {
        Log.i(getClass().toString(), "Showing update instructions");
        this.findViewById(R.id.updateInstructions).setVisibility(show?View.VISIBLE:View.GONE);
    }

    private void showUpdateFailed(boolean show) {
        Log.i(getClass().toString(), "Showing 'update-failed' info");
        Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
    }

    private void checkNetworkAccess() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Log.i(getClass().toString(), "Network access: NO");
            Toast.makeText(this, R.string.network_access_error, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.cant_update_nor_download_images, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(getClass().toString(), "Network access: YES");
        }
    }
}
