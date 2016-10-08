package com.nestedworld.nestedworld.ui.fight.battle;

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

import java.util.List;

public class BattleMonsterAdapter extends ArrayAdapter<Monster> {

    /*
    ** Constructor
     */
    public BattleMonsterAdapter(@NonNull final Context context, @NonNull final List<Monster> objects) {
        super(context, R.layout.item_fight_monster, objects);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        //Check if and existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_fight_monster, parent, false);
        }

        //Get selectedMonster
        Monster monster = getItem(position);
        if (monster == null) {
            return view;
        }

        populateView(view, monster);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final View view, @NonNull final Monster monster) {
        //Populate name & lvl
        ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monster.name);

        //Display monster picture
        final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
        Glide.with(getContext())
                .load(monster.sprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(imageViewMonster);

        //Add color shape around monster picture
        view.findViewById(R.id.imageView_monster_shape).setBackgroundColor(ContextCompat.getColor(getContext(), monster.getColorResource()));
    }

}
