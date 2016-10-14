package com.nestedworld.nestedworld.ui.welcome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.base.BaseFragment;

import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class welcomerFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = welcomerFragment.class.getSimpleName();

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final Boolean toBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new welcomerFragment());
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
        return R.layout.fragment_register;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Do your job here
    }

    /*
    ** ButterKnife callback
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
