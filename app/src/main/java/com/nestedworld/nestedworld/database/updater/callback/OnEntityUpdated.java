package com.nestedworld.nestedworld.database.updater.callback;

import android.support.annotation.NonNull;

public interface OnEntityUpdated {
    void onSuccess();

    void onError(@NonNull final KIND errorKind);

    enum KIND {
        NETWORK,
        SERVER,
    }
}

