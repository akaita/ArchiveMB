package com.akaita.fda;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View mView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_main, container, false);

        fillGridRecyclerView();
        return this.mView;
    }

    private void fillGridRecyclerView(){
        final int COLUMNS = 2;
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
// in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

//        recyclerView.addItemDecoration(new MarginDecoration(this));

//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(COLUMNS,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new ArtistAdapter(getActivity()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int[] lastVisiblesItems = ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPositions(null);

                boolean loading = true;
                if (loading) {
                    if ((lastVisiblesItems[0] + COLUMNS) >= totalItemCount) {
                        loading = false;
                        Log.e("...", "Last Item Wow !");
                    }
                }
            }
        });
    }
}
