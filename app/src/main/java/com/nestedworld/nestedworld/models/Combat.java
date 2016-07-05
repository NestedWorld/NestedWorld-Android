package com.nestedworld.nestedworld.models;

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
    public String combat_id;// the sql table will be called combatid (see sugarOrm doc)
    public String type;
    public String origin;
    public Integer monsterId;
    public String opponent_pseudo;// the sql table will be called opponentpseudo (see sugarOrm doc)
}
