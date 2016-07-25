package com.nestedworld.nestedworld.fragments.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.request.combat.SendAttackRequest;
import com.rey.material.widget.ProgressView;

import org.msgpack.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class FightFragment extends BaseFragment implements ConnectionListener {

    private final ArrayList<Integer> mPositions = new ArrayList<>();
    @Bind(R.id.progressView)
    ProgressView progressView;
    private NestedWorldSocketAPI mNestedWorldSocketAPI;
    private DrawingGestureView mDrawingGestureView;
    private StartMessage mStartMessage;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final StartMessage startMessage) {
        FightFragment fightFragment = new FightFragment();
        fightFragment.setStartMessage(startMessage);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fightFragment);
        fragmentTransaction.commit();
    }

    public void setStartMessage(@NonNull final StartMessage startMessage) {
        mStartMessage = startMessage;
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_fight;
    }

    @Override
    protected void init(final View rootView, Bundle savedInstanceState) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        /*Update toolbar title*/
        ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Fight");
        }

        /*start a loading animation*/
        progressView.start();

        /*Init the gestureListener*/
        initDrawingGestureView(rootView);

        /*Init socket API*/
        NestedWorldSocketAPI.getInstance(this);
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
        mDrawingGestureView.setOnTileTouchListener(new DrawingGestureListener() {
            @Override
            public void onTouch(int tileId) {
                if (!mPositions.contains(tileId)) {
                    mPositions.add(tileId);
                }
            }
        });
        mDrawingGestureView.setOnFinishMoveListener(new OnFinishMoveListener() {
            @Override
            public void onFinish() {
                SendAttackRequest data = new SendAttackRequest(mStartMessage.opponent.monster.id, 10);

                mNestedWorldSocketAPI.sendRequest(data, SocketMessageType.MessageKind.TYPE_COMBAT_SEND_ATTACK);
                mPositions.clear();
            }
        });

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.layout_fight_body)).addView(mDrawingGestureView);
    }

    /*
    ** Connection listener implementation
     */
    @Override
    public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
        /*Socket successfully init*/
        mNestedWorldSocketAPI = nestedWorldSocketAPI;

        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        if (progressView != null) {
            progressView.stop();
        }

        mDrawingGestureView.setEnabled(true);
    }

    @Override
    public void onConnectionLost() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        /*Stop the loading animation and display an error message*/
        if (progressView != null) {
            progressView.stop();
        }

        /*Display an error message*/
        Toast.makeText(mContext, R.string.fight_msg_connection_impossible, Toast.LENGTH_LONG).show();

        /*Stop the activity (can't run without connection)*/
        getActivity().finish();
    }

    @Override
    public void onMessageReceived(@NonNull SocketMessageType.MessageKind messageKind, @NonNull Map<Value, Value> content) {

    }
}
