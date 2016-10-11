package com.nestedworld.nestedworld.ui.fight.battle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.orm.util.ContextUtil.getContext;


public class BattleMonsterAdapter extends RecyclerView.Adapter<BattleMonsterAdapter.BattleMonsterViewHolder> {
    private final List<BattleMonster> mMonsters = new ArrayList<>();
    /*
    ** Constructor
     */
    public BattleMonsterAdapter() {

    }

    /*
    ** Life cycle
     */
    @Override
    public BattleMonsterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fight_battlemonster, parent, false);
        BattleMonsterViewHolder battleMonsterViewHolder = new BattleMonsterViewHolder(v);
        ButterKnife.bind(battleMonsterViewHolder, v);

        return battleMonsterViewHolder;
    }

    @Override
    public void onBindViewHolder(BattleMonsterViewHolder holder, int position) {
        //Get selectedMonster
        BattleMonster battleMonster = mMonsters.get(position);

        if (battleMonster != null) {
            populateMonsterInfo(holder, battleMonster);
        }
    }

    @Override
    public int getItemCount() {
        return mMonsters.size();
    }

    /*
    ** Public method
     */
    public void add(@Nullable final Monster monster, @NonNull final Status status) {
        mMonsters.add(new BattleMonster(monster, status));
    }

    /*
    ** Internal method
     */
    private void populateMonsterInfo(@NonNull final BattleMonsterViewHolder holder, @NonNull final BattleMonster battleMonster) {
        //Populate name & lvl
        LogHelper.d("populateMonsterInfo > ", "name=" + battleMonster.monster.name);
        holder.textViewMonsterName.setText(battleMonster.monster.name);

        //Display monster picture
        Glide.with(getContext())
                .load(battleMonster.monster.sprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(holder.imageViewMonster);

        //Check if monster is alive
        switch (battleMonster.status) {
            case DEAD:
                holder.imageViewMonsterStatus.setImageResource(R.drawable.red_cross);
                break;
            case SELECTED:
                holder.imageViewMonsterStatus.setImageResource(R.drawable.green_selected);
                break;
            default:
                break;
        }
    }

    public enum Status {
        DEAD,
        SELECTED,
        DEFAULT
    }

    private static class BattleMonster {
        public Monster monster;
        public Status status;

        public BattleMonster(@NonNull final Monster monster, @NonNull final Status status) {
            this.monster = monster;
            this.status = status;
        }
    }

    public class BattleMonsterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview_monster_name)
        TextView textViewMonsterName;
        @BindView(R.id.imageView_monster)
        ImageView imageViewMonster;
        @BindView(R.id.imageView_monster_status)
        ImageView imageViewMonsterStatus;

        public BattleMonsterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
