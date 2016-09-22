package com.nestedworld.nestedworld.event.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;

public class OnAttackReceiveEvent extends SocketMessageEvent<AttackReceiveMessage> {

    public OnAttackReceiveEvent(@NonNull AttackReceiveMessage message) {
        super(message);
    }
}
