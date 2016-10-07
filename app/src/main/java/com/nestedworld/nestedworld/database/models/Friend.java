package com.nestedworld.nestedworld.database.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.query.Select;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Friend extends SugarRecord {
    @Expose
    @SerializedName("user")
    public User info;

    public Long fkfuser;//key for User<->Friend relationship

    //Empty constructor for SugarRecord
    public Friend() {
        //Keep empty
    }

    //Utils
    public static int getNumberOfAllyOnline() {
        int allyOnline = 0;
        for (Friend friend : Select.from(Friend.class).list()) {
            User friendInfo = friend.info;
            if (friendInfo != null && friendInfo.is_connected) {
                allyOnline++;
            }
        }
        return allyOnline;
    }

    @Nullable
    public User info() {
        if (info == null) {
            info = Friend.findById(User.class, fkfuser);
        }
        return info;
    }

    //Generated
    @Override
    public String toString() {
        return "Friend{" +
                "info=" + info +
                ", fkfuser=" + fkfuser +
                '}';
    }
}
