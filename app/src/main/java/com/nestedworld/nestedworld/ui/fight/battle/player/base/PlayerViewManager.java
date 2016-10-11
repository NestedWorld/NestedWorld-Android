package com.nestedworld.nestedworld.ui.fight.battle.player.base;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;

public interface PlayerViewManager {
    void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);
    void displayAttackReceive();
    void displayAttackSend();
    void onMonsterKo(@Nullable final Monster monster);
    void setupUI(@NonNull final Context context);
}
