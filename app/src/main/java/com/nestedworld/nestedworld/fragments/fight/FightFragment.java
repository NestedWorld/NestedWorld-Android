package com.nestedworld.nestedworld.fragments.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.socket.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.customView.drawingGestureView.DrawingGestureView;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.DrawingGestureListener;
import com.nestedworld.nestedworld.customView.drawingGestureView.listener.OnFinishMoveListener;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FightFragment extends BaseFragment {

    private final ArrayList<Integer> mPositions = new ArrayList<>();
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
    protected void init(View rootView, Bundle savedInstanceState) {
        initSocket();
        initDrawingGestureView(rootView);
    }

    private void initSocket() {
        NestedWorldSocketAPI.getInstance(new com.nestedworld.nestedworld.api.socket.callback.Callback() {
            @Override
            public void onConnexionReady(NestedWorldSocketAPI nestedWorldSocketAPI) {
                mNestedWorldSocketAPI = nestedWorldSocketAPI;
            }

            @Override
            public void onConnexionFailed() {
                //TODO display and error message
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
                mNestedWorldSocketAPI.sendMessage(mPositions.toString());
                mPositions.clear();
            }
        });

        /*Add the custom view under the rootView*/
        ((RelativeLayout) rootView.findViewById(R.id.relativeLayout_fight)).addView(drawingGestureView);
    }

}
