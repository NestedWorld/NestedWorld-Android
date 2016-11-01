package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.MonsterAdapter;
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
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve monsters
        List<UserMonster> userMonsters = Select.from(UserMonster.class).list();

        //Create and populate adapter
        UserMonsterAdapter userMonsterAdapter = new UserMonsterAdapter(mContext);
        userMonsterAdapter.addAll(userMonsters);

        //set adapter to our grid
        gridView.setAdapter(userMonsterAdapter);

        //Set listener on our grid
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
