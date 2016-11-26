package com.nestedworld.nestedworld.ui.fight.battle.player;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BattlePlayerFragment;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

public class UserPlayerFragment extends BattlePlayerFragment {

    private final static String TAG = UserPlayerFragment.class.getSimpleName();

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
    @BindView(R.id.container_monster_detail)
    View viewMonsterDetailContainer;

    private List<UserMonster> mTeam = null;

    /*
    ** Public method
     */
    public static UserPlayerFragment load(@NonNull final FragmentManager fragmentManager, @NonNull final List<UserMonster> team) {
        UserPlayerFragment userPlayerFragment = new UserPlayerFragment().setTeam(team);

        fragmentManager.beginTransaction()
                .replace(R.id.container_player, userPlayerFragment)
                .commit();

        return userPlayerFragment;
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_battle_player;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Check params
        if (mTeam == null) {
            Toast.makeText(mContext, "Can't fight without monster !", Toast.LENGTH_LONG).show();
            ((BaseAppCompatActivity) mContext).finish();
        }

        //Setup recycler
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
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
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Set background to red
        getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.material_red_500));

        //Set background to normal after 1s
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

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
    public void displayMonsterKo(@NonNull StartMessage.StartMessagePlayerMonster monsterKo) {
        LogHelper.d(TAG, "displayMonsterKo");
        viewMonsterDetailContainer.setBackgroundColor(Color.RED);

        Iterator<UserMonster> iterator = mTeam.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().userMonsterId == monsterKo.userMonsterId) {
                iterator.remove();

                mAdapter.clear();
                for (UserMonster userMonster : mTeam) {
                    mAdapter.add(userMonster.info());
                }
                return;
            }
        }
    }

    @Override
    protected void displayMonsterDetails(@NonNull StartMessage.StartMessagePlayerMonster monster) {
        //Populate widget;
        monsterName.setText(monster.name);
        monsterLvl.setText(String.format(getString(R.string.combat_msg_monster_lvl), monster.level));
        progressBarMonsterHp.setMax(monster.hp);
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));
        viewMonsterDetailContainer.setBackgroundColor(Color.TRANSPARENT);

        //Populate monster sprite
        Monster monsterInfos = monster.info();
        if (monsterInfos != null) {
            Glide.with(mContext)
                    .load(monsterInfos.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .into(monsterPicture);
        }
    }

    /*
    ** Internal method
     */
    private UserPlayerFragment setTeam(@NonNull final List<UserMonster> team) {
        mTeam = team;
        return this;
    }
}
