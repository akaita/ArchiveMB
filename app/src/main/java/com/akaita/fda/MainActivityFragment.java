package com.akaita.fda;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.objects.Genre;

import java.sql.SQLException;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private View mView;

    ViewPager mPager;
    ViewPagerAdapter mAdapter;
    SlidingTabLayout mTabs;

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

        setSlidingTabs();

        return this.mView;
    }

    private void setSlidingTabs() {
        String[] tabList = getTabList();
        this.mAdapter = createTabAdapter(tabList);
        this.mPager = setTabPager(this.mAdapter, this.mView);
        configureTabs(this.mView, this.mPager);
    }

    private String[] getTabList() {
        String[] tabList = new String[]{getResources().getString(R.string.genre_all)};
        try {
            List<Genre> genreList = DaoFactory.getInstance().getGenreDao().queryForAll();
            tabList = new String[genreList.size()+1];
            tabList[0] = getResources().getString(R.string.genre_all);
            for (int i=0 ; i<genreList.size() ; i++){
                tabList[i+1] = genreList.get(i).getName();
            }
        } catch (SQLException e) {
            Log.e(getClass().toString(), "SQLException (" + e.getSQLState() + "): " + e.getMessage());
        }

        Log.d(getClass().toString(), "Found tags count: " + String.valueOf(tabList.length));
        return tabList;
    }

    private ViewPagerAdapter createTabAdapter(String[] tabList) {
        return new ViewPagerAdapter(getChildFragmentManager(),tabList);
    }

    private ViewPager setTabPager(ViewPagerAdapter adapter, View view) {
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        return pager;
    }

    private void configureTabs(View view, ViewPager pager) {
        // Assiging the Sliding Tab Layout View
        this.mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        this.mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the mTabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        this.mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        this.mTabs.setViewPager(pager);
    }

    public void updateSlidingTabs(){
        setSlidingTabs();
    }
}
