package com.akaita.fda;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.Artist;
import com.akaita.fda.database.RangedQuery;

import java.sql.SQLException;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment implements ArtistAdapter.OnArtistItemSelectedListener{
    public static final String EXTRA_GENRE_ID = "genre";

    final static float COLUMN_SIZE_INCHES = 1;
    final static int PAGE_SIZE = 10;

    OnArtistSelectedListener mCallback;

    private View mView;
    private ArtistAdapter artistAdapter;
    private String mGenreId;

    public ArtistFragment() {
    }

    public interface OnArtistSelectedListener {
        public void onArtistSelected(Artist artist);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_artist, container, false);

        this.artistAdapter = new ArtistAdapter(getActivity());
        this.artistAdapter.setOnArtistItemSelectedListener(this);

        this.mGenreId = getChosenGenre();
        prepareGridRecyclerView();
        loadFirstItemBatch();

        return this.mView;
    }

    private String getChosenGenre() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getString(EXTRA_GENRE_ID);
        } else {
            return null;
        }
    }

    private void prepareGridRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerVie
        recyclerView.setHasFixedSize(true);

        // recyclerView.addItemDecoration(new MarginDecoration(this));

        int columns = JavaUtils.getColumns(getActivity(), COLUMN_SIZE_INCHES);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(this.artistAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int[] lastVisibleItem = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(null);

                if ((JavaUtils.findMax(lastVisibleItem) + 1) == totalItemCount) {
                    loadMoreItems();
                }
            }
        });
    }

    private void loadFirstItemBatch() {
        loadMoreItems();
    }

    private void loadMoreItems() {
        List<Artist> newArtistList = null;
        try {
            newArtistList = RangedQuery.getArtistRangeByGenre(artistAdapter.getItemCount(), PAGE_SIZE, this.mGenreId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Artist artist : newArtistList){
            artistAdapter.add(artist, artistAdapter.getItemCount());
        }
    }

    @Override
    public void onArtistItemSelected(Artist artist) {
        mCallback.onArtistSelected(artist);
    }

}
