package com.nestedworld.nestedworld.activity.mainMenu;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activity.profil.ProfileActivity;
import com.nestedworld.nestedworld.adapter.TabsAdapter;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.HomeFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MapFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MonstersFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.ShopFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.ToolsFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;

public class MainMenuActivity extends BaseAppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

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
        setUpToolbar();
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
        //TODO use good icon
        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(getString(R.string.tab_home), new HomeFragment(), R.drawable.ic_cast_light);
        adapter.addFragment(getString(R.string.tab_tools), new ToolsFragment(), R.drawable.ic_cast_light);
        adapter.addFragment(getString(R.string.tab_map), new MapFragment(), R.drawable.ic_cast_light);
        adapter.addFragment(getString(R.string.tab_monster), new MonstersFragment(), R.drawable.ic_cast_light);
        adapter.addFragment(getString(R.string.tab_shop), new ShopFragment(), R.drawable.ic_cast_light);

        viewPager.setAdapter(adapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                startActivity(ProfileActivity.class);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /*
    ** Utils
     */
    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }
}
