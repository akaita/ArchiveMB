package com.akaita.fda;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.akaita.fda.database.Artist;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements UpdateDatabaseTask.UpdateDatabaseResponse, ArtistFragment.OnArtistSelectedListener {
    public static final String URL_1 = "http://i.img.co/data/data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            URL url1 = new URL(URL_1);
            showProgressBar(true);
            new UpdateDatabaseTask(this).execute(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        showFragmentArtists();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateDatabaseFinish() {
        showProgressBar(false);
        showFragmentArtists();
    }

    private void showProgressBar(boolean show) {
        ProgressBar progressBar = ((ProgressBar) findViewById(R.id.progressBar));
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
        transaction.replace(R.id.fragment, newFragment);
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
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }
}
