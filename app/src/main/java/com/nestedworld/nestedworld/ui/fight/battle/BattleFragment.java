package com.nestedworld.nestedworld.ui.fight.battle;

import android.content.ComponentName;
import android.content.Context;
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
    private List<UserMonster> mUserTeam = null;
    private BasePlayerViewManager mUserViewManager;
    private final OnFinishMoveListener mOnFinishMoveListener = new OnFinishMoveListener() {
        @Override
        public void onFinish() {
            sendAttack();
        }
    };
    private BasePlayerViewManager mOpponentViewManager;

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

    @Nullable
    private static ArrayList<MonsterAttackResponse.MonsterAttack> retrieveMonsterAttack(@NonNull final Context context, @NonNull final Monster monster) {
        try {
            //Retrieve current monster attack
            Response<MonsterAttackResponse> response = NestedWorldHttpApi.getInstance(context).getMonsterAttack(monster.monsterId).execute();
            if (response == null || response.body() == null) {
                //Can not retrieve monster attack
                return null;
            } else {
                //Success, return monster attack
                return response.body().attacks;
            }
        } catch (IOException e) {
            //Something wrong happen
            //Can not retrieve monster attack
            return null;
        }
    }

    public void setStartMessage(@NonNull final StartMessage startMessage) {
        mStartMessage = startMessage;
    }

    public void setTeam(@NonNull final List<UserMonster> team) {
        mUserTeam = team;
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
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }
        if (mUserTeam == null || mStartMessage == null) {
            //Display some log
            LogHelper.d(TAG, "You should call setStartMessage() and setTeam() before binding the fragment");

            //Warm the user we can't start this battle
            Toast.makeText(mContext, "Sorry, can't start your battle", Toast.LENGTH_LONG).show();

            //Stop current activity
            ((BaseAppCompatActivity) mContext).finish();
            return;
        }

        //start loading animation
        progressView.start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupEnvironment();
        setupActionBar();
        setupDrawingGestureView(rootView);
        setupPlayers();
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
        if (mUserViewManager.hasMonster(message.monster.id)) {
            //Attack sender is the user
            attacker = mOpponentViewManager;
            target = mUserViewManager;
        } else {
            attacker = mUserViewManager;
            target = mOpponentViewManager;
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
        MonsterKoMessage message = event.getMessage();

        if (mUserViewManager.hasMonster(message.monster)) {
            mUserViewManager.onMonsterKo(message.monster);
        } else {
            mOpponentViewManager.onMonsterKo(message.monster);
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

    private void setupDrawingGestureView(@NonNull final View rootView) {
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

    private void setupPlayers() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mUserViewManager = new UserViewManager(mStartMessage.user, layoutUser).setTeam(mUserTeam);
        mOpponentViewManager = new OpponentViewManager(mStartMessage.opponent, layoutOpponent);


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return null;
                }

                //TODO check if we successfully got monster attack
                mUserViewManager.setCurrentMonster(mStartMessage.user.monster, retrieveMonsterAttack(mContext, mStartMessage.user.monster.info()));
                mUserViewManager.setCurrentMonster(mStartMessage.opponent.monster, retrieveMonsterAttack(mContext, mStartMessage.opponent.monster.info()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Setup playerUI
                mUserViewManager.build(mContext);
                mOpponentViewManager.build(mContext);

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
                //Check if the current monster has an attack of the wanted type
                final MonsterAttackResponse.MonsterAttack attack = mUserViewManager.getMonsterAttackByType(attackTypeWanted);
                if (attack == null) {
                    //Current monster don't have any attack of the wantend type, just display error message
                    Toast.makeText(mContext, "Your monster didn't have this kind of attack", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sendAttackRequest(mUserViewManager.getCurrentMonster().id, attack.infos.attackId);
                }
                break;
        }
    }

    private void sendAttackRequest(final long targetId, final long attackId) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Current monster have an attack with the wanted type
        //We bind this fragment to the socketService for sending the attackRequest
        ServiceHelper.bindToSocketService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Service connected, retrieving socketApi instance
                NestedWorldSocketAPI nestedWorldSocketAPI = ((SocketService.LocalBinder) service).getService().getApiInstance();

                if (nestedWorldSocketAPI != null) {
                    //Sending request
                    SendAttackRequest request = new SendAttackRequest(mStartMessage.combatId, targetId, attackId);
                    nestedWorldSocketAPI.sendRequest(request, SocketMessageType.MessageKind.TYPE_COMBAT_SEND_ATTACK);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Cannot send attack (api not available)
                //Display error message
                Toast.makeText(mContext, R.string.combat_msg_send_atk_failed, Toast.LENGTH_LONG).show();

                //Stop loading animation and re-enable drawingGestureView
                progressView.stop();
                enableDrawingGestureView(true);
            }
        });
    }
}
