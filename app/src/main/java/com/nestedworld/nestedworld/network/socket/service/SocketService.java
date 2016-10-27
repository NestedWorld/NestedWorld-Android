package com.nestedworld.nestedworld.network.socket.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.events.socket.chat.OnMessageReceivedEvent;
import com.nestedworld.nestedworld.events.socket.chat.OnUserJoinedEvent;
import com.nestedworld.nestedworld.events.socket.chat.OnUserPartedEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnAttackReceiveEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatEndEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatStartMessageEvent;
import com.nestedworld.nestedworld.events.socket.combat.OnMonsterKoEvent;
import com.nestedworld.nestedworld.events.socket.generic.OnResultResponseEvent;
import com.nestedworld.nestedworld.gcm.NestedWorldGcm;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.handlers.SocketMessageHandler;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AttackReceiveMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.CombatEndMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.message.generic.ResultMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.MessageReceivedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserJoinedMessage;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserPartedMessage;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.value.Value;

import java.util.HashMap;
import java.util.Map;

public class SocketService extends Service {

    private final static String TAG = SocketService.class.getSimpleName();
    private final static Map<SocketMessageType.MessageKind, SocketMessageHandler> mHandlers = buildHandlers();
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
            public void onMessageReceived(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Do internal job
                handleMessage(message, messageKind, idKind);
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
        LogHelper.d(TAG, "returning instance (isNull=" + (mNestedWorldSocketAPI == null) + ")");
        return mNestedWorldSocketAPI;
    }

    /*
    ** Internal method
     */
    private void handleMessage(@NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind) {
        //Handle notification
        NestedWorldGcm.onMessageReceived(this, message, messageKind, idKind);

        //Do internal job
        SocketMessageHandler handler = mHandlers.get(messageKind);
        if (handler == null) {
            LogHelper.d(TAG, "Unsupported message: " + messageKind);
        } else {
            mHandlers.get(messageKind).handleMessage(message, messageKind, idKind);
        }
    }

    private static Map<SocketMessageType.MessageKind, SocketMessageHandler> buildHandlers() {
        //TODO 1 class for each handler

        Map<SocketMessageType.MessageKind, SocketMessageHandler> handler = new HashMap<>();
        handler.put(SocketMessageType.MessageKind.TYPE_CHAT_USER_JOINED, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse message
                UserJoinedMessage userJoinedMessage = new UserJoinedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnUserJoinedEvent(userJoinedMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_CHAT_USER_PARTED, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse message
                UserPartedMessage userPartedMessage = new UserPartedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnUserPartedEvent(userPartedMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_CHAT_MESSAGE_RECEIVED, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse message
                MessageReceivedMessage messageReceivedMessage = new MessageReceivedMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnMessageReceivedEvent(messageReceivedMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_COMBAT_START, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse response
                StartMessage startMessage = new StartMessage(message, messageKind, idKind);

                //Send notification
                EventBus.getDefault().post(new OnCombatStartMessageEvent(startMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_COMBAT_AVAILABLE, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse response
                AvailableMessage availableMessage = new AvailableMessage(message, messageKind, idKind);
                availableMessage.saveAsCombat();

                //Send event
                EventBus.getDefault().post(new OnAvailableMessageEvent(availableMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_COMBAT_MONSTER_KO, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse response
                MonsterKoMessage monsterKoMessage = new MonsterKoMessage(message, messageKind, idKind);

                //Send Event
                EventBus.getDefault().post(new OnMonsterKoEvent(monsterKoMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_COMBAT_ATTACK_RECEIVED, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse response
                AttackReceiveMessage attackReveiveMessage = new AttackReceiveMessage(message, messageKind, idKind);

                //Send Event
                EventBus.getDefault().post(new OnAttackReceiveEvent(attackReveiveMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_COMBAT_END, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Parse message
                CombatEndMessage combatEndMessage = new CombatEndMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnCombatEndEvent(combatEndMessage));
            }
        });

        handler.put(SocketMessageType.MessageKind.TYPE_RESULT, new SocketMessageHandler() {
            @Override
            public void handleMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                //Generic response
                ResultMessage resultMessage = new ResultMessage(message, messageKind, idKind);

                //Send event
                EventBus.getDefault().post(new OnResultResponseEvent(resultMessage));
            }
        });

        return handler;
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
