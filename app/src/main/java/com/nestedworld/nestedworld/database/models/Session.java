package com.nestedworld.nestedworld.database.models;

import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.annotation.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Session extends SugarRecord {
    @Unique
    public String authToken;

    @Unique
    public String email;

    //Empty constructor for SugarRecord
    public Session() {
        //keep empty
    }

    @Nullable
    public Player getUser() {
        return Select.from(Player.class)
                .where(Condition.prop("email").eq(email))
                .first();
    }

    //Generated
    @Override
    public String toString() {
        return "Session{" +
                "authToken='" + authToken + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}