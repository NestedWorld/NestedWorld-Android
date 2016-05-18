package com.nestedworld.nestedworld.helpers.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.User;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * AccountManager abstraction
 * /!\ this implementation only allow one account per application (it's a personal choice) /!\
 */
public final class UserManager {
    //singleton
    private static UserManager mUserManager = null;

    //private static field
    private final static String TAG = UserManager.class.getSimpleName();

    /*
    ** Constructor
     */
    private UserManager() {
    }

    /*
    ** singleton
     */
    public static UserManager get() {
        if (mUserManager == null) {
            mUserManager = new UserManager();
        }
        return mUserManager;
    }

    /*
    ** public method
     */
    public boolean newUser(@NonNull final Context context, @NonNull final String name,
                           @NonNull final String password, @NonNull final String authToken) {

        //Display some log
        LogHelper.d(TAG, "newUser : "
                + " name=" + name
                + " password=" + password
                + " authToken=" + authToken);

        //store name/authToken to sharedPreference
        SharedPreferenceUtils.setAuthTokenToPref(context, authToken);
        SharedPreferenceUtils.setAccountNameToPref(context, name);

        return true;
    }

    public void deleteCurrentUser(@NonNull final Context context) {
        //Clear user information
        SharedPreferenceUtils.clearPref(context);
    }

    @Nullable
    public User getUser(@NonNull final Context context) {
        String currentAccountName = SharedPreferenceUtils.getLastAccountNameConnected(context);
        return Select.from(User.class).where(Condition.prop("email").eq(currentAccountName)).first();
    }

    @Nullable
    public String getUserEmail(@NonNull final Context context) {
        return SharedPreferenceUtils.getLastAccountNameConnected(context);
    }

    @Nullable
    public String getCurrentAuthToken(@NonNull final Context context) {
        return SharedPreferenceUtils.getCurrentAuthToken(context);
    }

    /*
    ** SharedPreference Utils
     */
    private static class SharedPreferenceUtils {
        private final static String TAG = SharedPreferenceUtils.class.getSimpleName();

        private final static String USER_DATA_PREF_NAME = "com.nestedworld.user_data";

        private final static String ACCOUNT_DETAIL_PREF_NAME = "com.nestedworld.account_detail";
        private final static String KEY_ACCOUNT_NAME = "name";
        private final static String KEY_ACCOUNT_TOKEN = "token";

        /*
        ** Account name
         */
        @Nullable
        private static String getLastAccountNameConnected(@NonNull final Context context) {
            return context.getSharedPreferences(ACCOUNT_DETAIL_PREF_NAME, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_NAME, "");
        }

        private static void setAccountNameToPref(@NonNull final Context context, @NonNull final String name) {
            LogHelper.d(TAG, "setAccountNameToPref : " + name);

            SharedPreferences.Editor edit = context.getSharedPreferences(ACCOUNT_DETAIL_PREF_NAME, Context.MODE_PRIVATE).edit();
            edit.putString(KEY_ACCOUNT_NAME, name);
            edit.apply();
        }

        /*
        ** AuthToken
         */
        @Nullable
        private static String getCurrentAuthToken(@NonNull final Context context) {
            return context.getSharedPreferences(ACCOUNT_DETAIL_PREF_NAME, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_TOKEN, "");
        }

        public static void setAuthTokenToPref(@NonNull final Context context, @NonNull final String authToken) {
            LogHelper.d(TAG, "setAuthTokenToPref : " + authToken);

            SharedPreferences.Editor edit = context.getSharedPreferences(ACCOUNT_DETAIL_PREF_NAME, Context.MODE_PRIVATE).edit();
            edit.putString(KEY_ACCOUNT_TOKEN, authToken);
            edit.apply();
        }

        /*
        ** Utils
         */
        private static void clearPref(@NonNull final Context context) {
            LogHelper.d(TAG, "clearPref");

            //clean the account user
            context.getSharedPreferences(ACCOUNT_DETAIL_PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();

            //clean the related user user
            context.getSharedPreferences(USER_DATA_PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        }
    }
}
