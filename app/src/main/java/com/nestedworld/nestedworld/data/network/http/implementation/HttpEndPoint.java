package com.nestedworld.nestedworld.data.network.http.implementation;

import com.nestedworld.nestedworld.BuildConfig;
import com.nestedworld.nestedworld.data.network.NetworkConstant;

public final class HttpEndPoint {
    /*
     * #############################################################################################
     * # Base URL related
     * #############################################################################################
     */
    private final static String DEV_URL = "http://api-dev.nestedworld.com";
    private final static String PROD_URL = "http://api.nestedworld.com";

    private final static String BASE_URL =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_URL
            : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_URL
            : DEV_URL;
    private final static String API_VERSION = "/v1/";
    public final static String BASE_END_POINT = BASE_URL + API_VERSION;

    /*
     * #############################################################################################
     * # Monster related
     * #############################################################################################
     */
    private final static String MONSTERS_PREFIX = "monsters";
    public final static String MONSTERS_LIST = MONSTERS_PREFIX;
    public final static String MONSTER_ATTACK = MONSTERS_PREFIX + "/{monsterId}/attacks";

    /*
     * #############################################################################################
     * # Place related
     * #############################################################################################
     */
    private final static String GEO_PREFIX = "geo";
    public final static String PORTALS_LIST = GEO_PREFIX + "/portals/{longitude}/{latitude}";

    /*
     * #############################################################################################
     * # User related
     * #############################################################################################
     */
    private final static String USER_PREFIX = "users/";
    public final static String USER_INFO = USER_PREFIX + "me/";
    public final static String USER_FRIENDS = USER_PREFIX + "friends/";
    public final static String USER_MONSTERS = USER_PREFIX + "monsters";
    public final static String USER_INVENTORY = USER_INFO + "inventory/";

    /*
     * #############################################################################################
     * # Object related
     * #############################################################################################
     */
    private final static String OBJECT_PREFIX = "objects/";
    public final static String OBJECT_LIST = OBJECT_PREFIX;
    public final static String OBJECT_DETAIL = "objects/{objectId}";

    /*
     * #############################################################################################
     * # Auth related
     * #############################################################################################
     */
    private final static String AUTH_PREFIX = USER_PREFIX + "auth/";
    public final static String USER_SIGN_IN = AUTH_PREFIX + "login/simple";
    public final static String USER_REGISTER = AUTH_PREFIX + "register";
    public final static String USER_PASSWORD = AUTH_PREFIX + "resetpassword";
    public final static String USER_LOGOUT = AUTH_PREFIX + "logout";

    /*
     * #############################################################################################
     * # Attack related
     * #############################################################################################
     */
    private final static String ATTACK_PREFIX = "attacks/";
    public final static String ATTACK_LIST = ATTACK_PREFIX;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private HttpEndPoint() {
        //Private constructor for avoiding this class to be construct
    }
}

