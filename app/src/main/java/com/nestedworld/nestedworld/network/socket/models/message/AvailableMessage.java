package com.nestedworld.nestedworld.network.socket.models.message;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.User;

public class AvailableMessage {
    @Expose
    String id;

    @Expose
    String origin;

    @Expose
    Long monster_id;

    @Expose
    User user;
}
