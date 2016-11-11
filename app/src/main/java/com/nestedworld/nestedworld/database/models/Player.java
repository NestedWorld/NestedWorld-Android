package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Player extends SugarRecord {
    @Expose
    @SerializedName("birth_date")
    public String birthDate;

    @Expose
    public String city;

    @Expose
    public String gender;

    @Expose
    public String background;

    @Expose
    public String email;

    @Expose
    public Long level;

    @Expose
    @SerializedName("is_active")
    public String isActive;

    @Expose
    public String pseudo;

    @Expose
    @SerializedName("registered_at")
    public String registeredAt;

    @Expose
    public String avatar;

    @Expose
    @SerializedName("is_connected")
    public Boolean isConnected;

    //Empty constructor for SugarRecord
    public Player() {
        //keep empty
    }

    //Generated method
    @Override
    public String toString() {
        return "User{" +
                "birthDate='" + birthDate + '\'' +
                ", background='" + background + '\'' +
                ", isActive='" + isActive + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", registeredAt='" + registeredAt + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
