package com.nestedworld.nestedworld.analytics;

import android.support.annotation.NonNull;
import com.nestedworld.nestedworld.analytics.sender.Answer;
import com.nestedworld.nestedworld.data.database.entities.session.SessionData;

public final class NestedWorldAnalytics {

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private NestedWorldAnalytics() {
        //Private constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void setUserInfo(@NonNull final SessionData sessionData) {
        Answer.logUser(sessionData);
    }

    public static void logViewLoaded(@NonNull final String viewType,
                                     @NonNull final String viewName) {
        Answer.logViewLoaded(viewType, viewName);
    }
}
