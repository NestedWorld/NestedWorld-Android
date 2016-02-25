package com.nestedworld.nestedworld.activity.fight;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.fragment.fight.FightFragment;

import butterknife.Bind;

public class FightActivity extends BaseAppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty_with_toolbar;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setUpToolbar();
        FightFragment.load(getSupportFragmentManager());
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
            actionBar.setTitle(getString(R.string.menu_action_fight));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
