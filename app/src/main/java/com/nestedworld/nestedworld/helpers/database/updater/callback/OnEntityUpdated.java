package com.nestedworld.nestedworld.helpers.database.updater.callback;

public interface OnEntityUpdated {
    void onSuccess();

    void onError(KIND errorKind);

    enum KIND {
        NETWORK,
        SERVER,
    }
}

