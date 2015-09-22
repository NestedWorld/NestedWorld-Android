package com.nestedworld.nestedworld.activity.profil;

import android.os.Bundle;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.customWidget.typeface.TypefaceToolbar;
import com.nestedworld.nestedworld.fragment.profil.ProfileFragment;

import butterknife.Bind;

public class ProfileActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    TypefaceToolbar toolbar;

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty_with_toolbar;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        setUpToolbar();
        ProfileFragment.load(getSupportFragmentManager(), false);
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }

    /*
    ** Utils
     */
    private void setUpToolbar() {
        toolbar.setTitle(getString(R.string.menu_ation_profil));
        toolbar.setNavigationIcon(R.drawable.ic_navigation_chevron_left_green);
        setSupportActionBar(toolbar);
    }
}
