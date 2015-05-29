package com.akaita.fda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.objects.Artist;
import com.akaita.fda.imagedownload.SetImage;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements ArtistFragment.OnArtistSelectedListener, ArtistFragment.OnArtistListUpdatedListener {
    public static final String TAG_MAIN_FRAGMENT = "main_fragment";
    public static final String TAG_ALBUM_FRAGMENT = "album_fragment";

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showUpdateInstructions(!isDataAvailable());
        checkNetworkAccess();

        setConfiguration();

        showFragmentArtists();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                launchPreferences();
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
        Bundle bundle = new Bundle();
        bundle.putLong(AlbumFragment.EXTRA_ARTIST_ID, artist.getId());

        // Create new fragment and transaction
        AlbumFragment newFragment = new AlbumFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment, newFragment, TAG_ALBUM_FRAGMENT);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

        //remove search action
        //(seems like there's some problem with the current AppCompat version, so I have to do this here :( )
        this.mMenu.removeItem(R.id.action_search);
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

    private void setConfiguration() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        SetImage.setMethod(preferencesManager.isCacheImageEnabled() ? SetImage.Method.CACHE : SetImage.Method.ASYNCTASK);
        SetImage.setCacheIndicatorEnabled(preferencesManager.isCacheImageIndicatorEnabled());
    }

    private void launchPreferences() {
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
    }
}
