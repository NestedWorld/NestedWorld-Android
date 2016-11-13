package com.nestedworld.nestedworld.helpers.database;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.orm.SugarRecord;

public final class DataBaseHelper {

    /*
    ** Constructor
     */
    private DataBaseHelper() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void cleanDataBase() {
        SugarRecord.deleteAll(Attack.class);
        SugarRecord.deleteAll(Combat.class);
        SugarRecord.deleteAll(Friend.class);
        SugarRecord.deleteAll(Monster.class);
        SugarRecord.deleteAll(Session.class);
        SugarRecord.deleteAll(Player.class);
        SugarRecord.deleteAll(UserMonster.class);
    }
}
