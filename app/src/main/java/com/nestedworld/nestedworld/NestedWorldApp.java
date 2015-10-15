package com.nestedworld.nestedworld;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.nestedworld.nestedworld.utils.log.LogHelper;
import com.newrelic.agent.android.NewRelic;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application implementation
 * it allow the crash logger to be launch at the very start of the application
 */
public class NestedWorldApp extends android.support.multidex.MultiDexApplication {
    private static String TAG = NestedWorldApp.class.getSimpleName();
    private static NestedWorldApp mApplication;
    private CallbackManager mCallbackManager;
    private RefWatcher mRefWatcher;

    /*
    ** singleton
     */
    public static NestedWorldApp get() {
        if (mApplication == null) {
            mApplication = new NestedWorldApp();
        }
        return mApplication;
    }

    /*
    ** Life cycle
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initCrashLogger();
        initFontOverrider();
        initFacebookSDK();
        initLeakLogger();
    }

    /*
    ** Public method
     */
    //return the facebook callbackManager
    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    //return the refWater from LeakCanary
    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }


    /*
    ** Utils
     */
    private void initCrashLogger() {
        LogHelper.d(TAG, "initCrashLogger");
        if (!BuildConfig.DEBUG) {
            NewRelic.withApplicationToken("AAfa7011e7d073accc8bc537079365343349369b8f")
                    .start(this);
        }
    }

    private void initLeakLogger() {
        LogHelper.d(TAG, "initLeakLogger");
        mRefWatcher = LeakCanary.install(this);
    }

    private void initFacebookSDK() {
        LogHelper.d(TAG, "initFacebookSDK");
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void initFontOverrider() {
        LogHelper.d(TAG, "initFontOverrider");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ProximaNova-Reg.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
