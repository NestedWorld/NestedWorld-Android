package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;

import java.util.List;

/*
** Custom Adapter for displaying userMonsters
 */
public class UserMonsterAdapter extends BaseAdapter {

    private final List<UserMonster> userMonsters;

    /*
    ** Constructor
     */
    public UserMonsterAdapter(@NonNull final List<UserMonster> userMonsters) {
        this.userMonsters = userMonsters;
    }

    /*
    ** Life cycle
     */
    @Override
    public int getCount() {
        return userMonsters.size();
    }

    @Override
    public Object getItem(int position) {
        return userMonsters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userMonsters.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        //Get current monster
        final UserMonster monster = (UserMonster) getItem(position);
        if (monster == null) {
            return view;
        }

        //Get current monster information
        Monster monsterInfo = monster.info();
        if (monsterInfo == null) {
            return view;
        }

        //Check if parents view hasn't been detach
        Context context = parent.getContext();
        if (context == null) {
            return view;
        }

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_monster, parent, false);
        }

        //Populate name & lvl
        ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monsterInfo.name);
        ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(context.getResources().getString(R.string.tabHome_msg_monsterLvl), monster.level));

        //Display monster picture
        final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
        Glide.with(context)
                .load(monsterInfo.base_sprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(imageViewMonster);

        //Add color shape around monster picture
        view.findViewById(R.id.imageView_monster_shape).setBackgroundColor(ContextCompat.getColor(context, monster.getColorResource()));

        return view;
    }
}
