package com.nestedworld.nestedworld.fragment.fight;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class FightFragment extends BaseFragment implements View.OnTouchListener{

    private ArrayList<ImageView> mTiles = new ArrayList<>();

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FightFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_fight;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom_right));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_bottom_left));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_left));
        mTiles.add((ImageView) rootView.findViewById(R.id.imageView_top_left));

        rootView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE :
                //We check if the user touch a tiles
                ImageView view = hitTest(Math.round(event.getX()), Math.round(event.getY()));

                //if he touch, we change the tile background color
                if (view != null) {
                    view.setBackgroundResource(R.drawable.background_rounded);
                }
               break;
            case MotionEvent.ACTION_UP:
                //Gesture ended, we clear the background color of every tile
                clearTilesBackground();
        }

        return true;
    }

    /*
    ** Utils
     */
    private ImageView hitTest(final int x, final int y) {
        for (ImageView view: mTiles) {
            if (isPositionInView(view, x, y)) {
                return view;
            }
        }
        return null;
    }

    private boolean isPositionInView(@NonNull final View view, final int x, final int y) {
        return (x > view.getLeft() /*check left limit*/
                && x < view.getLeft() + view.getWidth() /*check right limit*/
                && y > view.getTop() /*check top limit*/
                && y < view.getY() + view.getHeight()/*check bottom limit*/);
    }

    private void clearTilesBackground() {
        for (View tiles : mTiles) {
            tiles.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
