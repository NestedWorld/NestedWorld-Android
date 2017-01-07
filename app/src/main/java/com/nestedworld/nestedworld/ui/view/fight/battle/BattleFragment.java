package com.nestedworld.nestedworld.ui.view.fight.battle;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Attack;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.database.entities.UserMonster;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.data.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.data.network.socket.models.request.combat.ReplaceMonsterRequest;
import com.nestedworld.nestedworld.data.network.socket.models.request.combat.SendAttackRequest;
import com.nestedworld.nestedworld.data.network.socket.service.SocketService;
import com.nestedworld.nestedworld.events.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatEndEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnMonsterKoEvent;
import com.nestedworld.nestedworld.helpers.battle.BattleHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.DrawingGestureView;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.ui.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;
import com.nestedworld.nestedworld.ui.view.base.BattlePlayerFragment;
import com.nestedworld.nestedworld.ui.view.fight.battle.player.OpponentPlayerFragment;
import com.nestedworld.nestedworld.ui.view.fight.battle.player.UserPlayerFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import retrofit2.Response;

public class BattleFragment extends BaseFragment {

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.imageView_battle_background)
    ImageView battleBackground;
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
    private UserPlayerFragment mUserPlayerFragment;
    private OpponentPlayerFragment mOpponentPlayerFragment;
    private final OnFinishMoveListener mOnFinishMoveListener = new OnFinishMoveListener() {
        @Override
        public void onFinish() {
            sendAttack();
        }
    };

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager,
                            @NonNull final StartMessage startMessage,
                            @NonNull final List<UserMonster> selectedMonster) {
        final BattleFragment fightFragment = new BattleFragment();
        fightFragment.setStartMessage(startMessage);
        fightFragment.setUserTeam(selectedMonster);

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fightFragment)
                .commit();
    }

    /*
     * #############################################################################################
     * # EventBus
     * #############################################################################################
     */
    @Subscribe
    public void onAttackReceive(OnAttackReceiveEvent event) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve message
        AttackReceiveMessage message = event.getMessage();

        BattlePlayerFragment attacker;
        BattlePlayerFragment target;
        if (mUserPlayerFragment.hasMonsterInFront(message.monster.id)) {
            LogHelper.d(TAG, "onAttackReceive > opponent");

            //Attack sender is the player
            attacker = mUserPlayerFragment;
            target = mOpponentPlayerFragment;
        } else if (mOpponentPlayerFragment.hasMonsterInFront(message.monster.id)) {
            LogHelper.d(TAG, "onAttackReceive > opponent");

            attacker = mOpponentPlayerFragment;
            target = mUserPlayerFragment;
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

        if (mUserPlayerFragment.hasMonsterInFront(message.monster)) {
            LogHelper.d(TAG, "onMonsterKo > player");
            mUserPlayerFragment.onCurrentMonsterKo();
            if (mUserPlayerFragment.hasRemainingMonster()) {
                UserMonster nextMonster = mUserPlayerFragment.getNextMonster();
                if (nextMonster == null) {
                    BattleResultFragment.load(getFragmentManager(), "You didn't have any monster left.");
                } else {
                    sendReplaceMonsterKoRequest(nextMonster);
                }
            }
        } else if (mOpponentPlayerFragment.hasMonsterInFront(message.monster)) {
            LogHelper.d(TAG, "onMonsterKo > opponent");
            mOpponentPlayerFragment.onCurrentMonsterKo();
            if (!mOpponentPlayerFragment.hasRemainingMonster()) {
                BattleResultFragment.load(getFragmentManager(), "Your opponent didn't have any monster left.");
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

        BattleResultFragment.load(getFragmentManager(), "Fight Ended");
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
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

        //Check arg
        if (mUserTeam == null || mStartMessage == null) {
            //Display some log
            LogHelper.d(TAG, "You should call setStartMessage() and setUserTeam() before binding the fragment");

            //Warm the player we can't start this battle
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

        //Init
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
     * #############################################################################################
     * # Private method
     * #############################################################################################
     */
    private void setupActionBar() {
        //Check if fragment hasn't been detach
        if (mContext != null) {
            //Update toolbar title
            final ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    private void setupEnvironment() {
        switch (mStartMessage.env) {
            case "city":
                Glide.with(mContext).load(R.drawable.citybg).centerCrop().into(battleBackground);
                break;
            default:
                Glide.with(mContext).load(R.drawable.citybg).centerCrop().into(battleBackground);
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
        **  We'll add listeners after the pre-requisite loading
         */

        /*Add the custom view under the rootView*/
        ((ViewGroup) rootView.findViewById(R.id.layout_fight_body)).addView(mDrawingGestureView);
    }

    private void setupPlayers() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mUserPlayerFragment = UserPlayerFragment.load(getChildFragmentManager(), mUserTeam);
        mOpponentPlayerFragment = OpponentPlayerFragment.load(getChildFragmentManager(), mStartMessage.opponent.monsterCount);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return null;
                }

                //TODO check if we successfully got monster attack
                mUserPlayerFragment.setCurrentMonster(mStartMessage.user.monster, retrieveMonsterAttack(mStartMessage.user.monster.info()));
                mOpponentPlayerFragment.setCurrentMonster(mStartMessage.opponent.monster, retrieveMonsterAttack(mStartMessage.opponent.monster.info()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Enable drawingGestureView (allow player to send attack)
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

        //Retrieve and clear player gesture
        Attack.AttackType attackTypeWanted = BattleHelper.gestureToAttackType(mUserGestureInput);
        mUserGestureInput = "";

        //Check if the player monster is alive
        if (mUserPlayerFragment.getCurrentMonster() == null) {
            Toast.makeText(mContext, "Your monster is dead, he can't attack !", Toast.LENGTH_LONG).show();
            return;
        } else if (mOpponentPlayerFragment.getCurrentMonster() == null) {
            Toast.makeText(mContext, "You can't attack a dead monster !", Toast.LENGTH_LONG).show();
            return;
        }

        //Parse player gesture
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
                //If we're here, it means the player want to send: attack || attackSp || defense || defenceSp
                //Check if the current monster has an attack of the wanted type
                MonsterAttackResponse.MonsterAttack attack = mUserPlayerFragment.getCurrentMonsterAttack(attackTypeWanted);
                if (attack == null) {
                    //Current monster don't have any attack of the wantend type, just display error message
                    Toast.makeText(mContext, "Your monster didn't have this kind of attack", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sendAttackRequest(mOpponentPlayerFragment.getCurrentMonster().id, attack.infos.attackId);
                }
                break;
        }
    }

    private void sendAttackRequest(final long targetId,
                                   final long attackId) {
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
                    final ReplaceMonsterRequest replaceMonsterRequest = new ReplaceMonsterRequest(nextMonster.userMonsterId, mStartMessage.combatId);
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

    private void setStartMessage(@NonNull final StartMessage startMessage) {
        mStartMessage = startMessage;
    }

    private void setUserTeam(@NonNull final List<UserMonster> team) {
        mUserTeam = team;
    }

    /*
     * #############################################################################################
     * # Private (static) method
     * #############################################################################################
     */
    @Nullable
    private static List<MonsterAttackResponse.MonsterAttack> retrieveMonsterAttack(@NonNull final Monster monster) {
        try {
            //Retrieve current monster attack
            final Response<MonsterAttackResponse> response = NestedWorldHttpApi.getInstance().getMonsterAttack(monster.monsterId).execute();
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
