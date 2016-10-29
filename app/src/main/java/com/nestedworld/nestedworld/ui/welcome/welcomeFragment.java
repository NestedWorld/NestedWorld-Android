package com.nestedworld.nestedworld.ui.welcome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;

import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeFragment extends BaseFragment {

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new WelcomeFragment())
                .commit();
    }

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_welcome;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Do your job here
    }

    /*
    ** ButterKnife callback
     */
    @OnClick(R.id.button_login)
    public void login() {LoginFragment.load(((BaseAppCompatActivity) mContext).getSupportFragmentManager());
    }

    @OnClick(R.id.button_inscription)
    public void createAccount() {
        CreateAccountFragment.load(((BaseAppCompatActivity) mContext).getSupportFragmentManager());
    }
}
