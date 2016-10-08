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

    private final List<Monster> mMonsters = new ArrayList<>();

    /*
    ** Constructor
     */
    public BattleMonsterAdapter() {

    }

    public BattleMonsterAdapter(@NonNull final List<Monster> monsters) {
        mMonsters.addAll(monsters);
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
        Monster monster = mMonsters.get(position);

        if (monster != null) {
            populateMonsterInfo(holder, monster);
        }
    }

    @Override
    public int getItemCount() {
        return mMonsters.size();
    }

    /*
    ** Public method
     */
    public void addAll(@NonNull final List<Monster> monsters) {
        mMonsters.clear();
        mMonsters.addAll(monsters);
    }

    public void add(@Nullable final Monster monster) {
        mMonsters.add(monster);
    }

    /*
    ** Internal method
     */
    private void populateMonsterInfo(@NonNull final BattleMonsterViewHolder holder, @NonNull final Monster monster) {
        //Populate name & lvl
        LogHelper.d("populateMonsterInfo > ", "name=" + monster.name);
        holder.textViewMonsterName.setText(monster.name);

        //Display monster picture
        Glide.with(getContext())
                .load(monster.sprite)
                .placeholder(R.drawable.default_monster)
                .centerCrop()
                .into(holder.imageViewMonster);
    }

    public class BattleMonsterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview_monster_name)
        TextView textViewMonsterName;
        @BindView(R.id.imageView_monster)
        ImageView imageViewMonster;

        public BattleMonsterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
