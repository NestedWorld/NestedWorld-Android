package com.nestedworld.nestedworld.models;

import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

public class Session extends SugarRecord {
    @Unique
    public String authToken;

    @Unique
    public String email;

    @Nullable
    public User getUser() {
        return Select.from(User.class)
                .where(Condition.prop("email").eq(email))
                .first();
    }

    //Empty constructor for SugarRecord
    public Session() {
        //keep empty
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