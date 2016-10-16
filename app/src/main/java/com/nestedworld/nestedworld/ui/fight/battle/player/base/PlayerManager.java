package com.nestedworld.nestedworld.ui.fight.battle.player.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class PlayerManager {
    protected final View mViewContainer;
    protected final BattleMonsterAdapter mAdapter = new BattleMonsterAdapter();
    private StartMessage.StartMessagePlayerMonster mCurrentMonster = null;
    @Nullable protected ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = null;
    protected final List<StartMessage.StartMessagePlayerMonster> mFrontMonster = new ArrayList<>();
    protected final List<StartMessage.StartMessagePlayerMonster> mDeadMonster = new ArrayList<>();
    protected final int mTeamSize;
    protected int mRemainingMonster;

    /*
    ** Constructor
     */
    protected PlayerManager(@NonNull final View container, final int teamSize) {
        //Init internal field
        mViewContainer = container;
        mTeamSize = teamSize;
        mRemainingMonster = teamSize;

        //Retrieve widget
        ButterKnife.bind(this, container);
    }

    /*
    ** Method for child
     */
    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    public abstract void displayMonsterKo(@NonNull final StartMessage.StartMessagePlayerMonster monster);

    protected abstract void displayMonsterDetails(@NonNull final StartMessage.StartMessagePlayerMonster monster);

    /*
    ** Utils
     */
    @CallSuper
    public synchronized void onCurrentMonsterKo() {
        if (!mDeadMonster.contains(mCurrentMonster)) {
            mFrontMonster.remove(mCurrentMonster);
            mDeadMonster.add(mCurrentMonster);

            mRemainingMonster -= 1;
            displayMonsterKo(mCurrentMonster);
        }
    }

    public boolean hasRemainingMonster() {
        return mRemainingMonster > 0;
    }

    @CallSuper
    public void setCurrentMonster(@NonNull final StartMessage.StartMessagePlayerMonster monster, @NonNull final ArrayList<MonsterAttackResponse.MonsterAttack> attacks) {
        mCurrentMonster = monster;
        mCurrentMonsterAttacks = attacks;

        mFrontMonster.add(monster);

        ((BaseAppCompatActivity) mViewContainer.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayMonsterDetails(monster);
            }
        });
    }

    @Nullable
    public StartMessage.StartMessagePlayerMonster getCurrentMonster() {
        if (mFrontMonster.contains(mCurrentMonster)) {
            return mCurrentMonster;
        }
        return null;
    }

    public boolean hasMonsterInFront(final long monsterBattleId) {
        for (StartMessage.StartMessagePlayerMonster monster : mFrontMonster) {
            if (monster.id == monsterBattleId) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public MonsterAttackResponse.MonsterAttack getCurrentMonsterAttack(@NonNull Attack.AttackType attackTypeWanted) {
        if (mCurrentMonsterAttacks == null) {
            return null;
        }

        //Loop over current monster attack for finding an attack of the given type
        for (MonsterAttackResponse.MonsterAttack monsterAttack : mCurrentMonsterAttacks) {
            if (monsterAttack.infos.getType() == attackTypeWanted) {
                return monsterAttack;
            }
        }
        return null;
    }
}
