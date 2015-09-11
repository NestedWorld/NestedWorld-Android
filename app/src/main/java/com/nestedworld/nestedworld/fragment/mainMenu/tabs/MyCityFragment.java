package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

public class MyCityFragment extends BaseFragment {

    public static void load(final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.container, new MyCityFragment());
        fragmentTransaction.addToBackStack("MyCityFragment");
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_city;
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}