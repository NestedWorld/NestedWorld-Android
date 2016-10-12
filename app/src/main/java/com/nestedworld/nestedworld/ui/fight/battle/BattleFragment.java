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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.customView.drawingGestureView.DrawingGestureView;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.database.models.Attack;
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
import com.nestedworld.nestedworld.ui.fight.battle.player.OpponentViewManager;
import com.nestedworld.nestedworld.ui.fight.battle.player.UserViewManager;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.BasePlayerViewManager;
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
    ** Private field
     */
    private final ArrayList<MonsterAttackResponse.MonsterAttack> mCurrentMonsterAttacks = new ArrayList<>();
    /*
    ** Butterknife binding
     */
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.layout_user)
    View layoutUser;
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
    private BasePlayerViewManager userViewManager;
    private BasePlayerViewManager opponentViewManager;

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

        /*init internal field*/
        mCurrentUserMonster = mStartMessage.user.monster;
        mCurrentOpponentMonster = mStartMessage.opponent.monster;
        userViewManager = new UserViewManager(mStartMessage.user, layoutUser).setTeam(mUserMonsterAlive);
        opponentViewManager = new OpponentViewManager(mStartMessage.opponent, layoutOpponent);

        /*setup the view*/
        setupEnvironment();
        setupActionBar();
        userViewManager.setupUI(mContext);
        opponentViewManager.setupUI(mContext);

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

        //Retrieve message
        AttackReceiveMessage message = event.getMessage();

        BasePlayerViewManager attacker;
        BasePlayerViewManager target;
        if (message.target.id == mCurrentUserMonster.id) {
            //Attack sender is the user
            attacker = opponentViewManager;
            target = userViewManager;
        } else {
            attacker = userViewManager;
            target = opponentViewManager;
        }

        //Update monsters life
        target.updateCurrentMonsterLife(message.target);
        attacker.updateCurrentMonsterLife(message.monster);

        //Display animation
        attacker.displayAttackSend();
        target.displayAttackReceive();
    }

    @Subscribe
    public void onMonsterKo(OnMonsterKoEvent event) {
        MonsterKoMessage monsterKoMessage = event.getMessage();

        if (monsterKoMessage.monster == mCurrentUserMonster.id) {
            for (UserMonster userMonster : mUserMonsterAlive) {
                if (userMonster.userMonsterId == mCurrentUserMonster.userMonsterId) {
                    mUserMonsterAlive.remove(userMonster);

                    userViewManager.onMonsterKo(userMonster.info());
                }
            }
        }
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
                    SendAttackRequest request = new SendAttackRequest(mStartMessage.combatId, mCurrentOpponentMonster.id, attack.infos.attackId);
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
                ((BaseAppCompatActivity) mContext).finish();
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
            ((BaseAppCompatActivity) mContext).finish();
        }
    }
}
