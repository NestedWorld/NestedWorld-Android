package com.nestedworld.nestedworld.api.models;

import java.io.Serializable;

public class User implements Serializable {

    /*
    ** Fields
     */
    private Long localId;
    private String id;
    private String email;
    private String password;
    private String authentication_token;

    /*
    ** Constructors
     */
    public User() {
    }

    public User(Long localId, String id, String email, String password, String authentication_token) {
        this.localId = localId;
        this.id = id;
        this.email = email;
        this.password = password;
        this.authentication_token = authentication_token;
    }

    /*
    ** Getter & Setter
     */
    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthentication_token() {
        return authentication_token;
    }

    public void setAuthentication_token(String authentication_token) {
        this.authentication_token = authentication_token;
    }
}