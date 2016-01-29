package com.nestedworld.nestedworld.api.implementation;

import com.nestedworld.nestedworld.BuildConfig;

public class Constant {
    private final static String BASE_URL = BuildConfig.DEBUG ? "http://eip-api-dev.kokakiwi.net/" : "http://eip-api.kokakiwi.net/";
    private final static String API_VERSION = "v1/";

    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    public static final String MONSTERS_LIST = "monsters";

    public static final String PLACES_LIST = "places";

    public static final String USER_INFO = "users/";
    private static final String AUTH_PREFIX = USER_INFO + "auth/";
    public static final String USER_SIGN_IN = AUTH_PREFIX + "login/simple";
    public static final String USER_REGISTER = AUTH_PREFIX + "register";
    public static final String USER_PASSWORD = AUTH_PREFIX + "resetpassword";
    public static final String USER_LOGOUT = AUTH_PREFIX + "logout";
}
