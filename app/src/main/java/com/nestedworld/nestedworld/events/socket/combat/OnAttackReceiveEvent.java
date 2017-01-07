package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnAttackReceiveEvent extends SocketMessageEvent<AttackReceiveMessage> {

    public OnAttackReceiveEvent(@NonNull final AttackReceiveMessage message) {
        super(message);
    }
}
