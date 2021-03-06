package com.nestedworld.nestedworld.ui.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.ui.view.connected.monster.monsterDetail.MonsterDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Custom Adapter for displaying monsters
 */
public class MonsterAdapter extends RecyclerView.Adapter<MonsterAdapter.MonsterViewHolder> {

    private final List<Monster> mItems = new ArrayList<>();

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public void addAll(@NonNull final List<Monster> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void add(@NonNull final Monster item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /*
     * #############################################################################################
     * # RecyclerView.Adapter<MonsterAdapter.MonsterViewHolder> implementation
     * #############################################################################################
     */
    @Override
    public MonsterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_monster, parent, false);

        final MonsterViewHolder monsterViewHolder = new MonsterViewHolder(view);
        ButterKnife.bind(monsterViewHolder, view);

        return monsterViewHolder;
    }

    @Override
    public void onBindViewHolder(MonsterViewHolder holder, int position) {
        //Get current monster
        final Monster monster = mItems.get(position);
        if (monster == null) {
            return;
        }

        holder.populateLate(monster);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /*
    ** Inner class
     */
    public static class MonsterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_monster_name)
        TextView textViewMonsterName;
        @BindView(R.id.imageView_monster)
        ImageView imageViewMonster;
        @BindView(R.id.imageview_monster_type)
        ImageView imageViewMonsterType;

        public MonsterViewHolder(View itemView) {
            super(itemView);
        }

        public void populateLate(@NonNull final Monster monster) {
            final Context context = itemView.getContext();
            if (context != null) {
                //Add listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MonsterDetailActivity.start(context, monster);
                    }
                });

                //Populate name & lvl
                textViewMonsterName.setText(monster.name);

                //Populate element image
                imageViewMonsterType.setImageResource(monster.getElementImageResource());

                //Display monster picture
                Glide.with(itemView.getContext())
                        .load(monster.enragedSprite)
                        .placeholder(R.drawable.default_monster)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(imageViewMonster);
            }
        }
    }
}