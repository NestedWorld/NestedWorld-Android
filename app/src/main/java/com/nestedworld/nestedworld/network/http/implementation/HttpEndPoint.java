package com.nestedworld.nestedworld.network.http.implementation;

import com.nestedworld.nestedworld.BuildConfig;
import com.nestedworld.nestedworld.network.NetworkConstant;

public final class HttpEndPoint {

    private final static String API_VERSION = "v1/";

    private final static String DEV_URL = "http://nestedworld-dev.kokakiwi.net/";
    private final static String PROD_URL = "dev.nestedworld.com";

    private final static String BASE_URL =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_URL
                    : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_URL
                    : DEV_URL;

    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    //Monster related endpoint
    private final static String MONSTERS_PREFIX = "monsters";
    public final static String MONSTERS_LIST = MONSTERS_PREFIX;
    public final static String MONSTER_ATTACK = MONSTERS_PREFIX + "/{monsterId}/attacks";

    //Place related endpoint
    private final static String PLACES_PREFIX = "places";
    public final static String PLACES_LIST = PLACES_PREFIX;
    public final static String REGIONS_LIST = PLACES_PREFIX + "/regions";

    //User related endpoint
    private final static String USER_PREFIX = "users/";
    public final static String USER_INFO = USER_PREFIX;
    public final static String USER_FRIENDS = USER_PREFIX + "friends/";
    public final static String USER_MONSTERS = USER_PREFIX + "monsters";

    //Auth related endpoint
    private final static String AUTH_PREFIX = USER_INFO + "auth/";
    public final static String USER_SIGN_IN = AUTH_PREFIX + "login/simple";
    public final static String USER_REGISTER = AUTH_PREFIX + "register";
    public final static String USER_PASSWORD = AUTH_PREFIX + "resetpassword";
    public final static String USER_LOGOUT = AUTH_PREFIX + "logout";

    //Attack related endpoint
    private final static String ATTACK_PREFIX = "attacks/";
    public final static String ATTACK_LIST = ATTACK_PREFIX;

    private HttpEndPoint() {
        //Empty constructor for avoiding this class to be construct
    }
}
