package com.nestedworld.nestedworld.network.socket.implementation;

import com.nestedworld.nestedworld.BuildConfig;
import com.nestedworld.nestedworld.network.NetworkConstant;

public interface SocketEndPoint {
    String DEV_URL = "realtime-dev.kokakiwi.net";
    String PROD_URL = "realtime.kokakiwi.net";

    String SOCKET_END_POINT =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_URL
            : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_URL
            : DEV_URL;

    int DEV_PORT = 6465;
    int PROD_PORT = 6464;
    int SOCKET_PORT =
            (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.DEV) ? DEV_PORT
            : (BuildConfig.ENVIRONMENT == NetworkConstant.Environement.PROD) ? PROD_PORT
            : DEV_PORT;
}
