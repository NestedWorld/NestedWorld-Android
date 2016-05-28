package com.nestedworld.nestedworld.activities.mainMenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activities.chat.ChatActivity;
import com.nestedworld.nestedworld.activities.fight.FightActivity;
import com.nestedworld.nestedworld.activities.launch.LaunchActivity;
import com.nestedworld.nestedworld.activities.profil.ProfileActivity;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.HomeFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.MapFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.MonstersFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.ShopFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.ToolsFragment;
import com.nestedworld.nestedworld.helpers.database.updater.entity.EntityUpdater;
import com.nestedworld.nestedworld.helpers.database.updater.entity.FriendsUpdater;
import com.nestedworld.nestedworld.helpers.database.updater.entity.MonsterUpdater;
import com.nestedworld.nestedworld.helpers.database.updater.entity.UserMonsterUpdater;
import com.nestedworld.nestedworld.helpers.database.updater.entity.UserUpdater;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;

public class MainMenuActivity extends BaseAppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.progressView)
    ProgressView progressView;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_menu;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setUpToolbar();

        progressView.start();
        updateDataBase();
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
            case R.id.action_chat:
                startActivity(ChatActivity.class);
                return true;
            case R.id.action_fight:
                startActivity(FightActivity.class);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initTabs() {
        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.addFragment(getString(R.string.tab_home), new HomeFragment(), R.drawable.account_balance);
        adapter.addFragment(getString(R.string.tab_garden), new ToolsFragment(), R.drawable.ic_action_sword);
        adapter.addFragment(getString(R.string.tab_map), new MapFragment(), R.drawable.ic_action_map);
        adapter.addFragment(getString(R.string.tab_monster), new MonstersFragment(), R.drawable.ic_action_ghost);
        adapter.addFragment(getString(R.string.tab_shop), new ShopFragment(), R.drawable.ic_action_cart);

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

        progressView.stop();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }


    /**
     * Simple asyncTask implementation for updating the database
     */
    private void updateDataBase() {
        final AtomicInteger taskEnded = new AtomicInteger(0);
        final List<Thread> tasks = new ArrayList<>();
        final EntityUpdater.onEntityUpdated callback = new EntityUpdater.onEntityUpdated() {
            @Override
            public void onSuccess() {
                if (taskEnded.incrementAndGet() == tasks.size()) {
                    initTabs();
                }
            }

            @Override
            public void onError() {
                //Stop every thread
                for (Thread t : tasks) {
                    t.interrupt();
                }

                //stop loading animation
                progressView.stop();

                //display error message
                Toast.makeText(MainMenuActivity.this, getString(R.string.error_update_user_info), Toast.LENGTH_LONG).show();

                //remove user
                SessionManager.get().deleteSession();

                //Go to launch screen
                startActivity(LaunchActivity.class);

                ///Finish current activity
                finish();
            }
        };

        tasks.add(new UserUpdater(MainMenuActivity.this, callback));
        tasks.add(new FriendsUpdater(MainMenuActivity.this, callback));
        tasks.add(new MonsterUpdater(MainMenuActivity.this, new EntityUpdater.onEntityUpdated() {
            @Override
            public void onSuccess() {
                Thread thread = new UserMonsterUpdater(MainMenuActivity.this, callback);
                tasks.add(thread);
                thread.start();

                callback.onSuccess();
            }

            @Override
            public void onError() {
                callback.onError();
            }
        }));

        for (Thread t : tasks) {
            t.start();
        }
    }

    /**
     * Custom FragmentPagerAdapter
     * It's use for displaying the TABS under activity.mainMenu
     */
    private static class TabsAdapter extends FragmentPagerAdapter {
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
}
