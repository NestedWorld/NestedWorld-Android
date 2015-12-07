package com.nestedworld.nestedworld.activity.mainMenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activity.launch.LaunchActivity;
import com.nestedworld.nestedworld.activity.profil.ProfileActivity;
import com.nestedworld.nestedworld.adapter.TabsAdapter;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.User;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.HomeFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MapFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.MonstersFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.ShopFragment;
import com.nestedworld.nestedworld.fragment.mainMenu.tabs.ToolsFragment;
import com.rey.material.widget.ProgressView;

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
    protected void initUI(Bundle savedInstanceState) {
        setUpToolbar();
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
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

    private void updateUserInformation() {
        progressView.start();


        NestedWorldApi.getInstance(this).getUserInfo(new com.nestedworld.nestedworld.api.callback.Callback<User>() {
            @Override
            public void onSuccess(Response<User> response, Retrofit retrofit) {
                initTabs();
                progressView.stop();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<User> response) {

                Toast.makeText(MainMenuActivity.this,
                        RetrofitErrorHandler.getErrorMessage(MainMenuActivity.this, errorKind, getString(R.string.error_update_user_info), response),
                        Toast.LENGTH_LONG).show();

                //remove user
                UserManager.get(MainMenuActivity.this).deleteCurrentAccount(MainMenuActivity.this);

                //go to launch screen & kill the current context
                startActivity(LaunchActivity.class);
                MainMenuActivity.this.finish();
            }
        });
    }
}
