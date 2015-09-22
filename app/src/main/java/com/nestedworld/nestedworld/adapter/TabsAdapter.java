package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MyCityFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MyCountryFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MyMonsterFragment;

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
    public TabsAdapter(final FragmentManager fm, final Context context) {
        super(fm);

        mContext = context;

        //init tabsList
        //TODO use good icon
        tabList.add(new CustomTab(mContext.getString(R.string.tab_village_name), new MyCityFragment(), R.drawable.ic_cast_dark));
        tabList.add(new CustomTab(mContext.getString(R.string.tab_monster_name), new MyMonsterFragment(), R.drawable.ic_cast_dark));
        tabList.add(new CustomTab(mContext.getString(R.string.tab_city_name), new MyCountryFragment(), R.drawable.ic_cast_dark));
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

        // Generate title based on item position
        Drawable image = ContextCompat.getDrawable(mContext, tabList.get(position).getIcon());
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());

        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabList.get(position).getTitle());
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    /*
    ** Custom class for easy tab management
     */
    private class CustomTab {
        private final Fragment mFragment;
        private final int mIcon;
        private String mTitle = "";

        public CustomTab(final String title, final Fragment fragment, final int icon) {
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
