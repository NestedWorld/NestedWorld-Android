package com.nestedworld.nestedworld.ui.fight.battle.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.nestedworld.nestedworld.ui.fight.battle.player.base.PlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpponentPlayerManager extends PlayerManager {

    private final static String TAG = OpponentPlayerManager.class.getSimpleName();
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
    @BindView(R.id.container_monster_detail)
    View viewMonsterDetailContainer;
    @BindView(R.id.RecyclerView_battle_monster)
    RecyclerView recyclerViewMonsters;

    /*
    ** Constructor
     */
    public OpponentPlayerManager(@NonNull final View viewContainer, final int teamSize) {
        super(viewContainer, teamSize);

        //Init internal field
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(viewContainer.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMonsters.setAdapter(mAdapter);

        for (int i = 0; i < teamSize; i++) {
            mAdapter.add(null);
        }
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
    public void displayMonsterKo() {
        viewMonsterDetailContainer.setBackgroundColor(Color.RED);

        int newAdapterLength = mAdapter.getItemCount() - 1;
        mAdapter.clear();

        for (int i = 0; i < newAdapterLength; i++) {
            mAdapter.add(null);
        }
    }

    @Override
    public void displayCurrentMonster() {
        Context context = recyclerViewMonsters.getContext();

        monsterName.setText(mCurrentMonster.name);
        monsterLvl.setText(String.format(context.getString(R.string.combat_msg_monster_lvl), mCurrentMonster.level));
        progressBarMonsterHp.setMax(mCurrentMonster.hp);
        progressBarMonsterHp.setProgress(mCurrentMonster.hp);
        monsterLife.setText(String.valueOf(mCurrentMonster.hp));
        viewMonsterDetailContainer.setBackgroundColor(Color.TRANSPARENT);

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
