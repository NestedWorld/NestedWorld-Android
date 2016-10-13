package com.nestedworld.nestedworld.ui.fight.battle.player.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;

public abstract class PlayerManager {
    protected final View mViewContainer;
    protected StartMessage.StartMessagePlayerMonster mCurrentMonster = null;
    protected ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = null;
    protected BattleMonsterAdapter mAdapter = new BattleMonsterAdapter();

    /*
    ** Constructor
     */
    public PlayerManager(@NonNull final View container) {
        mViewContainer = container;
        ButterKnife.bind(this, container);
    }

    /*
    ** Method for child
     */
    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    public abstract void onCurrentMonsterChanged();

    public abstract void onMonsterKo(final long monster);

    /*
    ** Utils
     */
    @CallSuper
    public PlayerManager setCurrentMonster(@NonNull final StartMessage.StartMessagePlayerMonster monster, @NonNull final ArrayList<MonsterAttackResponse.MonsterAttack> attacks) {
        mCurrentMonster = monster;
        mCurrentMonsterAttacks = attacks;

        ((BaseAppCompatActivity) mViewContainer.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onCurrentMonsterChanged();
            }
        });
        return this;
    }

    public StartMessage.StartMessagePlayerMonster getCurrentMonster() {
        return mCurrentMonster;
    }

    public boolean hasMonster(final long id) {
        return mCurrentMonster.id == id;
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
