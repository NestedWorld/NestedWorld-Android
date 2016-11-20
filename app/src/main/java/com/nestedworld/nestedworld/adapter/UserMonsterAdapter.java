package com.nestedworld.nestedworld.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.ui.monster.MonsterDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
** Custom Adapter for displaying userMonsters
 */
public class UserMonsterAdapter extends RecyclerView.Adapter<UserMonsterAdapter.UserMonsterViewHolder> {

    private final List<UserMonster> mItems = new ArrayList<>();

    /*
    ** Public method
     */
    public void addAll(@NonNull final List<UserMonster> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /*
    ** Life cycle
     */
    @Override
    public UserMonsterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_home_user_monster, parent, false);

        UserMonsterViewHolder userMonsterViewHolder = new UserMonsterViewHolder(view);
        ButterKnife.bind(userMonsterViewHolder, view);

        return userMonsterViewHolder;
    }

    @Override
    public void onBindViewHolder(UserMonsterViewHolder holder, int position) {
        //Get current monster
        UserMonster monster = mItems.get(position);
        if (monster == null) {
            return;
        }

        //Get current monster information
        Monster monsterInfo = monster.info();
        if (monsterInfo == null) {
            return;
        }

        holder.populateLate(monster, monsterInfo);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /*
    ** Inner class
     */
    public static class UserMonsterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_monster_name)
        TextView textViewMonsterName;

        @BindView(R.id.textview_monster_lvl)
        TextView textViewMonsterLvl;

        @BindView(R.id.imageView_monster)
        ImageView imageViewMonster;

        @BindView(R.id.user_monster_shape)
        View viewUserMonsterShape;


        public UserMonsterViewHolder(View itemView) {
            super(itemView);
        }

        public void populateLate(@NonNull final UserMonster monster, @NonNull final Monster monsterInfo) {
            final Context context = itemView.getContext();
            if (context != null) {
                //Add listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MonsterDetailActivity.start(context, monsterInfo);
                    }
                });

                //Populate name & lvl
                textViewMonsterName.setText(monsterInfo.name);
                textViewMonsterLvl.setText(String.format(context.getResources().getString(R.string.integer), monster.level));

                //Display monster picture
                Glide.with(itemView.getContext())
                        .load(monsterInfo.baseSprite)
                        .placeholder(R.drawable.default_monster)
                        .centerCrop()
                        .into(imageViewMonster);

                //Add color shape around monster picture
                viewUserMonsterShape.setBackgroundColor(ContextCompat.getColor(context, monster.getColorResource()));
            }
        }
    }
}
