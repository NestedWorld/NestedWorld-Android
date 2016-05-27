package com.nestedworld.nestedworld.helpers.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.models.User;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * /!\ this implementation only allow one session per application (it's a personal choice) /!\
 */
public final class SessionManager {
    //singleton
    private static SessionManager mUserManager = null;

    //private static field
    private final static String TAG = SessionManager.class.getSimpleName();

    /*
    ** Constructor
     */
    private SessionManager() {
    }

    /*
    ** singleton
     */
    public static SessionManager get() {
        if (mUserManager == null) {
            mUserManager = new SessionManager();
        }
        return mUserManager;
    }

    /*
    ** public method
     */
    public void newSession(@NonNull final String email, @NonNull final String password, @NonNull final String authToken) {

        //Display some log
        LogHelper.d(TAG, "newSession : "
                + " name=" + email
                + " password=" + password
                + " authToken=" + authToken);

        //Delete old session
        deleteSession();

        //Create a new session
        Session session = new Session();
        session.authToken = authToken;
        session.email = email;

        //Save the new session
        session.save();

        Log.e(TAG + "get: ", getSession().toString());
    }

    public void deleteSession() {
        //Display some log
        LogHelper.d(TAG, "DeleteOldSession");
        Session.deleteAll(Session.class);
    }

    @Nullable
    public Session getSession() {
        return Select.from(Session.class).first();
    }
}
