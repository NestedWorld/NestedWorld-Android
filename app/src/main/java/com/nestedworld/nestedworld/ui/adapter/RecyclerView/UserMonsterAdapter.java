package com.nestedworld.nestedworld.ui.adapter.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.models.Monster;
import com.nestedworld.nestedworld.data.database.models.UserMonster;
import com.nestedworld.nestedworld.ui.view.monster.userMonsterDetail.UserMonsterDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
        UserMonster userMonster = mItems.get(position);
        if (userMonster != null) {
            holder.populateUserMonsterInfo(userMonster);

            Monster monster = userMonster.getMonster();
            if (monster != null) {
                holder.populateMonsterInfo(monster);
            }
        }
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
        CircleImageView imageViewMonster;


        public UserMonsterViewHolder(View itemView) {
            super(itemView);
        }

        /*
        ** Internal method
         */
        private void populateMonsterInfo(@NonNull final Monster monster) {
            Context context = itemView.getContext();
            if (context != null) {
                textViewMonsterName.setText(monster.name);

                //Display monster picture
                Glide.with(itemView.getContext())
                        .load(monster.baseSprite)
                        .placeholder(R.drawable.default_monster)
                        .error(R.drawable.default_monster)
                        .into(imageViewMonster);

                //Set stroke color
                imageViewMonster.setBorderColor(ContextCompat.getColor(context, monster.getElementColorResource()));
            }
        }

        private void populateUserMonsterInfo(@NonNull final UserMonster userMonster) {
            final Context context = itemView.getContext();
            if (context != null) {
                //Add listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserMonsterDetailActivity.start(context, userMonster);
                    }
                });

                //Populate name & lvl
                textViewMonsterLvl.setText(String.format(context.getResources().getString(
                        R.string.integer),
                        userMonster.level));
            }
        }
    }
}
