package com.nestedworld.nestedworld;

import com.newrelic.agent.android.NewRelic;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application implementation
 * it allow the crash logger to be launch at the very start of the application
 */
public class NestedWorldApp extends Application {
    /*
    ** Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initCrashLogger();
        initFontOverrider();
    }

    /*
    ** Utils
     */
    private void initCrashLogger() {
        if (!BuildConfig.DEBUG) {
            NewRelic.withApplicationToken("AAfa7011e7d073accc8bc537079365343349369b8f")
                    .start(this);
        }
    }

    private void initFontOverrider() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ProximaNova-Reg.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
