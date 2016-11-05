package com.nestedworld.nestedworld.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.nestedworld.nestedworld.helpers.log.LogHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Abstract class for AppCompatActivity
 * it make a little abstraction of the life cycle
 * it add a butterKnife support and some useful field/method
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    @Nullable
    private Unbinder mUnbinder;
    /*
    ** Method that every child will have to implement
     */

    /**
     * get the layout id
     * it will be use under onCreate()
     * as setContentView(getLayoutResource());
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * init the activity here
     * This method is equivalent to onCreate()
     */
    protected abstract void init(@Nullable Bundle savedInstanceState);

    /*
    ** Life cycle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());
        mUnbinder = ButterKnife.bind(this);

        try {
            Trace.beginSection(TAG + " init");
            init(savedInstanceState);
        } finally {
            Trace.endSection();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                return true;
            default:
                return (super.onOptionsItemSelected(menuItem));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //We override the font
        //See https://github.com/chrisjenx/Calligraphy
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*
    ** Utils
     */
    protected boolean isUiBinded() {
        boolean isBinded = mUnbinder != null;
        LogHelper.d(TAG, "isUiBinded > " + isBinded);
        return isBinded && !super.isDestroyed();
    }

    public String toString() {
        return TAG;
    }

    protected void startActivity(@NonNull final Class clazz, @Nullable Bundle bundle) {
        final Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(@NonNull final Class clazz) {
        startActivity(clazz, null);
    }
}
