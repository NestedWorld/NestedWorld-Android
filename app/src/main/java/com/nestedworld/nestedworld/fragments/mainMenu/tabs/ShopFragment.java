package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.api.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.api.socket.models.request.chat.JoinChannelRequest;
import com.nestedworld.nestedworld.api.socket.models.request.chat.PartChannelRequest;
import com.nestedworld.nestedworld.api.socket.models.request.chat.SendMessageRequest;
import com.nestedworld.nestedworld.api.socket.models.request.combat.FleeRequest;
import com.nestedworld.nestedworld.api.socket.models.request.combat.MonsterKoCaptureRequest;
import com.nestedworld.nestedworld.api.socket.models.request.combat.MonsterKoReplaceRequest;
import com.nestedworld.nestedworld.api.socket.models.request.combat.SendAttackRequest;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShopFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    @Bind(R.id.button_flee)
    Button buttonFlee;
    @Bind(R.id.button_capture)
    Button buttonCapture;
    @Bind(R.id.button_replace)
    Button buttonReplace;
    @Bind(R.id.button_attack)
    Button buttonAttack;
    @Bind(R.id.button_join)
    Button buttonJoin;
    @Bind(R.id.button_part)
    Button buttonPart;
    @Bind(R.id.button_send_message)
    Button buttonSendMessage;
    @Bind(R.id.progressView)
    ProgressView progressView;
    private NestedWorldSocketAPI mNestedWorldSocketApi;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new HomeFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_shop;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        progressView.start();
        NestedWorldSocketAPI.getInstance(new ConnectionListener() {
            @Override
            public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                progressView.stop();
                mNestedWorldSocketApi = nestedWorldSocketAPI;
                initFightButton();
                initChatButton();
            }

            @Override
            public void onConnectionLost() {
                mNestedWorldSocketApi = null;
                Toast.makeText(mContext, "Connection lost", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initChatButton() {
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    JoinChannelRequest data = new JoinChannelRequest();
                    data.channel = "ChanelJoin";
                    if (mContext != null) {
                        mNestedWorldSocketApi.chatRequest(mContext, data);
                    }
                }
            }
        });

        buttonPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    PartChannelRequest data = new PartChannelRequest();
                    data.channel = "ChanelPart";
                    if (mContext != null) {
                        mNestedWorldSocketApi.chatRequest(mContext, data);
                    }
                }
            }
        });

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    SendMessageRequest data = new SendMessageRequest();
                    data.channel = "ChanelMessage";
                    data.message = "Message";
                    if (mContext != null) {
                        mNestedWorldSocketApi.chatRequest(mContext, data);
                    }
                }
            }
        });
    }

    private void initFightButton() {
        buttonFlee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    FleeRequest data = new FleeRequest();
                    if (mContext != null) {
                        mNestedWorldSocketApi.combatRequest(mContext, data);
                    }
                }
            }
        });

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    MonsterKoCaptureRequest data = new MonsterKoCaptureRequest();
                    data.capture = true;
                    data.name = "pokemon name";
                    if (mContext != null) {
                        mNestedWorldSocketApi.combatRequest(mContext, data);
                    }
                }
            }
        });

        buttonReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    MonsterKoReplaceRequest data = new MonsterKoReplaceRequest();
                    data.userMonsterId = 15;
                    if (mContext != null) {
                        mNestedWorldSocketApi.combatRequest(mContext, data);
                    }
                }
            }
        });

        buttonAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNestedWorldSocketApi != null) {
                    SendAttackRequest data = new SendAttackRequest();
                    data.attack = 10;
                    data.target = 45;
                    if (mContext != null) {
                        mNestedWorldSocketApi.combatRequest(mContext, data);
                    }
                }
            }
        });
    }
}