package com.nestedworld.nestedworld.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.event.socket.chat.OnMessageReceivedEvent;
import com.nestedworld.nestedworld.event.socket.chat.OnUserJoinedEvent;
import com.nestedworld.nestedworld.event.socket.chat.OnUserPartedEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnAskMessageEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnCombatEndEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnCombatStartMessageEvent;
import com.nestedworld.nestedworld.event.socket.combat.OnMonsterKoEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.Combat;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.CombatEndMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.MessageReceivedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserJoinedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserPartedMessage;
import com.nestedworld.nestedworld.ui.launch.LaunchActivity;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.value.Value;

import java.util.Map;

public class SocketService extends Service {

    public final static String TAG = SocketService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private NestedWorldSocketAPI mNestedWorldSocketAPI = null;

    /*
    ** Life cycle
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Display some log
        LogHelper.d(TAG, "onBind()");

        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        //Display some log
        LogHelper.d(TAG, "onStartCommand()");

        //Instantiate a socketConnection listener
        NestedWorldSocketAPI.getInstance().addListener(new ConnectionListener() {
            @Override
            public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                mNestedWorldSocketAPI = nestedWorldSocketAPI;
            }

            @Override
            public void onConnectionLost() {
                mNestedWorldSocketAPI = null;

                //Clean API
                NestedWorldSocketAPI.reset();

                //Re-init API
                onStartCommand(intent, flags, startId);
            }

            @Override
            public void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
                //Do internal job
                parseMessage(kind, content);
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        NestedWorldSocketAPI.reset();
    }

    /*
    ** Public method (for client)
     */
    @Nullable
    public NestedWorldSocketAPI getApiInstance() {
        return mNestedWorldSocketAPI;
    }

    /*
    ** Internal method
     */
    private void parseMessage(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
        //Do internal job
        switch (kind) {
            case TYPE_CHAT_USER_JOINED:
                //Parse message
                UserJoinedMessage userJoinedMessage = new UserJoinedMessage(content);

                //Send event
                EventBus.getDefault().post(new OnUserJoinedEvent(userJoinedMessage));
                break;
            case TYPE_CHAT_USER_PARTED:
                //Parse message
                UserPartedMessage userPartedMessage = new UserPartedMessage(content);

                //Send event
                EventBus.getDefault().post(new OnUserPartedEvent(userPartedMessage));
                break;
            case TYPE_CHAT_MESSAGE_RECEIVED:
                //Parse message
                MessageReceivedMessage messageReceivedMessage = new MessageReceivedMessage(content);

                //Send event
                EventBus.getDefault().post(new OnMessageReceivedEvent(messageReceivedMessage));
                break;
            case TYPE_COMBAT_START:
                //Parse response
                StartMessage startMessage = new StartMessage(content);

                //Send notification
                EventBus.getDefault().post(new OnCombatStartMessageEvent(startMessage));
                break;
            case TYPE_COMBAT_AVAILABLE:
                //Parse response
                AvailableMessage availableMessage = new AvailableMessage(content);
                Combat combat = availableMessage.saveAsCombat();

                //Display notification
                displayNotification("Un combat est diposnible : " + combat.origin);

                //Send event
                EventBus.getDefault().post(new OnAvailableMessageEvent(availableMessage));
                break;
            case TYPE_COMBAT_MONSTER_KO:
                //Parse response
                MonsterKoMessage monsterKoMessage = new MonsterKoMessage(content);

                //Send Event
                EventBus.getDefault().post(new OnMonsterKoEvent(monsterKoMessage));
                break;
            case TYPE_COMBAT_ATTACK_RECEIVED:
                //Parse response
                AttackReceiveMessage attackReveiveMessage = new AttackReceiveMessage(content);

                //Send Event
                EventBus.getDefault().post(new OnAttackReceiveEvent(attackReveiveMessage));
                break;
            case TYPE_COMBAT_MONSTER_REPLACED:
                break;
            case TYPE_COMBAT_END:
                //Parse message
                CombatEndMessage combatEndMessage = new CombatEndMessage(content);

                //Send event
                EventBus.getDefault().post(new OnCombatEndEvent(combatEndMessage));
                break;
            case TYPE_GEO_PLACES_CAPTURED:
                break;
            case TYPE_AUTHENTICATE:
                //Shouldn't use it (handle by socketManager)
                break;
            case TYPE_CHAT_JOIN_CHANNEL:
                //It's a response (it's probably a result for chat:join:chanel)
                break;
            case TYPE_CHAT_PART_CHANNEL:
                //It's a response (it's probably a result for chat:part:chanel)
                break;
            case TYPE_CHAT_SEND_MESSAGE:
                //It's a response (it's probably a result for chat:send:message)
                break;
            case TYPE_COMBAT_SEND_ATTACK:
                //It's a response (it's probably a result for combat:send:atk)
                break;
            case TYPE_COMBAT_MONSTER_KO_CAPTURE:
                //It's a response (it's probably a result for monster:ko:capture)
                break;
            case TYPE_COMBAT_MONSTER_KO_REPLACE:
                //It's a response (it's probably a result for monster:ko:replace)
                break;
            case TYPE_COMBAT_FLEE:
                //It's a response (it's probably a result for combat:flee)
                break;
            case TYPE_COMBAT_ASK:
                //Parse response (it's a result for combat:ask)
                AskMessage askMessage = new AskMessage(content);

                //Send Event
                EventBus.getDefault().post(new OnAskMessageEvent(askMessage));
                break;
            case TYPE_RESULT:
                //It's a response, shouldn't use it
                break;
            default:
                break;
        }
    }

    private void displayNotification(@NonNull final String title) {
        //Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.apptheme_color))
                .setContentText(title);

        //Add action on notification
        Intent intentTarget = new Intent(this, LaunchActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, intentTarget, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        //Display notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    /*
    ** Binder
     */
    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of SocketService so clients can call public methods
            return SocketService.this;
        }
    }
}
