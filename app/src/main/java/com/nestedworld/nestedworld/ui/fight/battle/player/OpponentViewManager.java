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
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.BasePlayerViewManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OpponentViewManager extends BasePlayerViewManager {

    private final static String TAG = OpponentViewManager.class.getSimpleName();
    private final StartMessage.StartMessageOpponent mPlayer;
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
    private BattleMonsterAdapter mAdapter = new BattleMonsterAdapter();

    /*
    ** Constructor
     */
    public OpponentViewManager(@NonNull final StartMessage.StartMessageOpponent player, @NonNull final View viewContainer) {
        super(viewContainer);

        //Init internal field
        mPlayer = player;
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
        //TODO display attack
    }

    @Override
    public void onMonsterKo(long monster) {
        LogHelper.d(TAG, "onMonsterKo > monster=" + monster);
        updateAdapterContent();
    }

    @Override
    public BasePlayerViewManager setCurrentMonster(@NonNull StartMessage.StartMessagePlayerMonster monster, @NonNull ArrayList<MonsterAttackResponse.MonsterAttack> attacks) {
        mTeam.add(monster.info());
        return super.setCurrentMonster(monster, attacks);
    }

    private List<Monster> mTeam = new ArrayList<>();

    @Override
    public void build(@NonNull final Context context) {
        super.build(context);

        //Init monster list
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        //Populate monster list and set adapter
        updateAdapterContent();
        recyclerViewMonsters.setAdapter(mAdapter);

        //Populate widget
        StartMessage.StartMessagePlayerMonster monster = mPlayer.monster;
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

    /*
    ** Internal method
     */
    private void updateAdapterContent() {
        //Clear old content
        mAdapter.clear();

        //Add known monster
        mAdapter.addAll(mTeam);

        //Complete with unknown monster
        for (int i = mTeam.size(); i < mPlayer.monsterCount; i++) {
            mAdapter.add(null);
        }
    }
}
