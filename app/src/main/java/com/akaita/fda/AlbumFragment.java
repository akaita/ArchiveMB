package com.akaita.fda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akaita.fda.database.Album;
import com.akaita.fda.database.Artist;
import com.akaita.fda.database.DaoFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 25/05/2015.
 */
public class AlbumFragment extends Fragment {
    public static final String ARTIST_ID = "artistId";

    private Artist artist;
    private AlbumAdapter albumAdapter;
    private View mView;

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_album, container, false);

        this.artist = getChosenArtist();
        this.albumAdapter = new AlbumAdapter(getActivity());

        prepareGridRecyclerView();
        loadItems();

        SetImage.setImage(getActivity(), (ImageView) mView.findViewById(R.id.albumImage), artist.pictureUrl);
        ((TextView)mView.findViewById(R.id.artistName)).setText(artist.name);
        ((TextView)mView.findViewById(R.id.artistDescription)).setText(artist.description);

        return this.mView;
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
}
