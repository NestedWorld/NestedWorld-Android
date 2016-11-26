package com.nestedworld.nestedworld.analytics.sender;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.nestedworld.nestedworld.database.models.Player;

public final class Answer {

    /*
    ** Constructor
     */
    private Answer() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void logUser(@NonNull final Player currentPlayer) {
        Crashlytics.setUserIdentifier(String.valueOf(currentPlayer.playerId));
        Crashlytics.setUserEmail(currentPlayer.email);
        Crashlytics.setUserName(currentPlayer.pseudo);
        Crashlytics.setString("avatar", currentPlayer.avatar);
        Crashlytics.setString("isActive", currentPlayer.isActive);
        Crashlytics.setString("registeredAt", currentPlayer.registeredAt);
    }

    public static void logViewLoaded(@NonNull final String viewType, @NonNull final String viewName) {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(viewName)
                .putContentType(viewType));
    }
}
