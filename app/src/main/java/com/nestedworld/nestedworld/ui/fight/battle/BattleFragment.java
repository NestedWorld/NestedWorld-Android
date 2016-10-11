package com.nestedworld.nestedworld.ui.fight.battle;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.events.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatEndEvent;
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
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import retrofit2.Response;

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
    @BindViews({
            R.id.imageView_top,
            R.id.imageView_top_right,
            R.id.imageView_bottom_right,
            R.id.imageView_bottom,
            R.id.imageView_bottom_left,
            R.id.imageView_top_left})
    List<ImageView> mTitles;
    /*
    ** Private field
     */
    private final ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = new ArrayList<>();
    private String mUserGestureInput = "";
    private final DrawingGestureListener mDrawingGestureListener = new DrawingGestureListener() {
        @Override
        public void onTouch(int tileId) {
            switch (tileId) {
                case R.id.imageView_top:
                    mUserGestureInput += "1";
                    break;
                case R.id.imageView_top_right:
                    mUserGestureInput += "2";
                    break;
                case R.id.imageView_bottom_right:
                    mUserGestureInput += "3";
                    break;
                case R.id.imageView_bottom:
                    mUserGestureInput += "4";
                    break;
                case R.id.imageView_bottom_left:
                    mUserGestureInput += "5";
                    break;
                case R.id.imageView_top_left:
                    mUserGestureInput += "6";
                    break;
                default:
                    break;
            }
        }
    };
    private DrawingGestureView mDrawingGestureView;
    private StartMessage mStartMessage = null;
    private List<UserMonster> mUserMonsterAlive = null;
    private StartMessage.StartMessagePlayerMonster mCurrentUserMonster;
    private StartMessage.StartMessagePlayerMonster mCurrentOpponentMonster;
    private final OnFinishMoveListener mOnFinishMoveListener = new OnFinishMoveListener() {
        @Override
        public void onFinish() {
            sendAttack();
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
        mUserMonsterAlive = team;
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
        if (mUserMonsterAlive == null || mStartMessage == null) {
            throw new IllegalArgumentException("You should call setStartMessage() and setTeam() before binding the fragment");
        }

        //start loading animation
        progressView.start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        /*Hide action bar*/
        setupActionBar();

        /*Init field*/
        mCurrentUserMonster = mStartMessage.user.monster;
        mCurrentOpponentMonster = mStartMessage.opponent.monster;

        /*populate the view*/
        setupEnvironment();
        setupOpponentInfos(mStartMessage.opponent);
        setupPlayerInfos(mStartMessage.user);

        /*Init the gestureListener*/
        initDrawingGestureView(rootView);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
               /*Load pre-requisite*/
                retrieveCurrentUserMonsterAttacks();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Enable drawingGestureView (allow user to send attack)
                enableDrawingGestureView(true);

                //Stop loading animation
                progressView.stop();
            }
        }.execute();
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
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        AttackReceiveMessage message = event.getMessage();

        if (message.target.id == mCurrentUserMonster.id) {
            LogHelper.d(TAG, "UserMonster is attacked:" + message.toString());
            //If UserMonster is the target (IE opponentMonster is attacking)

            //Update userMonster Life
            updateMonsterLife(layoutPlayer, message.target);

            //Show opponentMonster attacking
            displayAttackAnimation(layoutPlayer, layoutOpponent, mCurrentOpponentMonster.info());
        } else {
            LogHelper.d(TAG, "Opponent monster is attacked:" + message.toString());
            //If Opponent is the target (IE userMonster is Attacking)

            //Update opponentMonster Life
            updateMonsterLife(layoutOpponent, message.target);
            displayAttackAnimation(layoutOpponent, layoutPlayer, mCurrentUserMonster.info());
        }
    }

    @Subscribe
    public void onMonsterKo(OnMonsterKoEvent event) {
        MonsterKoMessage monsterKoMessage = event.getMessage();

        if (monsterKoMessage.monster == mCurrentUserMonster.id) {
            for (UserMonster userMonster : mUserMonsterAlive) {
                if (userMonster.user_monster_id == mCurrentUserMonster.userMonsterId) {
                    mUserMonsterAlive.remove(userMonster);
                }
            }
        }

        //TODO parse message
        //TODO add possibility to replace monster if it's our monster
    }

    @Subscribe
    public void onCombatEnd(OnCombatEndEvent onCombatEndEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        Toast.makeText(mContext, "Fight ended", Toast.LENGTH_LONG).show();

        //TODO parse message and display result page
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
                actionBar.hide();
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

    private void initDrawingGestureView(@NonNull final View rootView) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        /*Create and init the drawingGestureView*/
        mDrawingGestureView = new DrawingGestureView(mContext);
        mDrawingGestureView.setEnabled(false);
        mDrawingGestureView.setTiles(mTitles);

        /*
        ** We don't add listener into the gestureView now
        *  We'll add the listener after the pre-requisite loading
        */

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.layout_fight_body)).addView(mDrawingGestureView);
    }

    private void setupOpponentInfos(@NonNull final StartMessage.StartMessageOpponent opponent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve his monster
        StartMessage.StartMessagePlayerMonster monster = opponent.monster;

        //Init monster list
        RecyclerView monstersList = (RecyclerView) layoutOpponent.findViewById(R.id.listview_opponentMonster);
        monstersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //Populate monster list
        BattleMonsterAdapter battleMonsterAdapter = new BattleMonsterAdapter();
        battleMonsterAdapter.add(opponent.monster.info(), BattleMonsterAdapter.Status.SELECTED);
        for (int i = 1; i < opponent.monsterCount; i++) {
            battleMonsterAdapter.add(null, BattleMonsterAdapter.Status.DEFAULT);
        }
        monstersList.setAdapter(battleMonsterAdapter);

        //Populate opponent monster info with his current monster
        setUpMonsterContainer(layoutOpponent, monster);
    }

    private void setupPlayerInfos(@NonNull final StartMessage.StartMessagePlayer player) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Init player monster list
        RecyclerView monstersList = (RecyclerView) layoutPlayer.findViewById(R.id.listview_playerMonster);
        monstersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //Populate player monster list
        BattleMonsterAdapter battleMonsterAdapter = new BattleMonsterAdapter();
        for (int i = 0; i < mUserMonsterAlive.size(); i++) {
            UserMonster userMonster = mUserMonsterAlive.get(i);
            if (userMonster.user_monster_id == mCurrentUserMonster.userMonsterId) {
                battleMonsterAdapter.add(userMonster.info(), BattleMonsterAdapter.Status.SELECTED);
            } else {
                battleMonsterAdapter.add(userMonster.info(), BattleMonsterAdapter.Status.DEFAULT);
            }
        }
        monstersList.setAdapter(battleMonsterAdapter);

        //Populate player monster info with his currentMonster
        setUpMonsterContainer(layoutPlayer, player.monster);
    }

    private void setUpMonsterContainer(@NonNull final View container, @NonNull final StartMessage.StartMessagePlayerMonster monster) {
        LogHelper.d(TAG, "setUpMonsterContainer: " + monster.toString());

        //Retrieve widget
        TextView monsterLvl = (TextView) container.findViewById(R.id.textview_monster_lvl);
        TextView monsterName = (TextView) container.findViewById(R.id.textview_monster_name);
        ImageView monsterPicture = (ImageView) container.findViewById(R.id.imageView_monster);
        ProgressBar progressBarMonsterHp = (ProgressBar) container.findViewById(R.id.progressBar_MonsterLife);
        TextView monsterLife = (TextView) container.findViewById(R.id.textview_MonsterLife);

        //Populate widget;
        monsterName.setText(monster.name);
        monsterLvl.setText(String.format(getString(R.string.combat_msg_monster_lvl), monster.level));
        progressBarMonsterHp.setMax(monster.hp);
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));

        //Populate monster sprite
        Monster monsterInfos = monster.info();
        if (monsterInfos != null) {
            Glide.with(mContext)
                    .load(monsterInfos.sprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .centerCrop()
                    .into(monsterPicture);
        }
    }

    private void updateMonsterLife(@NonNull final View container, @NonNull final AttackReceiveMessage.AttackReceiveMessageMonster monster) {
        LogHelper.d(TAG, "updateMonsterLife: " + monster.toString());

        //Retrieve widget
        ProgressBar progressBarMonsterHp = (ProgressBar) container.findViewById(R.id.progressBar_MonsterLife);
        TextView monsterLife = (TextView) container.findViewById(R.id.textview_MonsterLife);

        //Populate widget
        progressBarMonsterHp.setProgress(monster.hp);
        monsterLife.setText(String.valueOf(monster.hp));
    }

    private void displayAttackAnimation(@NonNull final View targetLayout, @NonNull final View attackerLayout, @NonNull final Monster monster) {
        LogHelper.d(TAG, "displayAttackAnimation");

        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Set background to red
        targetLayout.setBackgroundColor(Color.RED);

        //Set background to normal after 1s
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                // Actions to do after 1s
                targetLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }, 1000);
    }

    private void enableDrawingGestureView(final boolean enable) {
        if (enable) {
            mDrawingGestureView.setEnabled(true);
            mDrawingGestureView.setOnFinishMoveListener(mOnFinishMoveListener);
            mDrawingGestureView.setOnTileTouchListener(mDrawingGestureListener);
        } else {
            mDrawingGestureView.setEnabled(false);
            mDrawingGestureView.setOnFinishMoveListener(null);
            mDrawingGestureView.setOnTileTouchListener(null);
        }
    }

    private void sendAttack() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve and clear user gesture
        Attack.AttackType attackTypeWanted = gestureToAttackType(mUserGestureInput);
        mUserGestureInput = "";

        //Parse user gesture
        switch (attackTypeWanted) {
            case UNKNOWN:
                //Unknow attack, display error message
                Toast.makeText(mContext, "Unknown attack type", Toast.LENGTH_SHORT).show();
                break;
            case OBJECT_USE:
                //Feature not supported yet, display error message
                Toast.makeText(mContext, "Feature incoming", Toast.LENGTH_SHORT).show();
                break;
            default:
                //If we're here, it means the user want to send: attack || attackSp || defense || defenceSp
                sendAttackRequest(attackTypeWanted);
                break;
        }
    }

    private void sendAttackRequest(@NonNull final Attack.AttackType attackTypeWanted) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Check if the current monster has an attack of the wanted type
        final MonsterAttackResponse.MonsterAttack attack = getCurrentMonsterAttackByType(attackTypeWanted);
        if (attack == null) {
            //Current monster don't have any attack of the wantend type, just display error message
            Toast.makeText(mContext, "Your monster didn't have this kind of attack", Toast.LENGTH_SHORT).show();
            return;
        }

        //Current monster have an attack with the wanted type
        //We bind this fragment to the socketService for sending the attackRequest
        ServiceHelper.bindToSocketService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Service connected, retrieving socketApi instance
                NestedWorldSocketAPI nestedWorldSocketAPI = ((SocketService.LocalBinder) service).getService().getApiInstance();

                if (nestedWorldSocketAPI != null) {
                    //Sending request
                    SendAttackRequest request = new SendAttackRequest(mStartMessage.combatId, mCurrentOpponentMonster.id, attack.infos.attack_id);
                    nestedWorldSocketAPI.sendRequest(request, SocketMessageType.MessageKind.TYPE_COMBAT_SEND_ATTACK);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //Cannot send attack (api not available)
                //Display error message
                Toast.makeText(mContext, R.string.combat_msg_send_atk_failed, Toast.LENGTH_LONG).show();

                //Stop loading animation and re-enable drawingGestureView
                progressView.stop();
                enableDrawingGestureView(true);
            }
        });
    }

    @Nullable
    private MonsterAttackResponse.MonsterAttack getCurrentMonsterAttackByType(@NonNull final Attack.AttackType attackTypeWanted) {
        //Loop over current monster attack for finding an attack of the given type
        for (MonsterAttackResponse.MonsterAttack monsterAttack : mCurrentMonsterAttacks) {
            if (monsterAttack.infos.getType() == attackTypeWanted) {
                return monsterAttack;
            }
        }
        return null;
    }

    private void retrieveCurrentUserMonsterAttacks() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        try {
            //Retrieve the current monster attack
            Response<MonsterAttackResponse> response = NestedWorldHttpApi.getInstance(mContext).getMonsterAttack(mCurrentUserMonster.monsterId).execute();
            if (response == null || response.body() == null) {
                //Can not retrieve monster attack
                //Display error message to warn the user
                Toast.makeText(mContext, "Can not retrieve your monster attack", Toast.LENGTH_LONG).show();

                //Finish the battle
                ((BaseAppCompatActivity)mContext).finish();
            } else if (response.body().attacks.isEmpty()) {
                //The monster didn't have any attack, just warn the user
                Toast.makeText(mContext, "Your monster didn't have any attack", Toast.LENGTH_LONG).show();
            } else {
                //Clear hold attack
                mCurrentMonsterAttacks.clear();

                //Populate attack list
                mCurrentMonsterAttacks.addAll(response.body().attacks);
            }
        } catch (IOException e) {
            //Something wrong happen, we can't retrieve monster attack
            e.printStackTrace();

            //Display error message to warn the user
            Toast.makeText(mContext, "Can not retrieve your monster attack", Toast.LENGTH_LONG).show();

            //Finish the battle
            ((BaseAppCompatActivity)mContext).finish();
        }
    }

    /*
    ** Utils
     */
    @NonNull
    private static Attack.AttackType gestureToAttackType(@NonNull final String gestureInput) {
        LogHelper.d(BattleFragment.class.getSimpleName(), "gestureToAttackType > gestureInput=" + gestureInput);

        Attack.AttackType attackType;
        switch (gestureInput) {
            case "41":
                attackType = Attack.AttackType.ATTACK;
                break;
            case "62":
                attackType = Attack.AttackType.DEFENSE;
                break;
            case "456123":
                attackType = Attack.AttackType.ATTACK_SP;
                break;
            case "432165":
                attackType = Attack.AttackType.DEFENSE_SP;
                break;
            case "6253":
                attackType = Attack.AttackType.OBJECT_USE;
                break;
            default:
                attackType = Attack.AttackType.UNKNOWN;
        }

        return attackType;
    }
}
