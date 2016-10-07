package com.nestedworld.nestedworld.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.ui.registration.RegistrationActivity;

import butterknife.ButterKnife;

/**
 * Abstract class for Fragment
 * it make a little abstraction of the life cycle
 * it add a butterKnife support and some useful field/method
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    @Nullable
    protected Context mContext;

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
        ButterKnife.bind(this, rootView);
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
        ButterKnife.unbind(this);
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

        //avoid leek with the static instance
        NestedWorldHttpApi.reset();
        NestedWorldSocketAPI.reset();

        //clean session
        SessionHelper.deleteSession();

        Toast.makeText(mContext, getString(R.string.error_request_user), Toast.LENGTH_LONG).show();

        //go to launch screen & kill the current context
        Intent intent = new Intent(mContext, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        ((AppCompatActivity) mContext).finish();
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