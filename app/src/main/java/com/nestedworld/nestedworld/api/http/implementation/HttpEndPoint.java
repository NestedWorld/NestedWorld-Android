package com.nestedworld.nestedworld.api.http.implementation;

import com.nestedworld.nestedworld.BuildConfig;

public class HttpEndPoint {

    private final static String BASE_URL = BuildConfig.DEBUG ? "http://eip-api-dev.kokakiwi.net/" : "http://eip-api.kokakiwi.net/";
    private final static String API_VERSION = "v1/";
    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    public final static String MONSTERS_LIST = "monsters";

    private final static String PLACES_PREFIX = "places";
    public final static String PLACES_LIST = PLACES_PREFIX;
    public final static String REGIONS_LIST = PLACES_PREFIX + "/regions";

    private final static  String USER_PREFIX = "users/";
    public final static String USER_INFO = USER_PREFIX;
    public final static String USER_FRIENDS = USER_PREFIX + "friends";
    public final static String USER_MONSTERS = USER_PREFIX + "monsters";

    private final static String AUTH_PREFIX = USER_INFO + "auth/";
    public final static String USER_SIGN_IN = AUTH_PREFIX + "login/simple";
    public final static String USER_REGISTER = AUTH_PREFIX + "register";
    public final static String USER_PASSWORD = AUTH_PREFIX + "resetpassword";
    public final static String USER_LOGOUT = AUTH_PREFIX + "logout";
}
