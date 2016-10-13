package com.nestedworld.nestedworld.ui.fight.battle.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.PlayerManager;

import java.util.List;

import butterknife.BindView;

public class UserPlayerManager extends PlayerManager {

    private final static String TAG = UserPlayerManager.class.getSimpleName();

    @BindView(R.id.textview_monster_lvl)
    TextView monsterLvl;
    @BindView(R.id.textview_monster_name)
    TextView monsterName;
    @BindView(R.id.imageView_monster)
    ImageView monsterPicture;
    @BindView(R.id.progressBar_MonsterLife)
    ProgressBar progressBarMonsterHp;
    @BindView(R.id.textview_MonsterLife)
    TextView monsterLife;
    @BindView(R.id.RecyclerView_battle_monster)
    RecyclerView recyclerViewMonsters;
    private List<UserMonster> mTeam = null;

    /*
    ** Constructor
     */
    public UserPlayerManager(@NonNull final View viewContainer, @NonNull final List<UserMonster> team) {
        super(viewContainer);

        //Init internal field
        mTeam = team;

        //Setup recycler
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(viewContainer.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMonsters.setAdapter(mAdapter);

        //Populate monster list
        for (UserMonster userMonster : mTeam) {
            mAdapter.add(userMonster.info());
        }
    }

    /*
    ** public method
     */
    @Nullable
    public UserMonster getNextMonster() {
        if (mTeam.isEmpty()) {
            return null;
        }
        return mTeam.get(0);
    }

    /*
    ** PlayerViewManager Implementation
     */
    @Override
    public void updateCurrentMonsterLife(@NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster) {
        LogHelper.d(TAG, "updateCurrentMonsterLife: " + monster.toString());

        //Populate widget
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));
    }

    @Override
    public void displayAttackReceive() {
        //Set background to red
        mViewContainer.setBackgroundColor(Color.RED);

        //Set background to normal after 1s
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 1s
                mViewContainer.setBackgroundColor(Color.TRANSPARENT);
            }
        }, 1000);
    }

    @Override
    public void displayAttackSend() {
        LogHelper.d(TAG, "displayAttackSend");
    }

    @Override
    public void onMonsterKo(final long monster) {
        LogHelper.d(TAG, "onMonsterKo > monster=" + monster);

        mAdapter.clear();

        for (UserMonster userMonster : mTeam) {
            if (userMonster.userMonsterId == mCurrentMonster.userMonsterId) {
                mTeam.remove(userMonster);
            } else {
                mAdapter.add(userMonster.info());
            }
        }
    }

    @Override
    public void onCurrentMonsterChanged() {
        Context context = mViewContainer.getContext();

        //Populate widget;
        monsterName.setText(mCurrentMonster.name);
        monsterLvl.setText(String.format(context.getString(R.string.combat_msg_monster_lvl), mCurrentMonster.level));
        progressBarMonsterHp.setMax(mCurrentMonster.hp);
        progressBarMonsterHp.setProgress(mCurrentMonster.hp);
        monsterLife.setText(String.valueOf(mCurrentMonster.hp));

        //Populate monster sprite
        Monster monsterInfos = mCurrentMonster.info();
        if (monsterInfos != null) {
            Glide.with(context)
                    .load(monsterInfos.sprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .centerCrop()
                    .into(monsterPicture);
        }
    }
}
