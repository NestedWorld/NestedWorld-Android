package com.nestedworld.nestedworld.ui.view.fight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import butterknife.BindView;

public class FightProcessActivity extends BaseAppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.acitivity_fight_process;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        setUpToolbar();
        FightListFragment.load(getSupportFragmentManager());
    }

    /*
    ** Utils
    */
    private void setUpToolbar() {
        //Set the toolbar as actionBar
        setSupportActionBar(toolbar);

        //Get back the Toolbar as actionBar and then custom it
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //customise the actionBar
            actionBar.setTitle(getString(R.string.mainMenu_action_fight));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
