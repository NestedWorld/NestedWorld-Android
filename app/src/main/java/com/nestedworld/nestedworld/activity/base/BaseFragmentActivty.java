package com.nestedworld.nestedworld.activity.base;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;

public abstract class BaseFragmentActivty extends FragmentActivity {
    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;

    protected abstract int getLayoutResource();

    protected abstract void initUI(Bundle savedInstanceState);

    protected abstract void initLogics(Bundle savedInstanceState);

    public String toString() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        initUI(savedInstanceState);
        initLogics(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    protected void startActivity(Class clazz, Bundle bundle) {
        try {
            final Intent intent = new Intent(this, clazz);
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