package com.nestedworld.nestedworld.ui.fight.battle.player.base;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;

import butterknife.ButterKnife;

public abstract class BasePlayerViewManager {
    protected final View mViewContainer;

    public BasePlayerViewManager(@NonNull final View container) {
        mViewContainer = container;
        ButterKnife.bind(this, container);
    }

    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    public abstract void onMonsterKo(@Nullable final Monster monster);

    public abstract void setupUI(@NonNull final Context context);
}
