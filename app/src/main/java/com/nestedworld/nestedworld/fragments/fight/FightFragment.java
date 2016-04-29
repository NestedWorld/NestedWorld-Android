package com.nestedworld.nestedworld.fragments.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.api.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.api.socket.models.request.combat.SendAttackRequest;
import com.nestedworld.nestedworld.customView.drawingGestureView.DrawingGestureView;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

public class FightFragment extends BaseFragment {

    private final ArrayList<Integer> mPositions = new ArrayList<>();
    @Bind(R.id.progressView)
    ProgressView progressView;
    private NestedWorldSocketAPI mNestedWorldSocketAPI;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FightFragment());
        fragmentTransaction.commit();
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
        /*start a loading animation*/
        progressView.start();

        /*Init the socket*/
        NestedWorldSocketAPI.getInstance(new ConnectionListener() {
            @Override
            public void OnConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                /*Socket successfully init*/
                mNestedWorldSocketAPI = nestedWorldSocketAPI;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*Init the custom view*/
                        initDrawingGestureView(rootView);

                        /*Stop the loading animation*/
                        if (progressView != null) {
                            progressView.stop();
                        }
                    }
                });
            }

            @Override
            public void OnConnectionLost() {
                /*Socket initialisation failed*/
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*Stop the loading animation and display an error message*/
                        progressView.stop();
                        Toast.makeText(mContext, "Connexion impossible", Toast.LENGTH_LONG).show();

                        /*Stop the activity (can't run without connection)*/
                        getActivity().finish();
                    }
                });
            }
        });
    }

    private void initDrawingGestureView(View rootView) {
        if (mContext == null) {
            return;
        }

        /*We create a list with every tile*/
        final List<ImageView> tiles = Arrays.asList(
                (ImageView) rootView.findViewById(R.id.imageView_top),
                (ImageView) rootView.findViewById(R.id.imageView_top_right),
                (ImageView) rootView.findViewById(R.id.imageView_right),
                (ImageView) rootView.findViewById(R.id.imageView_bottom_right),
                (ImageView) rootView.findViewById(R.id.imageView_bottom),
                (ImageView) rootView.findViewById(R.id.imageView_bottom_left),
                (ImageView) rootView.findViewById(R.id.imageView_left),
                (ImageView) rootView.findViewById(R.id.imageView_top_left));

        /*Create and init the custom view*/
        DrawingGestureView drawingGestureView = new DrawingGestureView(mContext);
        drawingGestureView.setTiles(tiles);
        drawingGestureView.setOnTileTouchListener(new DrawingGestureListener() {
            @Override
            public void onTouch(int tileId) {
                if (!mPositions.contains(tileId)) {
                    mPositions.add(tileId);
                }
            }
        });
        drawingGestureView.setmOnFinishMoveListener(new OnFinishMoveListener() {
            @Override
            public void onFinish() {
                SendAttackRequest data = new SendAttackRequest();

                String buf = "";
                for (int i : mPositions) {
                    buf += i;
                }

                data.target = 10;
                data.attack = Integer.parseInt(buf);

                mNestedWorldSocketAPI.combatRequest(mContext, data);
                mPositions.clear();
            }
        });

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.relativeLayout_fight)).addView(drawingGestureView);
    }

}
