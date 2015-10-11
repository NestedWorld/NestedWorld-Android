package com.nestedworld.nestedworld.authenticator;

import com.nestedworld.nestedworld.activity.launch.LaunchActivity;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Simple implementation of AbstractAccountAuthenticator
 */
public class NWAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;

    /*
    ** Constructor
     */
    public NWAuthenticator(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    /*
    ** Parents Method implementation
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        // We absolutely cannot add an account without some information from the user
        // so we're definitely going to return an Intent via KEY_INTENT
        final Bundle bundle = new Bundle();

        // We're going to use a LoginActivity to talk to the user (mContext
        // we'll have noted on construction).
        final Intent intent = new Intent(mContext, LaunchActivity.class);

        // We can configure that activity however we wish via the Intent
        // We'll set ARG_IS_ADDING_NEW_ACCOUNT so the Activity knows to ask for the account name as well
        intent.putExtra(Constant.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(Constant.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(Constant.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        // It will also need to know how to send its response to the
        // account manager; LoginActivity must derive from
        // AccountAuthenticatorActivity, which will want this key set
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        // Wrap up this intent, and return it, which will cause the
        // intent to be run
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // If the caller requested an authToken type we don't support, then return an error
        if (!authTokenType.equals(Constant.AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Bundle bundle = new Bundle();
        bundle.putString(Constant.ARG_ACCOUNT_TYPE, account.type);
        bundle.putString(Constant.ARG_AUTH_TYPE, authTokenType);
        bundle.putString(Constant.ARG_ACCOUNT_NAME, account.name);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
