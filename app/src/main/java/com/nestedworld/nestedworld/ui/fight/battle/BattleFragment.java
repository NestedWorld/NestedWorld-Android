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
import com.nestedworld.nestedworld.network.socket.models.request.combat.ReplaceMonsterRequest;
import com.nestedworld.nestedworld.network.socket.models.request.combat.SendAttackRequest;
import com.nestedworld.nestedworld.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.fight.FightResultFragment;
import com.nestedworld.nestedworld.ui.fight.battle.player.OpponentPlayerManager;
import com.nestedworld.nestedworld.ui.fight.battle.player.UserPlayerManager;
import com.nestedworld.nestedworld.ui.fight.battle.player.base.PlayerManager;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
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
    private UserPlayerManager mUserPlayerManager;
    private OpponentPlayerManager mOpponentPlayerManager;
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

    private void setStartMessage(@NonNull final StartMessage startMessage) {
        mStartMessage = startMessage;
    }

    private void setTeam(@NonNull final List<UserMonster> team) {
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

        PlayerManager attacker;
        PlayerManager target;
        if (mUserPlayerManager.hasMonsterInFront(message.monster.id)) {
            LogHelper.d(TAG, "onAttackReceive > opponent");

            //Attack sender is the user
            attacker = mUserPlayerManager;
            target = mOpponentPlayerManager;
        } else if (mOpponentPlayerManager.hasMonsterInFront(message.monster.id)) {
            LogHelper.d(TAG, "onAttackReceive > opponent");

            attacker = mOpponentPlayerManager;
            target = mUserPlayerManager;
        } else {
            //The monster is probably dead
            LogHelper.d(TAG, "onAttackReceive > Unknown");
            return;
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

        if (mUserPlayerManager.hasMonsterInFront(message.monster)) {
            LogHelper.d(TAG, "onMonsterKo > user");
            mUserPlayerManager.onCurrentMonsterKo();
            if (mUserPlayerManager.hasRemainingMonster()) {
                UserMonster nextMonster = mUserPlayerManager.getNextMonster();
                if (nextMonster == null) {
                    FightResultFragment.load(getFragmentManager(), "You didn't have any monster left.");
                } else {
                    sendReplaceMonsterKoRequest(nextMonster);
                }
            }
        } else if (mOpponentPlayerManager.hasMonsterInFront(message.monster)) {
            LogHelper.d(TAG, "onMonsterKo > opponent");
            mOpponentPlayerManager.onCurrentMonsterKo();
            if (!mOpponentPlayerManager.hasRemainingMonster()) {
                FightResultFragment.load(getFragmentManager(), "Your opponent didn't have any monster left.");
            }
        } else {
            //The monster is probably dead
            LogHelper.d(TAG, "onMonsterKo > Unknown");
        }
    }

    @Subscribe
    public void onCombatEnd(OnCombatEndEvent onCombatEndEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        FightResultFragment.load(getFragmentManager(), "Fight Ended");
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
        *  We'll add listeners after the pre-requisite loading
        */

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.layout_fight_body)).addView(mDrawingGestureView);
    }

    private void setupPlayers() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mUserPlayerManager = new UserPlayerManager(layoutUser, mUserTeam);
        mOpponentPlayerManager = new OpponentPlayerManager(layoutOpponent, (int) mStartMessage.opponent.monsterCount);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return null;
                }

                //TODO check if we successfully got monster attack
                mUserPlayerManager.setCurrentMonster(mStartMessage.user.monster, retrieveMonsterAttack(mStartMessage.user.monster.info()));
                mOpponentPlayerManager.setCurrentMonster(mStartMessage.opponent.monster, retrieveMonsterAttack(mStartMessage.opponent.monster.info()));
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

        //Check if the user monster is alive
        if (mUserPlayerManager.getCurrentMonster() == null) {
            Toast.makeText(mContext, "Your monster is dead, he can't attack !", Toast.LENGTH_LONG).show();
            return;
        } else if (mOpponentPlayerManager.getCurrentMonster() == null) {
            Toast.makeText(mContext, "You can't attack a dead monster !", Toast.LENGTH_LONG).show();
            return;
        }

        //Parse user gesture
        switch (attackTypeWanted) {
            case UNKNOWN:
                //Unknown attack, display error message
                Toast.makeText(mContext, "Unknown attack type", Toast.LENGTH_SHORT).show();
                break;
            case OBJECT_USE:
                //Feature not supported yet, display error message
                Toast.makeText(mContext, "Feature incoming", Toast.LENGTH_SHORT).show();
                break;
            default:
                //If we're here, it means the user want to send: attack || attackSp || defense || defenceSp
                //Check if the current monster has an attack of the wanted type
                MonsterAttackResponse.MonsterAttack attack = mUserPlayerManager.getCurrentMonsterAttack(attackTypeWanted);
                if (attack == null) {
                    //Current monster don't have any attack of the wantend type, just display error message
                    Toast.makeText(mContext, "Your monster didn't have this kind of attack", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sendAttackRequest(mOpponentPlayerManager.getCurrentMonster().id, attack.infos.attackId);
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
                } else {
                    onServiceDisconnected(null);
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
            }
        });
    }

    private void sendReplaceMonsterKoRequest(@NonNull final UserMonster nextMonster) {
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
                    ReplaceMonsterRequest replaceMonsterRequest = new ReplaceMonsterRequest(nextMonster.userMonsterId, mStartMessage.combatId);
                    nestedWorldSocketAPI.sendRequest(replaceMonsterRequest, SocketMessageType.MessageKind.TYPE_COMBAT_MONSTER_KO_REPLACE);

                } else {
                    onServiceDisconnected(null);
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
            }
        });
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
                break;
        }

        return attackType;
    }

    @Nullable
    private static List<MonsterAttackResponse.MonsterAttack> retrieveMonsterAttack(@NonNull final Monster monster) {
        try {
            //Retrieve current monster attack
            Response<MonsterAttackResponse> response = NestedWorldHttpApi.getInstance().getMonsterAttack(monster.monsterId).execute();
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
}
