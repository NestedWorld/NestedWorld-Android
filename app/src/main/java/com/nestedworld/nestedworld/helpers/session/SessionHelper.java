package com.nestedworld.nestedworld.helpers.session;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.orm.query.Select;

/**
 * /!\ this implementation only allow one session per application (it's a personal choice) /!\
 */
public final class SessionHelper {
    //private static field
    private final static String TAG = SessionHelper.class.getSimpleName();

    /*
    ** Constructor
     */
    private SessionHelper() {
    }

    /*
    ** public method
     */
    public static void newSession(@NonNull final String email, @NonNull final String password, @NonNull final String authToken) {

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
    }

    public static void deleteSession() {
        //Display some log
        LogHelper.d(TAG, "deleteSession()");

        //Delete the user linked to the session
        Session session = getSession();
        if (session != null) {
            User user = session.getUser();
            if (user != null) {
                LogHelper.d(TAG, "delete user: " + user.toString());
                user.delete();
            }

            LogHelper.d(TAG, "DeleteOldSession : " + session.toString());
            session.delete();
        }
    }

    @Nullable
    public static Session getSession() {
        return Select.from(Session.class).first();
    }
}
