package com.nestedworld.nestedworld.api.models.apiResponse.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @Expose
    @SerializedName("user")
    public User data;

    public class User {
        @Expose
        public String gender;

        @Expose
        public String pseudo;

        @Expose
        public String birth_date;

        @Expose
        public String city;

        @Expose
        public String registered_at;

        @Expose
        public String is_active;

        @Expose
        public String email;
    }

}
