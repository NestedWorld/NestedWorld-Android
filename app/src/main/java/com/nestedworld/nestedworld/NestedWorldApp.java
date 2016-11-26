package com.nestedworld.nestedworld;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.nestedworld.nestedworld.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application implementation
 * it allow the crash logger to be launch at the very start of the application
 */
public class NestedWorldApp extends Application {

    private final static String TAG = NestedWorldApp.class.getSimpleName();

    /*
    ** Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        initFontOverrider();
        setupGreenDao();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /*
    ** Internal method
     */
    private void setupCrashLogger() {
        Fabric.with(this, new Crashlytics());
    }

    private void initFontOverrider() {
        LogHelper.d(TAG, "initFontOverrider");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProximaNova-Reg.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void setupGreenDao() {
        LogHelper.d(TAG, "setupGreenDao");
        NestedWorldDatabase.setup(this);
        if (BuildConfig.LOG) {
            NestedWorldDatabase.enableQueryBuilderLog();
        }
    }
}
