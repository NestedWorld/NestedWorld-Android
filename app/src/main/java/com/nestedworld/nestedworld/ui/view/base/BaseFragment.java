package com.nestedworld.nestedworld.ui.view.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.analytics.NestedWorldAnalytics;
import com.nestedworld.nestedworld.helpers.application.ApplicationHelper;
import com.nestedworld.nestedworld.ui.view.welcome.WelcomeActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Abstract class for Fragment
 * it make a little abstraction of the life cycle
 * it add a butterKnife support and some useful field/method
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    @Nullable
    protected Context mContext;
    @Nullable
    private Unbinder mUnbinder;

    /*
    ** Method every child will have to implement
     */

    /**
     * get the layout id
     * it will be use under onCreateView()
     * as inflater.inflate(getLayoutResource(), ...)
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * init the fragment, this is the equivalent of onCreateView
     */
    protected abstract void init(@NonNull final View rootView, @Nullable final Bundle savedInstanceState);

    /*
    ** Life cycle
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        mContext = getContext();
        NestedWorldAnalytics.logViewLoaded("Fragment", TAG);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mContext = null;
    }

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    /*
    ** Utils
     */
    public String toString() {
        return TAG;
    }

    protected void onFatalError() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ApplicationHelper.logout(mContext);

        Toast.makeText(mContext, getString(R.string.error_request_user), Toast.LENGTH_LONG).show();

        //go to launch screen
        Intent intent = new Intent(mContext, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    protected void startActivity(Class clazz) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        final Intent intent = new Intent(mContext, clazz);
        startActivity(intent);
    }
}