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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom FragmentPagerAdapter
 * It's use for displaying the TABS under activity.mainMenu
 */
public class TabsAdapter extends FragmentPagerAdapter {
    protected final String TAG = getClass().getSimpleName();

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

        final CustomTab tab = tabList.get(position);

        Drawable drawable = ContextCompat.getDrawable(mContext, tab.getIcon());
        Log.d(TAG, "" + drawable.getIntrinsicWidth());
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        SpannableString sb = new SpannableString("   " + tab.getTitle());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
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
