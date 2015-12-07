package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom FragmentPagerAdapter
 * It's use for displaying the TABS under activity.mainMenu
 */
public class TabsAdapter extends FragmentPagerAdapter {
    private final List<CustomTab> tabList = new ArrayList<>();
    private final Context mContext;

    /*
    ** Constructor
     */
    public TabsAdapter(@NonNull final FragmentManager fm, @NonNull final Context context) {
        super(fm);

        mContext = context;
    }

    /*
    ** Public method
     */
    public void addFragment(@NonNull final String title, @NonNull final Fragment fragment, final int icon) {
        tabList.add(new CustomTab(title, fragment, icon));
    }

    /*
    ** Parents method
     */
    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO display icon
        return tabList.get(position).getTitle();
    }

    /**
     * Custom class for easy tab management
     */
    public class CustomTab {
        private final Fragment mFragment;
        private final int mIcon;
        private String mTitle = "";

        public CustomTab(@NonNull final String title, @NonNull final Fragment fragment, final int icon) {
            mTitle = title;
            mFragment = fragment;
            mIcon = icon;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getIcon() {
            return mIcon;
        }
    }
}
