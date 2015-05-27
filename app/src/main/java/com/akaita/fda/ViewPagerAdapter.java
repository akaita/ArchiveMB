package com.akaita.fda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String mNames[];

    public ViewPagerAdapter(FragmentManager fm, String mTitles[]) {
        super(fm);
        this.mNames = mTitles;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            return new ArtistFragment();
        } else {
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