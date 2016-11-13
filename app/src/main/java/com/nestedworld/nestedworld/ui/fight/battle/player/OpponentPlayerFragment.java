package com.nestedworld.nestedworld.ui.fight.battle.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.nestedworld.nestedworld.ui.base.BattlePlayerFragment;

import butterknife.BindView;

public class OpponentPlayerFragment extends BattlePlayerFragment {

    private final static String TAG = OpponentPlayerFragment.class.getSimpleName();
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
    ** Public method
     */
    public static OpponentPlayerFragment load(@NonNull final FragmentManager fragmentManager, final int teamSize) {
        OpponentPlayerFragment opponentPlayerManager = new OpponentPlayerFragment();

        Bundle args = new Bundle();
        args.putInt("TEAM_SIZE", teamSize);
        opponentPlayerManager.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.container_opponent, opponentPlayerManager)
                .commit();

        return opponentPlayerManager;
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_battle_opponent;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Init internal field
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMonsters.setAdapter(mAdapter);

        for (int i = 0; i < getArguments().getInt("TEAM_SIZE", 0); i++) {
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
        getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.material_red_500));

        //Set background to normal after 1s
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 1s
                getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.WhiteSmokeHalf));
            }
        }, 1000);
    }

    @Override
    public void displayAttackSend() {
        LogHelper.d(TAG, "displayAttackSend");
    }

    @Override
    public void displayMonsterKo(@NonNull StartMessage.StartMessagePlayerMonster monster) {
        viewMonsterDetailContainer.setBackgroundColor(Color.RED);

        mAdapter.clear();
        for (int i = 0; i < mRemainingMonster; i++) {
            mAdapter.add(null);
        }
    }

    @Override
    protected void displayMonsterDetails(@NonNull StartMessage.StartMessagePlayerMonster monster) {
        Context context = recyclerViewMonsters.getContext();

        monsterName.setText(monster.name);
        monsterLvl.setText(String.format(context.getString(R.string.combat_msg_monster_lvl), monster.level));
        progressBarMonsterHp.setMax(monster.hp);
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));
        viewMonsterDetailContainer.setBackgroundColor(Color.TRANSPARENT);

        //Populate monster sprite
        Monster monsterInfos = monster.info();
        if (monsterInfos != null) {
            Glide.with(context)
                    .load(monsterInfos.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .into(monsterPicture);
        }
    }
}
