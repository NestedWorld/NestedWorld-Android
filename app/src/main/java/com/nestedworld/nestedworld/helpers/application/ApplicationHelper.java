package com.nestedworld.nestedworld.helpers.application;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.data.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;

public final class ApplicationHelper {
    private ApplicationHelper() {
        //Private constructor for avoiding this class to be constructed
    }

    public static void logout(@NonNull final Context context) {
        //remove player
        SessionHelper.deleteSession();

        //stop socketService and close socket
        ServiceHelper.stopSocketService(context);
        NestedWorldSocketAPI.getInstance().disconnect();

        //avoid leak with the static instance
        NestedWorldHttpApi.reset();

        //clean db
        NestedWorldDatabase.reset();
    }
}
