package com.nestedworld.nestedworld;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.nestedworld.nestedworld.helper.log.LogHelper;
import com.orm.SugarApp;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application implementation
 * it allow the crash logger to be launch at the very start of the application
 */
public class NestedWorldApp extends SugarApp {

    private final static String TAG = NestedWorldApp.class.getSimpleName();

    /*
    ** Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initFontOverrider();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /*
    ** Utils
     */
    private void initFontOverrider() {
        LogHelper.d(TAG, "initFontOverrider");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProximaNova-Reg.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
