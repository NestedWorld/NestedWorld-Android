package com.nestedworld.nestedworld.fragment.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class LaunchFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = CreateAccountFragment.class.getSimpleName();

    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final Boolean toBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new LaunchFragment());
        if (toBackStack) {
            fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        }
        fragmentTransaction.commit();
    }

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_launch;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife
     */
    @OnClick(R.id.button_login)
    public void login() {
        LoginFragment.load(getFragmentManager());
    }

    @OnClick(R.id.button_inscription)
    public void createAccount() {
        CreateAccountFragment.load(getFragmentManager());
    }
}
