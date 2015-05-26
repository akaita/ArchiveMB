package com.akaita.fda;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.akaita.fda.database.Album;
import com.akaita.fda.database.Artist;
import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.Genre;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 25/05/2015.
 */
public class AlbumFragment extends Fragment {
    public static final String ARTIST_ID = "artistId";
    private final static String SEPARATOR = " ";

    private Artist artist;
    private AlbumAdapter albumAdapter;
    private View mView;
    private View mHeader;

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_album, container, false);

        this.artist = getChosenArtist();
        this.albumAdapter = new AlbumAdapter(getActivity());

        addAlbumHeader();
        prepareGridRecyclerView();
        loadItems();


        return this.mView;
    }

    private void addAlbumHeader() {
        this.mHeader = getActivity().getLayoutInflater().inflate(R.layout.artist_details, null, false);
        SetImage.setImage(getActivity(), (ImageView) mHeader.findViewById(R.id.albumImage), this.artist.pictureUrl);
        ((TextView)mHeader.findViewById(R.id.artistName)).setText(artist.name);
        try {
            List<Genre> genreList = this.artist.genres();
            ((TextView) mHeader.findViewById(R.id.artistGenres)).setText(concatenate(genreList, SEPARATOR));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((TextView)mHeader.findViewById(R.id.artistDescription)).setText(Html.fromHtml(this.artist.description));
        this.albumAdapter.addHeader(mHeader);
    }

    private Artist getChosenArtist() {
        Bundle bundle = getArguments();
        long artistId = bundle.getLong(ARTIST_ID);
        Artist artist = null;
        try {
            artist = DaoFactory.getInstance().getArtists().queryForId(artistId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }
    private void prepareGridRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerVie
        recyclerView.setHasFixedSize(true);

        // recyclerView.addItemDecoration(new MarginDecoration(this));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHeader != null) {
                    float tx = mHeader.getX();
                    float ty = mHeader.getY();
                    Log.d("temp", "translation X:" + String.valueOf(tx) + " Y:" + String.valueOf(ty));
                    //TODO add some kinf of parallax effect
                }
            }
        });
        
        recyclerView.setAdapter(this.albumAdapter);
    }

    private void loadItems() {
        try {
            List<Album> albumList = artist.albums();
            for (Album album : albumList){
                albumAdapter.add(album, albumAdapter.getItemCount());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String concatenate(List<Genre> list, String separator) {
        StringBuffer result = new StringBuffer();
        for (Genre genre : list) {
            result.append( genre.name );
            result.append( separator );
        }
        return result.delete(result.length()-separator.length(), result.length()).toString();
    }
}
