package com.nestedworld.nestedworld.ui.mainMenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.FragmentStatePager.TabsAdapter;
import com.nestedworld.nestedworld.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.database.updater.AttacksUpdater;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.MonsterUpdater;
import com.nestedworld.nestedworld.database.updater.ShopItemsUpdater;
import com.nestedworld.nestedworld.database.updater.UserItemUpdater;
import com.nestedworld.nestedworld.database.updater.UserMonsterUpdater;
import com.nestedworld.nestedworld.database.updater.UserUpdater;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.application.ApplicationHelper;
import com.nestedworld.nestedworld.helpers.drawable.DrawableHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.chat.FriendListFragment;
import com.nestedworld.nestedworld.ui.fight.FightProcessActivity;
import com.nestedworld.nestedworld.ui.launch.LaunchActivity;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.home.HomeFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.inventory.UserInventoryFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.map.MapFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.monster.MonstersFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.shop.ShopFragment;
import com.nestedworld.nestedworld.ui.profil.ProfileActivity;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainMenuActivity extends BaseAppCompatActivity {

    private final List<Tabs> mTabs = new ArrayList<Tabs>() {{
        add(new Tabs(new HomeFragment(), "Home", R.drawable.ic_home_24dp));
        add(new Tabs(new MonstersFragment(), "Monsters", R.drawable.ic_monster_24dp));
        add(new Tabs(new MapFragment(), "Map", R.drawable.ic_map_24dp));
        add(new Tabs(new UserInventoryFragment(), "Inventory", R.drawable.ic_inventory_24dp));
        add(new Tabs(new ShopFragment(), "Shop", R.drawable.ic_shop_24dp));
    }};
    private final List<EntityUpdater> mTasks = new ArrayList<EntityUpdater>() {{
        add(new FriendsUpdater());
        add(new AttacksUpdater());
        add(new MonsterUpdater());
        add(new UserMonsterUpdater());
        add(new ShopItemsUpdater());
        add(new UserItemUpdater());
    }};
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.progressView)
    ProgressView progressView;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_menu;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setUpToolbar();
        setupViewPager();
        setupTabLayout();
        initSocketService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //We want to redraw the toolbar
        invalidateOptionsMenu();

        updateDataBase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Retrieve widget
        MenuItem menuItem = menu.findItem(R.id.action_fight);

        //Calculate the number of fight and update icon
        //we don't have to check for 0 (see buildCounterDrawable())
        int numberOfFight = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getCombatDao()
                .loadAll()
                .size();
        menuItem.setIcon(DrawableHelper.buildCounterDrawable(this, numberOfFight, R.drawable.ic_swords_cross_24dp));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(ProfileActivity.class);
                return true;
            case R.id.action_chat:
                handleChatClick();
                return true;
            case R.id.action_fight:
                startActivity(FightProcessActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSocketService();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {

            fragmentManager.popBackStackImmediate();

            if (fragmentManager.getBackStackEntryCount() == 0) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setHomeButtonEnabled(false);
                    actionBar.setTitle(R.string.app_name);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onNewCombatAvailable(OnAvailableMessageEvent event) {
        //We want to redraw the toolbar
        invalidateOptionsMenu();
    }

    /*
    ** private method
     */
    private void handleChatClick() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof FriendListFragment) {
                    onBackPressed();
                    return;
                }
            }
        }
        FriendListFragment.load(getSupportFragmentManager());
    }

    private void initSocketService() {
        //Start the service
        ServiceHelper.startSocketService(this);
    }

    private void stopSocketService() {
        //Stop the service
        ServiceHelper.stopSocketService(this);
    }

    private void setupViewPager() {
        //Create adapter
        TabsAdapter viewPagerAdapter = new TabsAdapter(getSupportFragmentManager()).setDisplayPageTitle(false);

        //Populate adapter
        for (Tabs tabs : mTabs) {
            viewPagerAdapter.addFragment(tabs.fragment, tabs.title);
        }

        //Set adapter
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        //add listener for toolbar.name customisation
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Update toolbar title
                ActionBar actionBar = getSupportActionBar();
                Tabs selectedTabs = mTabs.get(tab.getPosition());

                if (actionBar != null && selectedTabs != null) {
                    actionBar.setTitle(selectedTabs.title);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);

        //Display tab icon
        for (int i = 0; i < mTabs.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(mTabs.get(i).icon);
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    /**
     * Simple asyncTask implementation for updating the database
     */
    private void updateDataBase() {
        //Start loading animation
        progressView.start();

        new UserUpdater().start(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                //We use run() method for convenience
                //for being thread safe, make request in asyncTask
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        for (EntityUpdater entityUpdater : mTasks) {
                            if (!entityUpdater.run()) {
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        //Check if activity hasn't been destroy
                        if (!isUiBinded()) {
                            return;
                        }

                        //stop loading animation
                        progressView.stop();

                        if (!result) {
                            //display error message
                            Toast.makeText(MainMenuActivity.this, getString(R.string.error_database_update), Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //display error message
                Toast.makeText(MainMenuActivity.this, getString(R.string.error_request_user), Toast.LENGTH_LONG).show();

                ApplicationHelper.logout(MainMenuActivity.this);

                //Go to launch screen
                startActivity(LaunchActivity.class);

                ///Finish current activity
                finish();
            }
        });
    }

    private final static class Tabs {
        @NonNull
        public final Fragment fragment;

        @Nullable
        public final String title;

        @DrawableRes
        public final int icon;

        public Tabs(@NonNull final Fragment fragment, @Nullable final String title, @DrawableRes final int icon) {
            this.icon = icon;
            this.fragment = fragment;
            this.title = title;
        }
    }
}
