package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;

/*
** Custom Adapter for displaying userMonsters
 */
public class UserMonsterAdapter extends ArrayAdapter<UserMonster> {

    /*
    ** Constructor
     */
    public UserMonsterAdapter(@NonNull final Context context) {
        super(context, 0);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
        }

        //Get current monster
        final UserMonster monster = getItem(position);
        if (monster == null) {
            return view;
        }

        //Get current monster information
        Monster monsterInfo = monster.info();
        if (monsterInfo == null) {
            return view;
        }

        //Populate name & lvl
        ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monsterInfo.name);
        ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(getContext().getResources().getString(R.string.tabHome_msg_monsterLvl), monster.level));

        //Display monster picture
        final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
        Glide.with(getContext())
                .load(monsterInfo.baseSprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(imageViewMonster);

        //Add color shape around monster picture
        view.findViewById(R.id.imageView_monster_shape).setBackgroundColor(ContextCompat.getColor(getContext(), monster.getColorResource()));

        return view;
    }
}
