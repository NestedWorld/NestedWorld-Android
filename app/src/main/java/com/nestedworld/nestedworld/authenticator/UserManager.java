package com.nestedworld.nestedworld.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.utils.log.LogHelper;

/**
 * AccountManager abstraction
 * /!\ this implementation only allow one account per application (it's a personal choice) /!\
 */
public class UserManager {
    //private static field
    private final static String TAG = UserManager.class.getSimpleName();

    //private properties
    private final AccountManager mAccountManager;
    private Account mAccount;

    /*
    ** Constructor
     */
    private UserManager(@NonNull final Context context) {
        mAccountManager = AccountManager.get(context);
        mAccount = getAccountByName(SharedPreferenceUtils.getLastAccountNameConnected(context));
    }

    /*
    ** singleton
     */
    public static UserManager get(@NonNull final Context context) {
        return (new UserManager(context));
    }

    /*
    ** public method
     */
    @Nullable
    public Account getCurrentAccount() {
        return mAccount;
    }

    public boolean setCurrentUser(@NonNull final Context context, @NonNull final String name, @NonNull final String password, @NonNull final String authToken, @Nullable final Bundle bundle) {
        //check if account already exist
        Account account = getAccountByName(name);

        if (account == null) {
            //We have to create a new account
            account = new Account(name, Constant.ACCOUNT_TYPE);
            if (!mAccountManager.addAccountExplicitly(account, password, bundle)) {
                return false;
            }
            LogHelper.d(TAG, "Successfully create a new account");
        }

        mAccount = account;
        //store name/authToken to sharedPreference
        SharedPreferenceUtils.setAuthTokenToPref(context, authToken);
        SharedPreferenceUtils.setAccountNameToPref(context, name);

        return true;
    }

    @Nullable
    public String getCurrentUserData(@NonNull final String key) {
        if (mAccount == null) {
            return null;
        }
        return mAccountManager.getUserData(mAccount, key);
    }

    public Boolean deleteCurrentAccount(@NonNull final Context context) {
        return deleteAccount(context, mAccount);
    }

    public String getCurrentAuthToken(@NonNull final Context context) {
        return SharedPreferenceUtils.getCurrentAuthToken(context);
    }

    @Nullable
    public String getCurrentAccountName() {
        return mAccount.name;
    }


    /*
    ** Private method
     */
    private Boolean deleteAccount(@NonNull final Context context, @NonNull final Account account) {
        SharedPreferenceUtils.clearPref(context);
        invalidateAuthTokenFromAccount(context, account);
        if (Build.VERSION.SDK_INT >= 22) {
            mAccountManager.removeAccountExplicitly(account);
        } else {
            mAccountManager.removeAccount(account, null, null);
        }
        return true;
    }

    @Nullable
    private Account getAccountByName(@NonNull final String accountName) {
        Account[] accounts = mAccountManager.getAccountsByType(Constant.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            LogHelper.d(TAG, "No registered account");
        }
        for (Account account : accounts) {
            if (account.name.equalsIgnoreCase(accountName)) {
                LogHelper.d(TAG, "Successfully found an account (last_registered = " + accountName + ")");
                return account;
            }
        }
        LogHelper.d(TAG, "No account found (last_registered = " + accountName + ")");
        return null;
    }


    private void invalidateAuthTokenFromAccount(@NonNull final Context context, @NonNull final Account account) {
        mAccountManager.invalidateAuthToken(account.type, SharedPreferenceUtils.getCurrentAuthToken(context));
    }

    /*
    ** SharedPreference Utils
     */
    private static class SharedPreferenceUtils {
        private final static String TAG = SharedPreferenceUtils.class.getSimpleName();

        private final static String ACCOUNT_DETAIL_KEY = "com.nestedworld.account_detail";
        private final static String KEY_ACCOUNT_NAME = "name";
        private final static String KEY_ACCOUNT_TOKEN = "token";

        /*
        ** Account name
         */
        public static String getLastAccountNameConnected(@NonNull final Context context) {
            return context.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_NAME, "");
        }

        public static void setAccountNameToPref(@NonNull final Context context, @NonNull final String name) {
            LogHelper.d(TAG, "setAccountNameToPref : " + name);
            SharedPreferences.Editor edit = context.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit();
            edit.putString(KEY_ACCOUNT_NAME, name);
            edit.apply();
        }

        /*
        ** AuthToken
         */
        public static String getCurrentAuthToken(@NonNull final Context context) {
            return context.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_TOKEN, "");
        }

        public static void setAuthTokenToPref(@NonNull final Context context, @NonNull final String authToken) {
            LogHelper.d(TAG, "setAuthTokenToPref : " + authToken);
            SharedPreferences.Editor edit = context.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit();
            edit.putString(KEY_ACCOUNT_TOKEN, authToken);
            edit.apply();
        }

        /*
        ** Utils
         */
        public static void clearPref(@NonNull final Context context) {
            LogHelper.d(TAG, "clearPref");
            context.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit().clear().apply();
        }
    }

}
