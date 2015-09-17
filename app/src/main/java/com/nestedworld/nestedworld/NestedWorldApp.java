package com.nestedworld.nestedworld;

import android.app.Application;

import com.newrelic.agent.android.NewRelic;

public class NestedWorldApp extends Application {
    /*
    ** Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initCrashLogger();
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
}
