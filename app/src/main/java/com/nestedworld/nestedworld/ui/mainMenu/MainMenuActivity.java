package com.nestedworld.nestedworld.ui.mainMenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.database.updater.AttacksUpdater;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.MonsterUpdater;
import com.nestedworld.nestedworld.database.updater.ShopItemsUpdater;
import com.nestedworld.nestedworld.database.updater.UserMonsterUpdater;
import com.nestedworld.nestedworld.database.updater.UserUpdater;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.application.ApplicationHelper;
import com.nestedworld.nestedworld.helpers.drawable.DrawableHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
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
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainMenuActivity extends BaseAppCompatActivity {
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
        initSocketService();
        initTabs();
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
        int numberOfFight = Select.from(Combat.class).list().size();
        menuItem.setIcon(DrawableHelper.buildCounterDrawable(this, numberOfFight, R.drawable.ic_sword_white_18dp));
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

    /*
    ** EventBus
     */
    @Subscribe
    public void onNewCombatAvailable(OnAvailableMessageEvent event) {
        //We want to redraw the toolbar
        invalidateOptionsMenu();
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

    private void initTabs() {
        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.addFragment("", new HomeFragment(), R.drawable.ic_home_white_18dp);
        adapter.addFragment("", new MonstersFragment(), R.drawable.ic_ghost_white_18dp);
        adapter.addFragment("", new MapFragment(), R.drawable.ic_map_marker_radius_white_18dp);
        adapter.addFragment("", new UserInventoryFragment(), R.drawable.ic_sword_white_18dp);
        adapter.addFragment("", new ShopFragment(), R.drawable.ic_cart_plus_white_18dp);

        viewPager.setAdapter(adapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);

        //Display tab icon
        //We loop over the adapter instead of calling setIcon() manually
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(adapter.getPageIcon(i));
            }
        }

        //Stop loading animation
        progressView.stop();
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

        final List<EntityUpdater> tasks = new ArrayList<>();
        tasks.add(new UserUpdater());
        tasks.add(new FriendsUpdater());
        tasks.add(new AttacksUpdater());
        tasks.add(new MonsterUpdater());
        tasks.add(new UserMonsterUpdater());//Always update userMonster after monster (for fk issue)
        tasks.add(new ShopItemsUpdater());

        //We use run() method for convenience
        //for being thread safe, make request in asyncTask
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                for (EntityUpdater entityUpdater : tasks) {
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

                if (!result){
                    //display error message
                    Toast.makeText(MainMenuActivity.this, getString(R.string.error_request_user), Toast.LENGTH_LONG).show();

                    ApplicationHelper.logout(MainMenuActivity.this);

                    //Go to launch screen
                    startActivity(LaunchActivity.class);

                    ///Finish current activity
                    finish();
                }
            }
        }.execute();
    }

    /**
     * Custom FragmentPagerAdapter
     * It's use for displaying the TABS under activity.mainMenu
     */
    private static class TabsAdapter extends FragmentStatePagerAdapter {
        protected final String TAG = getClass().getSimpleName();

        private final List<CustomTab> tabList = new ArrayList<>();

        /*
        ** Constructor
         */
        public TabsAdapter(@NonNull final FragmentManager fm) {
            super(fm);
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
            return tabList.get(position).getTitle();
        }

        public int getPageIcon(int position) {
            return tabList.get(position).getIcon();
        }

        /**
         * Custom class for easy tab management
         */
        public static class CustomTab {
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
}
