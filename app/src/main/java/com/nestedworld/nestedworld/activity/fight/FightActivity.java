package com.nestedworld.nestedworld.activity.fight;

import android.os.Bundle;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.fragment.fight.FightFragment;

public class FightActivity extends BaseAppCompatActivity{


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
        FightFragment.load(getSupportFragmentManager());
    }
}
