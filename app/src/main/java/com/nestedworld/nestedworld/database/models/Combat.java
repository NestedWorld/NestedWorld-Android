package com.nestedworld.nestedworld.database.models;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Combat extends SugarRecord {

    @Unique
    public String combatId;// the sql table will be called combatid (see sugarOrm doc)
    public String type;
    public String origin;
    public long monsterId;
    public String opponentPseudo;// the sql table will be called opponentpseudo (see sugarOrm doc)

    //Empty constructor for SugarRecord
    public Combat() {
        //Keep empty
    }

    //Generated
    @Override
    public String toString() {
        return "Combat{" +
                "combatId='" + combatId + '\'' +
                ", type='" + type + '\'' +
                ", origin='" + origin + '\'' +
                ", monsterId=" + monsterId +
                ", opponentPseudo='" + opponentPseudo + '\'' +
                '}';
    }
}
