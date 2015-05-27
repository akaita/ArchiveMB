package com.akaita.fda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public static final String EXTRA_ARTIST_ID = "artistId";
    public static final String SEPARATOR = " ";

    private Artist artist;
    private View mView;
    private LinearLayout mLinearLayout;

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_album, container, false);
        this.mLinearLayout = (LinearLayout) mView.findViewById(R.id.linearLayout);

        this.artist = getChosenArtist();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addHeader();
        addDescription();
        addAlbums();


        return this.mView;
    }

    private Artist getChosenArtist() {
        Bundle bundle = getArguments();
        long artistId = bundle.getLong(EXTRA_ARTIST_ID);
        Artist artist = null;
        try {
            artist = DaoFactory.getInstance().getArtistDao().queryForId(artistId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;
    }

    private void addHeader() {
        View header = getActivity().getLayoutInflater().inflate(R.layout.artist_details, this.mLinearLayout, false);
        SetImage.setImage(getActivity(), (ImageView) header.findViewById(R.id.artistImage), this.artist.pictureUrl);
        ((TextView)header.findViewById(R.id.artistName)).setText(artist.name);
        try {
            List<Genre> genreList = this.artist.genres();
            ((TextView) header.findViewById(R.id.artistGenres)).setText(concatenate(genreList, SEPARATOR));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.mLinearLayout.addView(header);
    }

    private void addDescription() {
        addtitle(getResources().getString(R.string.artist_details_descrition));

        View text = getActivity().getLayoutInflater().inflate(R.layout.list_text, this.mLinearLayout, false);
        ((TextView)text.findViewById(R.id.textText)).setText(Html.fromHtml(this.artist.description));
        this.mLinearLayout.addView(text);
    }

    private void addAlbums() {
        try {
            List<Album> albumList = artist.albums();
            if (!albumList.isEmpty()) {
                addtitle(getResources().getString(R.string.album_list_title));
            }
            for (Album album : albumList){
                addAlbum(album);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAlbum(Album album) {
        View albumView = getActivity().getLayoutInflater().inflate(R.layout.album_thumb_card, this.mLinearLayout, false);
        SetImage.setImage(getActivity(), (ImageView) albumView.findViewById(R.id.albumImage), album.pictureUrl);
        ((TextView)albumView.findViewById(R.id.albumName)).setText(album.title);
        this.mLinearLayout.addView(albumView);
    }

    private void addtitle(String titleText) {
        View title = getActivity().getLayoutInflater().inflate(R.layout.list_title, null, false);
        ((TextView)title.findViewById(R.id.titleText)).setText(titleText);
        this.mLinearLayout.addView(title);
    }

    public static String concatenate(List<Genre> list, String separator) {
        StringBuffer result = new StringBuffer();
        for (Genre genre : list) {
            result.append( genre.name );
            result.append( separator );
        }
        return result.delete(result.length()-separator.length(), result.length()).toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
