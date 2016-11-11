package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.util.List;

public class Portal extends SugarRecord {

    //Empty constructor for SugarRecord
    public Portal() {
        //Keep empty
    }

    @Expose
    public String url;

    @Expose
    public List<Float> position;

    @Expose
    public String name;
}
