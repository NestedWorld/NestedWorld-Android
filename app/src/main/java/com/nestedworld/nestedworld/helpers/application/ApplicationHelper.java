package com.nestedworld.nestedworld.helpers.application;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helpers.database.DataBaseHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;

public final class ApplicationHelper {
    private ApplicationHelper() {
        //Private constructor for avoiding this class to be constructed
    }

    public static void logout(@NonNull final Context context) {
        //remove user
        SessionHelper.deleteSession();

        //avoid leak with the static instance
        NestedWorldHttpApi.reset();
        NestedWorldSocketAPI.reset();

        //clean db
        DataBaseHelper.cleanDataBase();

        //stop socketService
        ServiceHelper.stopSocketService(context);
    }
}
