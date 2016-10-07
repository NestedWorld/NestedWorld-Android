package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;

public class OnAttackReceiveEvent extends SocketMessageEvent<AttackReceiveMessage> {

    public OnAttackReceiveEvent(@NonNull AttackReceiveMessage message) {
        super(message);
    }
}
