package com.nestedworld.nestedworld.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();

    protected Context mContext;

    protected abstract int getLayoutResource();

    protected abstract void initVariable(Bundle savedInstanceState);

    protected abstract void initData(Bundle savedInstanceState);

    public String toString() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, rootView);
        initVariable(savedInstanceState);
        initData(savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected void startActivity(Class clazz, Bundle bundle) {
        try {
            final Intent intent = new Intent(mContext, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void startActivity(Class clazz) {
        startActivity(clazz, null);
    }
}