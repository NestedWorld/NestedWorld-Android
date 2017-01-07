package com.nestedworld.nestedworld.helpers.session;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.database.entities.session.Session;
import com.nestedworld.nestedworld.data.database.entities.session.SessionData;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

/**
 * /!\ this implementation only allow one session per application (it's a personal choice) /!\
 */
public final class SessionHelper {
    //private static field
    private final static String TAG = SessionHelper.class.getSimpleName();

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private SessionHelper() {
        //private constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void newSession(@NonNull final String email,
                                  @NonNull final String password,
                                  @NonNull final String authToken) {
        //Display some log
        LogHelper.d(TAG, "newSession : "
                + " name=" + email
                + " password=" + password
                + " authToken=" + authToken);

        //Delete old session
        deleteSession();

        //Create a new session
        final Session session = new Session();
        session.authToken = authToken;
        session.email = email;

        //Save the new session
        NestedWorldDatabase.getInstance()
                .getDataBase()
                .getSessionDao()
                .insert(session);
    }

    public static void deleteSession() {
        //Display some log
        LogHelper.d(TAG, "deleteSession()");

        //Delete the player linked to the session
        final Session session = getSession();
        if (session != null) {
            final SessionData sessionData = session.getSessionData();
            if (sessionData != null) {
                LogHelper.d(TAG, "delete player: " + sessionData.toString());
                sessionData.delete();
            }

            LogHelper.d(TAG, "DeleteOldSession : " + session.toString());
            session.delete();
        }
    }

    @Nullable
    public static Session getSession() {
        return NestedWorldDatabase.getInstance()
                .getDataBase()
                .getSessionDao()
                .queryBuilder()
                .unique();
    }
}
