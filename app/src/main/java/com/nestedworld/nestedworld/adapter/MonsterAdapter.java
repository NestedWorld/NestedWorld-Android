package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.Monster;

import java.util.ArrayList;

public class MonsterAdapter extends ArrayAdapter<Monster> {

    private final static String TAG = MonsterAdapter.class.getSimpleName();

    /*
    ** Constructor
     */
    public MonsterAdapter(@NonNull Context context, @NonNull ArrayList<Monster> objects) {
        super(context, 0, objects);
    }

    /*
    ** Inherit method
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get user
        final Monster monster = getItem(position);

        //Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
        }

        //Populate the name
        final TextView textviewName = (TextView) convertView.findViewById(R.id.textview_monster_name);
        textviewName.setText(monster.name);

        //TODO utiliser la bonne image
        final ImageView imageViewMonster = (ImageView) convertView.findViewById(R.id.imageView_monster);
        Glide.with(getContext())
                .load(R.drawable.default_monster)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(imageViewMonster) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageViewMonster.setImageDrawable(circularBitmapDrawable);
                    }
                });

        return convertView;
    }
}
