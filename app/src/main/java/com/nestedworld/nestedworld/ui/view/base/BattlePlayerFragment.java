package com.nestedworld.nestedworld.ui.view.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.entities.Attack;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.adapter.RecyclerView.BattleMonsterAdapter;
import java.util.ArrayList;
import java.util.List;

public abstract class BattlePlayerFragment extends BaseFragment {
    protected final BattleMonsterAdapter mAdapter = new BattleMonsterAdapter();
    protected final List<StartMessage.StartMessagePlayerMonster> mFrontMonster = new ArrayList<>();
    protected final List<StartMessage.StartMessagePlayerMonster> mDeadMonster = new ArrayList<>();
    @Nullable protected List<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = null;
    protected int mRemainingMonster;
    private StartMessage.StartMessagePlayerMonster mCurrentMonster = null;

    /*
    ** Method for child
     */
    public abstract void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster);

    public abstract void displayAttackReceive();

    public abstract void displayAttackSend();

    protected abstract void displayMonsterKo(@NonNull final StartMessage.StartMessagePlayerMonster monster);

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
    public void setCurrentMonster(@NonNull final StartMessage.StartMessagePlayerMonster monster, @NonNull final List<MonsterAttackResponse.MonsterAttack> attacks) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mCurrentMonster = monster;
        mCurrentMonsterAttacks = attacks;

        mFrontMonster.add(monster);
        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
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
            if (monsterAttack.infos.getTypeKind() == attackTypeWanted) {
                return monsterAttack;
            }
        }
        return null;
    }
}
