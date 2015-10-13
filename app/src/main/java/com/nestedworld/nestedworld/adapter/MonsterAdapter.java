package com.nestedworld.nestedworld.adapter;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MonsterAdapter extends ArrayAdapter<MonstersList.Monster> {

    private final static String TAG = MonsterAdapter.class.getSimpleName();

    /*
    ** Constructor
     */
    public MonsterAdapter(@NonNull Context context, @NonNull ArrayList<MonstersList.Monster> objects) {
        super(context, 0, objects);
    }

    /*
    ** Inherit method
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get data
        final MonstersList.Monster monster = getItem(position);

        //Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
        }

        //Populate the view
        //TODO pupulate picture and description
        final TextView textviewName = (TextView) convertView.findViewById(R.id.textview_monster_name);
        textviewName.setText(monster.name);

        return convertView;
    }
}
