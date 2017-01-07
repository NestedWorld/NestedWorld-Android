package com.nestedworld.nestedworld.ui.adapter.fragmentStatePager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentStatePagerAdapter {
    protected final String TAG = getClass().getSimpleName();

    private final List<ViewPagerItem> mTabs = new ArrayList<>();
    private boolean mDisplayPageTitle = true;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public TabsAdapter(@NonNull final FragmentManager fm) {
        super(fm);
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    @NonNull
    public TabsAdapter addFragment(@NonNull final Fragment fragment,
                                   @Nullable final String title) {
        mTabs.add(new ViewPagerItem(fragment, title));
        return this;
    }

    @NonNull
    public TabsAdapter setDisplayPageTitle(boolean displayPageTitle) {
        mDisplayPageTitle = displayPageTitle;
        return this;
    }

    /*
     * #############################################################################################
     * # FragmentStatePagerAdapter implementation
     * #############################################################################################
     */
    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    @Nullable
    public CharSequence getPageTitle(int position) {
        if (mDisplayPageTitle) {
            return mTabs.get(position).getTitle();
        }
        return null;
    }

    /**
     * Custom class for easy tab management
     */
    public static class ViewPagerItem {
        private final Fragment mFragment;
        private String mTitle = null;

        public ViewPagerItem(@NonNull final Fragment fragment,
                             @Nullable final String title) {
            mTitle = title;
            mFragment = fragment;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getTitle() {
            return mTitle;
        }
    }

}