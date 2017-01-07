package com.nestedworld.nestedworld.ui.adapter.array;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Attack;

import java.util.List;

public class AttackAdapter extends ArrayAdapter<Attack> {

    @LayoutRes
    private final static int layoutRes = R.layout.item_attack;

    /*
    ** Constructor
     */
    public AttackAdapter(@NonNull final Context context, @NonNull final List<Attack> objects) {
        super(context, layoutRes, objects);
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
            view = LayoutInflater.from(getContext()).inflate(layoutRes, parent, false);
        }

        //Get current attack
        Attack currentAttack = getItem(position);
        if (currentAttack == null) {
            return view;
        }

        populateView(view, currentAttack);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final View view, @NonNull final Attack attack) {
        TextView textViewAttackType = (TextView) view.findViewById(R.id.textview_attackType);
        TextView textViewAttackName = (TextView) view.findViewById(R.id.textView_attackName);

        String attackType = attack.type == null ? "Unknown" : attack.type;
        String attackName = attack.name == null ? "Unknown" : attack.name;

        textViewAttackType.setText(String.format(getContext().getString(R.string.item_attack_msg_attackType), attackType));
        textViewAttackName.setText(String.format(getContext().getString(R.string.item_attack_msg_attackName), attackName));
    }
}