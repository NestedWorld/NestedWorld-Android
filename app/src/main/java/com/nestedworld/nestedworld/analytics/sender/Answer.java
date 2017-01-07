package com.nestedworld.nestedworld.analytics.sender;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.nestedworld.nestedworld.data.database.entities.session.SessionData;

public final class Answer {

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private Answer() {
        //Private constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void logUser(@NonNull final SessionData sessionPlayer) {
        //Set generic field
        Crashlytics.setUserIdentifier(String.valueOf(sessionPlayer.playerId));
        Crashlytics.setUserEmail(sessionPlayer.email);
        Crashlytics.setUserName(sessionPlayer.pseudo);

        //Set custom field
        Crashlytics.setString("avatar", sessionPlayer.avatar);
        Crashlytics.setString("isActive", sessionPlayer.isActive);
        Crashlytics.setString("registeredAt", sessionPlayer.registeredAt);
    }

    public static void logViewLoaded(@NonNull final String viewType,
                                     @NonNull final String viewName) {
        Answers.getInstance()
                .logContentView(new ContentViewEvent()
                        .putContentName(viewName)
                        .putContentType(viewType));
    }
}
