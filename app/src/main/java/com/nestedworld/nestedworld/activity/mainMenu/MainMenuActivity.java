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

import com.google.gson.Gson;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activity.launch.LaunchActivity;
import com.nestedworld.nestedworld.activity.profil.ProfileActivity;
import com.nestedworld.nestedworld.adapter.TabsAdapter;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.UserResponse;
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
            case R.id.action_chat:
                //TODO faire la vue et l'afficher
                Toast.makeText(MainMenuActivity.this, "Incoming...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_fight:
                //TODO faire la vue et l'afficher
                Toast.makeText(MainMenuActivity.this, "Incoming...", Toast.LENGTH_LONG).show();
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
        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(getString(R.string.tab_home), new HomeFragment(), R.drawable.account_balance);
        adapter.addFragment(getString(R.string.tab_garden), new ToolsFragment(), R.drawable.ic_action_sword);
        adapter.addFragment(getString(R.string.tab_map), new MapFragment(),R.drawable.ic_action_map);
        adapter.addFragment(getString(R.string.tab_monster), new MonstersFragment(), R.drawable.ic_action_ghost);
        adapter.addFragment(getString(R.string.tab_shop), new ShopFragment(), R.drawable.ic_action_cart);

        viewPager.setAdapter(adapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);

        //Display tab icon
        //We loop over the adapter instead of calling setIcon() manually
        for (int i=0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(adapter.getPageIcon(i));
            }
        }
    }

    private void updateUserInformation() {
        progressView.start();

        NestedWorldApi.getInstance(this).getUserInfo(new com.nestedworld.nestedworld.api.callback.Callback<UserResponse>() {
            @Override
            public void onSuccess(Response<UserResponse> response, Retrofit retrofit) {
                /*We convert the response as a String and then we store it*/

                final String json = new Gson().toJson(response.body().data);

                UserManager.get(MainMenuActivity.this).setUserData(MainMenuActivity.this, json);

                /*We display the tabs*/
                initTabs();
                progressView.stop();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserResponse> response) {

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
