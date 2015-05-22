package com.akaita.fda;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    final static int COLUMNS = 2;
    final static int PAGE_SIZE = 10;

    private View mView;
    private ArtistAdapter artistAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_main, container, false);

        this.artistAdapter = new ArtistAdapter(getActivity());

        prepareGridRecyclerView();
        loadFirstItemBatch();
        return this.mView;
    }

    private void prepareGridRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerVie
        recyclerView.setHasFixedSize(true);

        // recyclerView.addItemDecoration(new MarginDecoration(this));

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(this.artistAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int[] lastVisiblesItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null);

                if ((lastVisiblesItems[0] + COLUMNS) >= totalItemCount) {
                    loadMoreItems();
                    Log.e("temp", "last element");
                }
            }
        });
    }

    private void loadFirstItemBatch() {
        loadMoreItems();
    }

    private void loadMoreItems() {
        try {
            artistAdapter.loadMore(PAGE_SIZE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
