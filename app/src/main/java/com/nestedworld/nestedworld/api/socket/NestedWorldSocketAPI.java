package com.nestedworld.nestedworld.api.socket;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helper.log.LogHelper;

public class NestedWorldSocketAPI {
    private static NestedWorldSocketAPI mSingleton;
    private final String TAG = getClass().getSimpleName();
    private final static String BASE_URL = "127.0.0.1";
    private final static int PORT = 2009;
    private Context mContext;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final Context context) {
        if (mSingleton != null) {
            return;
        }

        //init API
        init(context);
    }

    /*
    ** Singleton
     */
    public static NestedWorldSocketAPI getInstance(@NonNull final Context context){
        if (mSingleton == null) {
            mSingleton = new NestedWorldSocketAPI(context);
        }
        return mSingleton;

    }

    /*
    ** Avoid leek when log out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** Private method
     */
    private void init(@NonNull final Context context) {
        LogHelper.d(TAG, "Init API(socket) (end_point = " + BASE_URL + ":" + PORT + ")");
        mContext = context;
    }

}

