package com.nestedworld.nestedworld.fragments.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;

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
    ** Method that every child will have to implement
     */

    /**
     * get the layout id
     * it will be use under onCreateView()
     * as inflater.inflate(getLayoutResource(), ...)
     */
    protected abstract int getLayoutResource();

    /**
     * init the fragment, this is the equivalent of onCreateView
     */
    protected abstract void init(View rootView, Bundle savedInstanceState);

    /*
    ** Life cycle
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, rootView);
        init(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
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

        Toast.makeText(mContext, getString(R.string.error_update_user_info), Toast.LENGTH_LONG).show();

        //go to launch screen & kill the current context
        Intent intent = new Intent(mContext, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        ((AppCompatActivity) mContext).finish();
    }

    protected void startActivity(Class clazz) {
        final Intent intent = new Intent(mContext, clazz);
        startActivity(intent);
    }
}