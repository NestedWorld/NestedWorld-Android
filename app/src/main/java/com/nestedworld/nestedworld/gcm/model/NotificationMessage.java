package com.nestedworld.nestedworld.gcm.model;

import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

import org.msgpack.value.Value;

import java.util.Map;

public class NotificationMessage {
    public String message;
    public SocketMessageType.MessageKind type;
    public Map<Value, Value> content;
}
