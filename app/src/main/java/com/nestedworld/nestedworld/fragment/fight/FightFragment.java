package com.nestedworld.nestedworld.fragment.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

public class FightFragment extends BaseFragment{

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FightFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_fight;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }
}
