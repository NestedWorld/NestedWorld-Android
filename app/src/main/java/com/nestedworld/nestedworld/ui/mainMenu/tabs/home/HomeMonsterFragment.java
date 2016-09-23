package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.models.UserMonster;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.monster.MonsterDetailDialog;
import com.orm.query.Select;

import java.util.List;

import butterknife.Bind;

public class HomeMonsterFragment extends BaseFragment {

    @Bind(R.id.gridLayout_home_monsters)
    GridView gridView;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home_monster_list;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        populateMonstersList();
    }

    /*
    ** Private method
     */
    private void populateMonstersList() {
        List<UserMonster> monsters = Select.from(UserMonster.class).list();
        gridView.setAdapter(new UserMonsterAdapter(monsters));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserMonster selectedUserMonster = (UserMonster) parent.getItemAtPosition(position);
                if (selectedUserMonster != null) {
                    Monster selectedMonster = selectedUserMonster.info();
                    if (selectedMonster != null) {
                        MonsterDetailDialog.newInstance(selectedMonster).show(getChildFragmentManager(), TAG);
                    }
                }
            }
        });
    }

    /*
    ** Custom Adapter for displaying userMonsters
     */
    private class UserMonsterAdapter extends BaseAdapter {

        private final List<UserMonster> userMonsters;

        public UserMonsterAdapter(@NonNull final List<UserMonster> userMonsters) {
            this.userMonsters = userMonsters;
        }

        @Override
        public int getCount() {
            return userMonsters.size();
        }

        @Override
        public Object getItem(int position) {
            return userMonsters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return userMonsters.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Check if fragment hasn't been detach
            if (mContext == null) {
                return null;
            }

            View view = convertView;

            //Get current monster
            final UserMonster monster = (UserMonster) getItem(position);
            final Monster monsterInfo = monster.info();

            if (monsterInfo == null) {
                return null;
            }

            //Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_monster, parent, false);
            }

            //Populate name & lvl
            ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monsterInfo.name);
            ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(getResources().getString(R.string.tabHome_msg_monsterLvl), monster.level));

            //Display monster picture
            final ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(getContext())
                    .load(monsterInfo.sprite)
                    .placeholder(R.drawable.default_monster)
                    .centerCrop()
                    .into(imageViewMonster);

            //Add color shape around monster picture
            view.findViewById(R.id.imageView_monster_shape).setBackgroundColor(ContextCompat.getColor(mContext, monster.getColorResource()));

            return view;
        }
    }
}
