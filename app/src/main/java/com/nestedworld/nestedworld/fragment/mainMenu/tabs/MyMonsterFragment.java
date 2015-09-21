package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

public class MyMonsterFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MyMonsterFragment.class.getSimpleName();

    public static void load(final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.container, new MyMonsterFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_monster;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }
}
