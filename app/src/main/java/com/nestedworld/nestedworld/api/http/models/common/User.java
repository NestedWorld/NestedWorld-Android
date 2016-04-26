package com.nestedworld.nestedworld.api.http.models.common;

import com.google.gson.annotations.Expose;

public class User {
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
}
