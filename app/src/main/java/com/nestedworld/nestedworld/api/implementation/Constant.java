package com.nestedworld.nestedworld.api.implementation;

import com.nestedworld.nestedworld.BuildConfig;

public class Constant {
    public final static String BASE_URL = BuildConfig.DEBUG ? "http://eip-api.kokakiwi.net/" : "http://eip-api.kokakiwi.net/";
    public final static String API_VERSION = "v1";

    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    public static final String USER_SIGN_IN = "/users/auth/login/simple";
    public static final String USER_SIGN_UP = "/users/auth/register";
    public static final String USER_PASSWORD = "/users/password";

}
