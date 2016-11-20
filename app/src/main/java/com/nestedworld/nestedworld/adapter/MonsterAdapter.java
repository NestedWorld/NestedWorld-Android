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

/**
 * Custom Adapter for displaying monsters
 */
public class MonsterAdapter extends ArrayAdapter<Monster> {

    /*
    ** Constructor
     */
    public MonsterAdapter(@NonNull Context context) {
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
        Monster monster = getItem(position);
        if (monster == null) {
            return view;
        }

        //Populate name & lvl
        TextView textViewName = (TextView) view.findViewById(R.id.textview_monster_name);
        textViewName.setText(monster.name);

        //Display monster picture
        ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
        Glide.with(getContext())
                .load(monster.baseSprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(imageViewMonster);

        //Add color shape around monster picture
        view.findViewById(R.id.user_monster_shape).setBackgroundColor(ContextCompat.getColor(getContext(), monster.getColorResource()));

        return view;
    }
}