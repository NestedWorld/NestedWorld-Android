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
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.PlayerViewManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpponentViewManager implements PlayerViewManager {

    private final static String TAG = PlayerViewManager.class.getSimpleName();
    private final StartMessage.StartMessageOpponent mPlayer;
    private final View mViewContainer;
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
    private BattleMonsterAdapter battleMonsterAdapter = new BattleMonsterAdapter();

    /*
    ** Constructor
     */
    public OpponentViewManager(@NonNull final StartMessage.StartMessageOpponent player, @NonNull final View viewContainer) {
        //Init internal field
        mPlayer = player;
        mViewContainer = viewContainer;

        //Retrieve widget
        ButterKnife.bind(this, viewContainer);
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

    }

    @Override
    public void onMonsterKo(@Nullable final Monster monster) {
        battleMonsterAdapter.replace(monster, BattleMonsterAdapter.Status.DEAD);
    }

    /*
    ** Internal method
     */
    @Override
    public void setupUI(@NonNull final Context context) {
        //Init monster list
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        //Populate monster list
        battleMonsterAdapter.add(mPlayer.monster.info(), BattleMonsterAdapter.Status.SELECTED);
        for (int i = 1; i < mPlayer.monsterCount; i++) {
            battleMonsterAdapter.add(null, BattleMonsterAdapter.Status.DEFAULT);
        }
        recyclerViewMonsters.setAdapter(battleMonsterAdapter);

        StartMessage.StartMessagePlayerMonster monster = mPlayer.monster;

        //Populate widget;
        monsterName.setText(monster.name);
        monsterLvl.setText(String.format(context.getString(R.string.combat_msg_monster_lvl), monster.level));
        progressBarMonsterHp.setMax(monster.hp);
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));

        //Populate monster sprite
        Monster monsterInfos = monster.info();
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
