package com.nestedworld.nestedworld.ui.adapter.pager;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.database.entities.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMonsterPagerAdapter extends PagerAdapter {

    private final static String TAG = UserMonsterPagerAdapter.class.getSimpleName();

    private final List<UserMonster> mItems = new ArrayList<>();

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public void addAll(@NonNull final List<UserMonster> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Nullable
    public UserMonster getItemAtPosition(final int position) {
        if (position > mItems.size() || position < 0) {
            return null;
        } else {
            return mItems.get(position);
        }
    }

    /*
     * #############################################################################################
     * # PagerAdapter implementation
     * #############################################################################################
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;

        final Context context = container.getContext();
        if (context != null) {
            //Create the view
            view = LayoutInflater
                    .from(container.getContext())
                    .inflate(R.layout.item_monster_selector, container, false);

            //Retrieve the monster we'll display
            final UserMonster userMonster = mItems.get(position);
            if (userMonster == null) {
                return view;
            }

            final Monster monster = userMonster.getMonster();
            if (monster == null) {
                return view;
            }
            populateView(context, view, userMonster, monster);

            container.addView(view);
        } else {
            return null;
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final UserMonster userMonster = mItems.get(position);
        if (userMonster == null) {
            LogHelper.d(TAG, "getPageTitle > null userMonster");
        } else {
            final Monster monster = userMonster.getMonster();
            if (monster == null) {
                LogHelper.d(TAG, "getPageTitle > null monster");
            } else {
                return monster.name;
            }
        }
        return "Unknown";
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void populateView(@NonNull final Context context,
                              @NonNull final View view,
                              @NonNull final UserMonster userMonster,
                              @NonNull final Monster monster) {

        //Populate monster information
        final Resources res = context.getResources();
        ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monster.name);
        ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(res.getString(
                R.string.integer),
                userMonster.level));
        ((TextView) view.findViewById(R.id.textview_monster_hp)).setText(String.format(res.getString(
                R.string.teamSelection_msg_monsterHp),
                (int) monster.hp));
        ((TextView) view.findViewById(R.id.textview_monster_attack)).setText(String.format(res.getString(
                R.string.teamSelection_msg_monsterAttack),
                (int) monster.attack));
        ((TextView) view.findViewById(R.id.textview_monster_defense)).setText(String.format(res.getString(
                R.string.teamSelection_msg_monsterDefence),
                (int) monster.defense));

        //Display monster picture
        final CircleImageView imageViewMonster = (CircleImageView) view.findViewById(R.id.imageView_monster);
        Glide.with(context)
                .load(monster.baseSprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(imageViewMonster);

        //Add color shape around monster picture
        imageViewMonster.setBorderColor(ContextCompat.getColor(context, monster.getElementColorResource()));
    }
}