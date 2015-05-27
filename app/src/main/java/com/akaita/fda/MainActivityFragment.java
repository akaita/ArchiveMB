package com.akaita.fda;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.objects.Genre;

import java.sql.SQLException;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private View mView;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] tabList = new String[]{"All"};
        try {
            List<Genre> genreList = DaoFactory.getInstance().getGenreDao().queryForAll();
            tabList = new String[genreList.size()+1];
            tabList[0] = "All";
            for (int i=0 ; i<genreList.size() ; i++){
                tabList[i+1] = genreList.get(i).getName();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creating The ViewPagerAdapter and Passing Fragment Manager, mNames fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getChildFragmentManager(),tabList);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) this.mView.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) this.mView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        return this.mView;
    }
}
