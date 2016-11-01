package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.UserMonsterAdapter;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.dialog.UserMonsterDetailDialog;
import com.nestedworld.nestedworld.events.http.OnUserMonstersUpdatedEvent;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class HomeMonsterFragment extends BaseFragment {

    @BindView(R.id.gridLayout_home_monsters)
    GridView gridView;
    private UserMonsterAdapter userMonsterAdapter;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home_monster_list;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        setupAdapter();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        populateMonstersList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onUserMonstersUpdated(OnUserMonstersUpdatedEvent onUserMonstersUpdatedEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateMonstersList();
            }
        });
    }

    /*
    ** Private method
     */
    private void setupAdapter() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Setup adapter
        userMonsterAdapter = new UserMonsterAdapter(mContext);
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

    private void populateMonstersList() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve monsters
        List<UserMonster> userMonsters = Select.from(UserMonster.class).list();

        //Create and populate adapter
        userMonsterAdapter.clear();
        userMonsterAdapter.addAll(userMonsters);
    }
}
