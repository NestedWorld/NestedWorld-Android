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
    protected void initVariable(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), mContext));
        tabLayout.setupWithViewPager(viewPager);
    }
}
