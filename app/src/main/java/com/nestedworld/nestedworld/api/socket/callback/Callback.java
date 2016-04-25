package com.nestedworld.nestedworld.api.socket.callback;


import com.nestedworld.nestedworld.api.socket.NestedWorldSocketAPI;

public interface Callback {
    void onConnexionReady(NestedWorldSocketAPI nestedWorldSocketAPI);
    void onConnexionFailed();
}
