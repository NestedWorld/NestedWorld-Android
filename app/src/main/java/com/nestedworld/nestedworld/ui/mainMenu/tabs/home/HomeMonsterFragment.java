package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.UserMonsterAdapter;
import com.nestedworld.nestedworld.customView.recycler.GridAutoFitRecyclerView;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.events.http.OnUserMonstersUpdatedEvent;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class HomeMonsterFragment extends BaseFragment {

    private final UserMonsterAdapter userMonsterAdapter = new UserMonsterAdapter();
    @BindView(R.id.recycler_home_monster)
    GridAutoFitRecyclerView recycler;
    @BindView(R.id.recycler_home_monster_container)
    View viewRecyclerContainer;
    @BindView(R.id.textview_no_monster)
    TextView textViewNoMonster;

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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
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

        recycler.setHasFixedSize(true);
        recycler.setColumnWidth((int) getResources().getDimension(R.dimen.item_user_monster_width));
        recycler.setAdapter(userMonsterAdapter);
    }

    @UiThread
    private void populateMonstersList() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve monsters
        List<UserMonster> userMonsters = Select.from(UserMonster.class).list();

        userMonsterAdapter.clear();
        if (userMonsters == null || userMonsters.isEmpty()) {
            textViewNoMonster.setVisibility(View.VISIBLE);
            viewRecyclerContainer.setVisibility(View.GONE);
        } else {
            textViewNoMonster.setVisibility(View.GONE);
            viewRecyclerContainer.setVisibility(View.VISIBLE);
            userMonsterAdapter.addAll(userMonsters);
        }
    }
}
