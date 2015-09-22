package com.nestedworld.nestedworld.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * AccountManager abstraction
 * /!\ this implementation only allow one account per application (it's a personal choice) /!\
 */
public class UserManager {
    //private static field
    private final static String ACCOUNT_DETAIL_KEY = "com.nestedworld.account_detail";
    private final static String KEY_ACCOUNT_NAME = "name";
    private final static String TAG = UserManager.class.getSimpleName();

    //private properties
    private final Context mContext;
    private final AccountManager mAccountManager;
    private Account mAccount;

    /*
    ** Constructor
     */
    private UserManager(@NonNull final Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        mAccount = getAccountByName(getLastAccountNameConnected());
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
    public Account getCurrentAccount() {
        return mAccount;
    }

    public boolean setCurrentUser(@NonNull final String name, @NonNull final String password, @NonNull final String authToken, @Nullable final Bundle bundle) {
        //check if account already exist
        Account account = getAccountByName(name);

        if (account == null) {
            //We have to create a new account
            account = new Account(name, Constant.ACCOUNT_TYPE);
            if (!mAccountManager.addAccountExplicitly(account, password, bundle)) {
                return false;
            }
            Log.d(TAG, "Successfully create a new account");
        }

        mAccount = account;
        setAuthTokenTypeOnAccount(account, authToken);
        setAccountNameToPref(name);

        return true;
    }

    public String getCurrentUserData(@NonNull final String key) {
        if (mAccount == null) {
            return null;
        }
        return mAccountManager.getUserData(mAccount, key);
    }

    public Boolean deleteCurrentAccount() {
        return deleteAccount(mAccount);
    }

    public String getCurrentAuthToken() {
        return getAuthTokenFromAccount(mAccount);
    }

    public String getCurrentAccountName() {
        return mAccount.name;
    }


    /*
    ** Private method
     */
    private Boolean deleteAccount(@NonNull final Account account) {
        removeAccountNameFromPref();
        invalidateAuthTokenFromAccount(account);
        if (Build.VERSION.SDK_INT >= 22) {
            mAccountManager.removeAccountExplicitly(account);
        }
        else {
            mAccountManager.removeAccount(account, null, null);
        }
        return true;
    }

    private Account getAccountByName(@NonNull final String accountName) {
        Account[] accounts = mAccountManager.getAccountsByType(Constant.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            Log.d(TAG, "No registered account");
        }
        for (Account account : accounts) {
            if (account.name.equalsIgnoreCase(accountName)) {
                Log.d(TAG, "Successfully found an account (last_registered = " + accountName + ")");
                return account;
            }
        }
        Log.d(TAG, "No account found (last_registered = " + accountName + ")");
        return null;
    }

    private String getAuthTokenFromAccount(@NonNull final Account account) {
        //TODO get token under async task
//        try {
//            AccountManagerFuture<Bundle> accountManagerFuture = mAccountManager.getAuthToken(account, Constant.AUTHTOKEN_TYPE, null, (Activity) mContext, null, null);
//            Bundle authTokenBundle = accountManagerFuture.getResult();
//            Object tokenObject = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN);
//            return tokenObject != null ? tokenObject.toString() : null;
//        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    private void invalidateAuthTokenFromAccount(@NonNull final Account account) {
        mAccountManager.invalidateAuthToken(account.type, getAuthTokenFromAccount(account));
    }

    private void setAuthTokenTypeOnAccount(@NonNull final Account account, @NonNull final String authToken) {
        mAccountManager.setAuthToken(account, Constant.AUTHTOKEN_TYPE, authToken);
    }

    /*
    ** SharedPreference Utils
     */
    private String getLastAccountNameConnected() {
        return mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_NAME, "");
    }

    private void setAccountNameToPref(@NonNull final String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.putString(KEY_ACCOUNT_NAME, name);
        edit.apply();
    }

    private void removeAccountNameFromPref() {
        mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
