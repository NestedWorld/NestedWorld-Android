package com.nestedworld.nestedworld.fragment.launch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import butterknife.OnClick;

public class LaunchFragment extends BaseFragment {

    /*
    ** Utils
     */
    public static void load(final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new LaunchFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_launch;
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife
     */
    @OnClick(R.id.button_login)
    public void login(Button button) {
        LoginFragment.load(getFragmentManager());
    }

    @OnClick(R.id.button_inscription)
    public void createAccount() {
        CreateAccountFragment.load(getFragmentManager());
    }
}
