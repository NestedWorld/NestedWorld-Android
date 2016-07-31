package com.nestedworld.nestedworld.helpers.database;

import com.nestedworld.nestedworld.models.Combat;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.models.Place;
import com.nestedworld.nestedworld.models.Region;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.models.UserMonster;
import com.orm.SugarRecord;

public final class DataBaseHelper {

    private DataBaseHelper() {
        //Empty constructor for avoiding this class to be construct
    }

    public static void cleanDataBase() {
        SugarRecord.deleteAll(Combat.class);
        SugarRecord.deleteAll(Friend.class);
        SugarRecord.deleteAll(Monster.class);
        SugarRecord.deleteAll(Place.class);
        SugarRecord.deleteAll(Region.class);
        SugarRecord.deleteAll(Session.class);
        SugarRecord.deleteAll(User.class);
        SugarRecord.deleteAll(UserMonster.class);
    }
}
