package com.nestedworld.nestedworld.activity.mainMenu;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseFragmentActivty;
import com.nestedworld.nestedworld.adapter.TabsAdapter;

import butterknife.Bind;

public class MainMenuActivity extends BaseFragmentActivty {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_menu;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogics(Bundle savedInstanceState) {
        //Init view pager
        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), mContext);
        viewPager.setAdapter(adapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);
    }
}
