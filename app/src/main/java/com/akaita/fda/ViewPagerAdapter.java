package com.akaita.fda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String mNames[];

    public ViewPagerAdapter(FragmentManager fm, String names[]) {
        super(fm);
        this.mNames = names;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(getClass().toString(), "Selected tab: " + String.valueOf(position));

        if(position == 0)
        {
            Log.i(getClass().toString(), "Showing first tab");
            return new ArtistFragment();
        } else {
            Log.i(getClass().toString(), "Showing 'not-first' tab");
            Bundle bundle = new Bundle();
            bundle.putString(ArtistFragment.EXTRA_GENRE_ID, mNames[position]);

            // Create new fragment and transaction
            ArtistFragment newFragment = new ArtistFragment();
            newFragment.setArguments(bundle);

            return newFragment;
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNames[position];
    }

    @Override
    public int getCount() {
        return this.mNames.length;
    }
}