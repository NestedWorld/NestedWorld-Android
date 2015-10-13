package com.nestedworld.nestedworld.api.implementation;

import com.nestedworld.nestedworld.BuildConfig;

public class Constant {
    public final static String BASE_URL = BuildConfig.DEBUG ? "http://eip-api.kokakiwi.net/" : "http://eip-api.kokakiwi.net/";
    public final static String API_VERSION = "v1";

    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    private static final String AUTH_PREFIX = "/users/auth";
    public static final String USER_SIGN_IN = AUTH_PREFIX + "/login/simple";
    public static final String USER_REGISTER = AUTH_PREFIX + "/register";
    public static final String USER_PASSWORD = AUTH_PREFIX + "/resetpassword";
    public static final String USER_LOGOUT = AUTH_PREFIX + "/logout";

    private static final String MONSTERS_PREFIX = "/monsters";
    public static final String MONSTERS_LIST = MONSTERS_PREFIX;

}
