package com.nestedworld.nestedworld.ui.view.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import butterknife.BindView;

public class WelcomeActivity extends BaseAppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        setupActionBar();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        WelcomeFragment.load(getSupportFragmentManager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackStackChanged() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    /*
    ** Internal method
     */
    private void setupActionBar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //Setup back navigation
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            //Remove title
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
}