package com.nestedworld.nestedworld.authentificator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

/*
** AccountManager abstraction
** /!\ this implementation allow only one account per application /!\
*/
public class UserManager {
    //private static field
    private final static String ACCOUNT_DETAIL_KEY = "com.nestedworld.accounnt_detail";
    private final static String KEY_ACCOUNT_NAME = "name";
    private final static String TAG = UserManager.class.getSimpleName();

    //private properties
    private Context mContext;
    private Account mAccount;
    private AccountManager mAccountManager;

    /*
    ** Constructor
     */
    private UserManager(final Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        mAccount = retrieveAccount(getLastAccountNameConnected());
    }

    /*
    ** singleton
     */
    public static UserManager get(final Context context) {
        return (new UserManager(context));
    }

    /*
    ** public method
     */
    public Account getUserAccount() {
        return mAccount;
    }

    public boolean setUser(final String name, final String password, final String authToken, final Bundle bundle) {
        //Create a new account
        Account newAccount = new Account(name, Constant.ACCOUNT_TYPE);

        //Save account
        if (mAccountManager.addAccountExplicitly(newAccount, password, bundle)) {
            //Store authToken under created account
            setAuthTokenType(newAccount, authToken);
            setAccountNameToPref(name);
            mAccount = newAccount;
            return true;
        }
        return false;
    }

    public String getUserExtraData(final String key) {
        return mAccountManager.getUserData(getUserAccount(), key);
    }

    public Boolean deleteAccount(final Account account) {
        removeAccountNameFromPref();
        invalidateAuthToken(account);
        if (Build.VERSION.SDK_INT >= 22) {
            mAccountManager.removeAccountExplicitly(account);
        }
        else {
            mAccountManager.removeAccount(account, null, null);
        }
        return true;
    }

    /*
    ** Private method
     */
    private Account retrieveAccount(final String accountName) {
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

    private String getAuthToken(final Account account) {
        try {
            AccountManagerFuture<Bundle> accountManagerFuture = mAccountManager.getAuthToken(account, Constant.AUTHTOKEN_TYPE, null, (Activity) mContext, null, null);
            Bundle authTokenBundle = accountManagerFuture.getResult();
            Object tokenObject = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN);
            return tokenObject != null ? tokenObject.toString() : null;
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean invalidateAuthToken(final Account account) {
        String authToken = getAuthToken(account);
        if (authToken != null) {
            mAccountManager.invalidateAuthToken(account.type, authToken);
            return true;
        }
        return false;
    }

    private void setAuthTokenType(final Account account, final String authToken) {
        mAccountManager.setAuthToken(account, Constant.AUTHTOKEN_TYPE, authToken);
    }

    /*
    ** SharedPreference Utils
     */
    private String getLastAccountNameConnected() {
        return mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).getString(KEY_ACCOUNT_NAME, "");
    }

    private void setAccountNameToPref(final String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.putString(KEY_ACCOUNT_NAME, name);
        edit.apply();
    }

    private void removeAccountNameFromPref() {
        mContext.getSharedPreferences(ACCOUNT_DETAIL_KEY, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
