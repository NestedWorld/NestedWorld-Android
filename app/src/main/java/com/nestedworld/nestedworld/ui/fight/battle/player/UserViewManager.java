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
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.fight.battle.BattleMonsterAdapter;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.BasePlayerViewManager;

import java.util.List;

import butterknife.BindView;

public class UserViewManager extends BasePlayerViewManager {

    private final static String TAG = UserViewManager.class.getSimpleName();
    private final StartMessage.StartMessagePlayer mPlayer;
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
    private List<UserMonster> mUserMonsterAlive = null;

    /*
    ** Constructor
     */
    public UserViewManager(@NonNull final StartMessage.StartMessagePlayer player, @NonNull final View viewContainer) {
        super(viewContainer);
        mPlayer = player;
    }

    /*
    ** Public method
     */
    public UserViewManager setTeam(@NonNull final List<UserMonster> team) {
        mUserMonsterAlive = team;
        return this;
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
    public void onMonsterKo(final long monster) {
        for (UserMonster userMonster : mUserMonsterAlive) {
            if (userMonster.userMonsterId == getCurrentMonster().userMonsterId) {
                mUserMonsterAlive.remove(userMonster);
                battleMonsterAdapter.replace(userMonster.info(), BattleMonsterAdapter.Status.DEAD);
            }
        }
    }

    @Override
    public boolean hasMonster(long id) {
        return false;
    }

    /*
    ** Internal method
     */
    @Override
    public void build(@NonNull final Context context) {
        //Init monster list
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        //Populate monster list
        battleMonsterAdapter.add(mPlayer.monster.info(), BattleMonsterAdapter.Status.SELECTED);
        for (UserMonster userMonster : mUserMonsterAlive) {
            battleMonsterAdapter.add(userMonster.info(), BattleMonsterAdapter.Status.DEFAULT);
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
