package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.nestedworld.nestedworld.adapter.UserMonsterAdapter;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.dialog.UserMonsterDetailDialog;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import java.util.List;

import butterknife.BindView;

public class HomeMonsterFragment extends BaseFragment {

    @BindView(R.id.gridLayout_home_monsters)
    GridView gridView;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home_monster_list;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
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
                        UserMonsterDetailDialog.show(getChildFragmentManager(), selectedUserMonster);
                    }
                }
            }
        });
    }
}
