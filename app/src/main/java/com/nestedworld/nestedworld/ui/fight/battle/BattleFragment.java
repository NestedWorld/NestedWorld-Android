package com.nestedworld.nestedworld.ui.fight.battle;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.customView.drawingGestureView.DrawingGestureView;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.events.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnMonsterKoEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.request.combat.SendAttackRequest;
import com.nestedworld.nestedworld.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class BattleFragment extends BaseFragment {

    /*
    ** Butterknife binding
     */
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.layout_player)
    View layoutPlayer;
    @BindView(R.id.layout_opponent)
    View layoutOpponent;

    /*
    ** Private field
     */
    private final ArrayList<Integer> mPositions = new ArrayList<>();
    private DrawingGestureView mDrawingGestureView;
    private StartMessage mStartMessage = null;
    private List<UserMonster> mTeam = null;
    private final Map<UserMonster, ArrayList<MonsterAttackResponse.MonsterAttack>> mTeamAttack = new HashMap<>();
    private final OnFinishMoveListener mOnFinishMoveListener = new OnFinishMoveListener() {
        @Override
        public void onFinish() {
            sendAttack();
        }
    };
    private final DrawingGestureListener mDrawingGestureListener = new DrawingGestureListener() {
        @Override
        public void onTouch(int tileId) {
            if (!mPositions.contains(tileId)) {
                mPositions.add(tileId);
            }
        }
    };

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final StartMessage startMessage, @NonNull final List<UserMonster> selectedMonster) {
        BattleFragment fightFragment = new BattleFragment();
        fightFragment.setStartMessage(startMessage);
        fightFragment.setTeam(selectedMonster);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fightFragment);
        fragmentTransaction.commit();
    }

    public void setStartMessage(@NonNull final StartMessage startMessage) {
        mStartMessage = startMessage;
    }

    public void setTeam(@NonNull final List<UserMonster> team) {
        mTeam = team;
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_battle;
    }

    @Override
    protected void init(@NonNull final View rootView, @Nullable Bundle savedInstanceState) {
        if (mTeam == null || mStartMessage == null) {
            throw new IllegalArgumentException("You should call setStartMessage() and setTeam() before binding the fragment");
        }

        //start loading animation
        progressView.start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupActionBar();

        /*populate the view*/
        setupEnvironment();
        initOpponentInfos(mStartMessage.opponent);
        initPlayerInfos(mStartMessage.user);

        /*Init the gestureListener*/
        initDrawingGestureView(rootView);

        /*Retrieve mTeams attacks (and populate mTeamAttack) */
        retrieveMonstersAttacks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onAttackReceive(OnAttackReceiveEvent event) {
        AttackReceiveMessage message = event.getMessage();

        //TODO parse message
    }

    @Subscribe
    public void onMonsterKo(OnMonsterKoEvent event) {
        MonsterKoMessage monsterKoMessage = event.getMessage();

        //TODO parse message
    }


    /*
    ** Private method
     */
    private void setupActionBar() {
        //Check if fragment hasn't been detach
        if (mContext != null) {
            /*Update toolbar title*/
            ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.combat_title));
            }
        }
    }

    private void setupEnvironment() {
        //TODO parse mStartMessage.env and set background

        switch (mStartMessage.env) {
            case "city":
                break;
            default:
                break;
        }
    }

    private void initDrawingGestureView(View rootView) {
        if (mContext == null) {
            return;
        }

        /*We create a list with every tile*/
        final List<ImageView> tiles = Arrays.asList(
                (ImageView) rootView.findViewById(R.id.imageView_top),
                (ImageView) rootView.findViewById(R.id.imageView_top_right),
                (ImageView) rootView.findViewById(R.id.imageView_bottom_right),
                (ImageView) rootView.findViewById(R.id.imageView_bottom),
                (ImageView) rootView.findViewById(R.id.imageView_bottom_left),
                (ImageView) rootView.findViewById(R.id.imageView_top_left));

        /*Create and init the custom view*/
        mDrawingGestureView = new DrawingGestureView(mContext);
        mDrawingGestureView.setEnabled(false);
        mDrawingGestureView.setTiles(tiles);

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.layout_fight_body)).addView(mDrawingGestureView);
    }

    private void updateMonsterContainer(@NonNull final View container, @NonNull final StartMessage.PlayerMonster monster) {
        //Retrieve widget
        TextView monsterLvl = (TextView) container.findViewById(R.id.textview_monster_lvl);
        TextView monsterName = (TextView) container.findViewById(R.id.textview_monster_name);
        ImageView monsterPicture = (ImageView) container.findViewById(R.id.imageView_monster);
        ProgressBar progressBarMonsterHp = (ProgressBar) container.findViewById(R.id.progressBar_MonsterLife);

        //Populate widget
        monsterName.setText(monster.name);
        monsterLvl.setText(String.format(getString(R.string.combat_msg_monster_lvl), monster.level));
        progressBarMonsterHp.setMax(monster.hp);
        progressBarMonsterHp.setProgress(monster.hp);

        //Populate monster sprite
        Monster monsterInfos = monster.infos();
        if (monsterInfos != null) {
            Glide.with(mContext)
                    .load(monsterInfos.sprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .centerCrop()
                    .into(monsterPicture);
        }
    }

    private void initOpponentInfos(@NonNull final StartMessage.Opponent opponent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve his monster
        StartMessage.PlayerMonster monster = opponent.monster;

        //Init monster list
        RecyclerView monstersList = (RecyclerView) layoutOpponent.findViewById(R.id.listview_opponentMonster);
        monstersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //Populate monster list
        BattleMonsterAdapter battleMonsterAdapter = new BattleMonsterAdapter();
        battleMonsterAdapter.add(opponent.monster.infos());
        for (int i = 1; i < opponent.monsterCount; i++) {
            battleMonsterAdapter.add(null);
        }
        monstersList.setAdapter(battleMonsterAdapter);

        //Populate opponent monster infos with his current monster
        updateMonsterContainer(layoutOpponent, monster);
    }

    private void initPlayerInfos(@NonNull final StartMessage.Player player) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Init player monster list
        RecyclerView monstersList = (RecyclerView) layoutPlayer.findViewById(R.id.listview_playerMonster);
        monstersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //Populate player monster list
        BattleMonsterAdapter battleMonsterAdapter = new BattleMonsterAdapter();
        for (int i = 0; i < mTeam.size(); i++) {
            battleMonsterAdapter.add(mTeam.get(i).info());
        }
        monstersList.setAdapter(battleMonsterAdapter);

        //Populate player monster infos with his currentMonster
        updateMonsterContainer(layoutPlayer, player.monster);
    }

    private void sendAttack() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ServiceHelper.bindToSocketService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NestedWorldSocketAPI nestedWorldSocketAPI = ((SocketService.LocalBinder) service).getService().getApiInstance();
                if (nestedWorldSocketAPI != null) {
                    SendAttackRequest data = new SendAttackRequest(mStartMessage.opponent.monster.id, 10);
                    nestedWorldSocketAPI.sendRequest(data, SocketMessageType.MessageKind.TYPE_COMBAT_SEND_ATTACK);
                    mPositions.clear();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(mContext, R.string.combat_msg_send_atk_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retrieveMonstersAttacks() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        final NestedWorldHttpApi nestedWorldHttpApi = NestedWorldHttpApi.getInstance(mContext);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < mTeam.size(); i++) {
                    LogHelper.d(TAG, "Retrieve attacks (task: " + i + ")");

                    final UserMonster userMonster = mTeam.get(i);
                    final Monster userMonsterInfo = userMonster.info();
                    if (userMonsterInfo == null) {
                        throw new IllegalArgumentException("UserMonter not link to any monster");
                    }

                    try {
                         mTeamAttack.put(userMonster, nestedWorldHttpApi.getMonsterAttack(userMonsterInfo.getId()).execute().body().attacks);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Enable drawingGestureView (allow user to send attack)
                enableDrawingGestureView(true);

                //Stop loading animation
                progressView.stop();
            }
        }.execute();
    }

    private void enableDrawingGestureView(final boolean enable) {
        if (enable) {
            mDrawingGestureView.setEnabled(true);
            mDrawingGestureView.setOnFinishMoveListener(mOnFinishMoveListener);
            mDrawingGestureView.setOnTileTouchListener(mDrawingGestureListener);
        } else {
            //Didnd't need to remove listener
            mDrawingGestureView.setEnabled(false);
            mDrawingGestureView.setOnFinishMoveListener(null);
            mDrawingGestureView.setOnTileTouchListener(null);
        }
    }
}
