package com.nestedworld.nestedworld.ui.fight.battle.player.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BasePlayerViewManager {
    private final static String TAG = BasePlayerViewManager.class.getSimpleName();

    protected final View mViewContainer;
    protected StartMessage.StartMessagePlayerMonster mCurrentMonster = null;
    protected ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = null;

    /*
    ** Constructor
     */
    public BasePlayerViewManager(@NonNull final View container) {
        mViewContainer = container;
        ButterKnife.bind(this, container);
    }

    /*
    ** Method for child
     */
    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    public abstract void onMonsterKo(final long monster);
    /*
    ** Utils
     */
    @CallSuper
    public void build(@NonNull final Context context) {
        if (mCurrentMonster == null || mCurrentMonsterAttacks == null) {
            LogHelper.d(TAG, "currentMonster or currentMonsterAttacks null");
            throw new IllegalArgumentException("You must call setCurrentUser before calling build");
        }
    }

    public boolean hasMonster(final long id)  {
        return mCurrentMonster.id == id;
    }

    @CallSuper
    public BasePlayerViewManager setCurrentMonster(@NonNull final StartMessage.StartMessagePlayerMonster monster, @NonNull final ArrayList<MonsterAttackResponse.MonsterAttack> attacks) {
        mCurrentMonster = monster;
        mCurrentMonsterAttacks = attacks;
        return this;
    }

    public StartMessage.StartMessagePlayerMonster getCurrentMonster() {
        return mCurrentMonster;
    }

    @Nullable
    public MonsterAttackResponse.MonsterAttack getMonsterAttackByType(@NonNull Attack.AttackType attackTypeWanted) {
        //Loop over current monster attack for finding an attack of the given type
        for (MonsterAttackResponse.MonsterAttack monsterAttack : mCurrentMonsterAttacks) {
            if (monsterAttack.infos.getType() == attackTypeWanted) {
                return monsterAttack;
            }
        }
        return null;
    }
}
