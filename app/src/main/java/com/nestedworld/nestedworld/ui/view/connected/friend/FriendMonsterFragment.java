package com.nestedworld.nestedworld.ui.view.connected.friend;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import butterknife.BindView;

public class FriendMonsterFragment extends BaseFragment {
    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.view_element_count_water)
    View viewElementCountElectric;
    @BindView(R.id.view_element_count_electric)
    View viewElementCountWater;
    @BindView(R.id.view_element_count_earth)
    View viewElementCountEarth;
    @BindView(R.id.view_element_count_fire)
    View viewElementCountFire;
    @BindView(R.id.view_element_count_plant)
    View viewElementCountPlant;
    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_friend_monster;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        setupElementsCount();
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setupElementsCount() {
        setupElementCount(viewElementCountElectric, R.drawable.element_electric);
        setupElementCount(viewElementCountWater, R.drawable.element_water);
        setupElementCount(viewElementCountEarth, R.drawable.element_earth);
        setupElementCount(viewElementCountFire, R.drawable.element_fire);
        setupElementCount(viewElementCountPlant, R.drawable.element_plant);
    }

    private void setupElementCount(@NonNull final View itemElem,
                                   @DrawableRes final int res) {
        //Retrieve widget
        final ImageView imageViewElem = (ImageView) itemElem.findViewById(R.id.imageview_monster_type);
        final TextView textViewCount = (TextView) itemElem.findViewById(R.id.textview_count);

        imageViewElem.setImageResource(res);
        textViewCount.setText("0");//TODO should use placeHolder
    }
}
