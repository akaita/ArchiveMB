package com.akaita.fda;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.Artist;
import com.akaita.fda.database.DaoFactory;

import java.sql.SQLException;

/**
 * Created by mikel on 25/05/2015.
 */
public class AlbumFragment extends Fragment {
    public static final String ARTIST_ID = "artistId";

    private Artist artist;
    private View mView;

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.artist_thumb_card, container, false);

        Bundle bundle=getArguments();
        long artistId = bundle.getLong(ARTIST_ID);
        try {
            this.artist = DaoFactory.getInstance().getArtists().queryForId((int) artistId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.mView;
    }
}
