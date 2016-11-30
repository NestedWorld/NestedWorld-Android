package com.nestedworld.nestedworld.network.http.implementation;

import com.nestedworld.nestedworld.BuildConfig;
import com.nestedworld.nestedworld.network.NetworkConstant;

public interface HttpEndPoint {
    String DEV_URL = "http://api-dev.nestedworld.com";
    String PROD_URL = "http://api.nestedworld.com";

    String BASE_URL =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_URL
            : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_URL
            : DEV_URL;

    String API_VERSION = "/v1/";

    String BASE_END_POINT = BASE_URL + API_VERSION;

    //Monster related endpoint
    String MONSTERS_PREFIX = "monsters";
    String MONSTERS_LIST = MONSTERS_PREFIX;
    String MONSTER_ATTACK = MONSTERS_PREFIX + "/{monsterId}/attacks";

    //Place related endpoint
    String GEO_PREFIX = "geo";
    String PORTALS_LIST = GEO_PREFIX + "/portals/{longitude}/{latitude}";

    //User related endpoint
    String USER_PREFIX = "users/";
    String USER_INFO = USER_PREFIX + "me/";
    String USER_FRIENDS = USER_PREFIX + "friends/";
    String USER_MONSTERS = USER_PREFIX + "monsters";
    String USER_INVENTORY = USER_INFO + "inventory/";

    //Object related endpoint
    String OBJECT_PREFIX = "objects/";
    String OBJECT_LIST = OBJECT_PREFIX;
    String OBJECT_DETAIL = "objects/{objectId}";

    //Auth related endpoint
    String AUTH_PREFIX = USER_PREFIX + "auth/";
    String USER_SIGN_IN = AUTH_PREFIX + "login/simple";
    String USER_REGISTER = AUTH_PREFIX + "register";
    String USER_PASSWORD = AUTH_PREFIX + "resetpassword";
    String USER_LOGOUT = AUTH_PREFIX + "logout";

    //Attack related endpoint
    String ATTACK_PREFIX = "attacks/";
    String ATTACK_LIST = ATTACK_PREFIX;
}
