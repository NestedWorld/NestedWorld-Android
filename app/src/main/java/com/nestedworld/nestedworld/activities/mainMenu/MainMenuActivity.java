package com.nestedworld.nestedworld.activities.mainMenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.nestedworld.nestedworld.activities.profil.ProfileActivity;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.HomeFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.MapFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.MonstersFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.ShopFragment;
import com.nestedworld.nestedworld.fragments.mainMenu.tabs.ToolsFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit.Response;
import retrofit.Retrofit;

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
        updateUserInformation();
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

    /*
    ** Utils
     */
    private void setUpToolbar() {
        setSupportActionBar(toolbar);
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
    }

    private void updateUserInformation() {
        NestedWorldHttpApi.getInstance(this).getUserInfo(new Callback<UserResponse>() {
            @Override
            public void onSuccess(Response<UserResponse> response, Retrofit retrofit) {
                //store user information under userManager
                UserManager.get(MainMenuActivity.this).setUserData(MainMenuActivity.this, response.body().user);
                progressView.stop();

                initTabs();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserResponse> response) {
                //stop loading animation
                progressView.stop();

                //display error message
                String errorMessage = RetrofitErrorHandler.getErrorMessage(MainMenuActivity.this, errorKind, getString(R.string.error_update_user_info), response);
                Toast.makeText(MainMenuActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                //remove user
                UserManager.get(MainMenuActivity.this).deleteCurrentAccount(MainMenuActivity.this);

                //avoid leek with the static instance
                NestedWorldHttpApi.reset();

                //go to launch screen & kill current activity
                startActivity(RegistrationActivity.class);
                MainMenuActivity.this.finish();
            }
        });
    }

    /**
     * Custom FragmentPagerAdapter
     * It's use for displaying the TABS under activity.mainMenu
     */
    private class TabsAdapter extends FragmentPagerAdapter {
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
