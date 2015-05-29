package com.akaita.fda;

/**
 * Created by mikel on 20/05/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akaita.fda.database.objects.Artist;
import com.akaita.fda.database.RangedQuery;
import com.akaita.fda.update.UpdateDatabaseTask;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment implements ArtistAdapter.OnArtistItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, UpdateDatabaseTask.OnUpdateDatabaseFinishListener {
    public static final String EXTRA_GENRE_ID = "genre";

    final static float COLUMN_SIZE_INCHES = 1;
    final static ArtistAdapter.ViewType mViewType = ArtistAdapter.ViewType.THUMB;

    private OnArtistSelectedListener mOnArtistSelectedListener;
    public OnArtistListUpdatedListener mOnArtistListUpdatedListener;

    private View mView;
    private ArtistAdapter mArtistAdapter;
    private String mGenreId;
    private SwipeRefreshLayout mSwipeLayout;
    private String mArtistNameFilter;

    public ArtistFragment() {
    }

    public interface OnArtistListUpdatedListener {
        void onArtistListUpdated();
    }

    public interface OnArtistSelectedListener {
        void onArtistSelected(Artist artist);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            this.mOnArtistSelectedListener = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistSelectedListener");
        }
        try {
            this.mOnArtistListUpdatedListener = (OnArtistListUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistListUpdatedListener");
        }
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_artists, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                setNewArtistNameFilter(arg0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                setNewArtistNameFilter(arg0);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_artist, container, false);

        this.mArtistAdapter = new ArtistAdapter(getActivity());
        this.mArtistAdapter.setOnArtistItemSelectedListener(this);
        this.mArtistAdapter.setViewType(this.mViewType);

        this.mGenreId = getChosenGenre();
        prepareGridRecyclerView();
        loadFirstItemBatch();

        setSwipeRefreshListener();

        setHasOptionsMenu(true);

        return this.mView;
    }

    private void setSwipeRefreshListener() {
        this.mSwipeLayout = (SwipeRefreshLayout) this.mView.findViewById(R.id.swipe_container);
        this.mSwipeLayout.setOnRefreshListener(this);
        this.mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private String getChosenGenre() {
        String genre = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            genre = bundle.getString(EXTRA_GENRE_ID);
        }
        Log.d(getClass().toString(), "Detected genre filter: " + genre);
        return genre;
    }

    private void prepareGridRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerVie
        recyclerView.setHasFixedSize(true);

        // recyclerView.addItemDecoration(new MarginDecoration(this));

        int columns = JavaUtils.getColumns(getActivity(), COLUMN_SIZE_INCHES);

        switch (mViewType){
            case THUMB:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));
                break;
            case LIST:
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
        }
        recyclerView.setAdapter(this.mArtistAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();

                int lastVisibleItem = 0;
                switch (mViewType) {
                    case THUMB:
                        lastVisibleItem = JavaUtils.findMax(
                                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(null));
                        break;
                    case LIST:
                        lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                }

                if ((lastVisibleItem + 1) == totalItemCount) {
                    Log.d(getClass().toString(), "Detected scroll end");
                    loadMoreItems();
                }
            }
        });
    }

    private void loadFirstItemBatch() {
        loadMoreItems();
    }

    private void loadMoreItems() {
        List<Artist> newArtistList = new ArrayList<>();
        try {
            PreferencesManager preferencesManager = new PreferencesManager(getActivity());
            if (this.mArtistNameFilter==null
                    || this.mArtistNameFilter.isEmpty()) {
                newArtistList = RangedQuery.getArtistRangeByGenre(mArtistAdapter.getItemCount(), preferencesManager.getPageSize(), this.mGenreId);
            } else {
                newArtistList = RangedQuery.getArtistRangeByGenreByName(mArtistAdapter.getItemCount(), preferencesManager.getPageSize(), this.mGenreId, this.mArtistNameFilter);
            }
        } catch (SQLException e) {
            Log.e(getClass().toString(), "SQLException (" + e.getSQLState() + "): " + e.getMessage());
        }
        for (Artist artist : newArtistList){
            this.mArtistAdapter.add(artist, mArtistAdapter.getItemCount());
        }
    }

    @Override
    public void onArtistItemSelected(Artist artist) {
        this.mOnArtistSelectedListener.onArtistSelected(artist);
    }

    @Override
    public void onRefresh() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            Log.i(getClass().toString(), "Internet access: NO");
            Toast.makeText(getActivity(), R.string.network_access_error, Toast.LENGTH_SHORT).show();
            finishRefreshUI();
            this.mSwipeLayout.setRefreshing(false);
        } else {
            Log.i(getClass().toString(), "Internet access: YES");
            URL url1 = null;
            try {
                PreferencesManager preferencesManager = new PreferencesManager(getActivity());
                url1 = new URL(preferencesManager.getDatabaseUrl());
                new UpdateDatabaseTask(this, preferencesManager).execute(url1);
            } catch (MalformedURLException e) {
                Log.e(getClass().toString(), "Malformed URL: " + e.getMessage());
                this.onUpdateDatabaseFinish(false);
            }
        }
    }

    @Override
    public void onUpdateDatabaseFinish(boolean newData) {
        finishRefreshUI();
        if (newData){
            this.mOnArtistListUpdatedListener.onArtistListUpdated();
        } else {
            Toast.makeText(getActivity(), R.string.no_new_version, Toast.LENGTH_SHORT).show();
        }
    }

    private void finishRefreshUI(){
        this.mSwipeLayout.setRefreshing(false);
    }

    private void setNewArtistNameFilter(String filter) {
        this.mArtistNameFilter = filter;
        this.mArtistAdapter.removeAll();
        loadFirstItemBatch();
    }
}
