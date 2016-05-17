package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

public class User extends SugarRecord {
    @Expose
    public String birth_date;

    @Expose
    public String background;

    @Expose
    public String is_active;

    @Expose
    public String pseudo;

    @Expose
    public String registered_at;

    @Expose
    public String email;

    @Expose
    public String city;

    @Expose
    public String avatar;

    @Expose
    public String gender;

    //Empty constructor for SugarRecord
    public User() {

    }

    //Generated method
    @Override
    public String toString() {
        return "User{" +
                "birth_date='" + birth_date + '\'' +
                ", background='" + background + '\'' +
                ", is_active='" + is_active + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", registered_at='" + registered_at + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}