package com.nestedworld.nestedworld.analytics;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.analytics.sender.Answer;
import com.nestedworld.nestedworld.database.models.Player;

public class NestedWorldAnalytics {

    /*
    * Constructor
     */
    private NestedWorldAnalytics() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void setUserInfo(@NonNull final Player player) {
        Answer.logUser(player);
    }

    public static void logViewLoaded(@NonNull final String viewType, @NonNull final String viewName) {
        Answer.logViewLoaded(viewType, viewName);
    }
}
