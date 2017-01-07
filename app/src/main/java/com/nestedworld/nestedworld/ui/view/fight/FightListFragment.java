package com.nestedworld.nestedworld.ui.view.fight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.database.entities.CombatDao;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.adapter.array.FightAdapter;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class FightListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.listView_fightList)
    ListView listView;
    @BindView(R.id.swipeRefreshLayout_fight_list)
    SwipeRefreshLayout swipeRefreshLayout;

    private FightAdapter mAdapter;

    /*
    ** Public static method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FightListFragment())
                .commit();
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onNewCombatAvailable(OnAvailableMessageEvent event) {
        LogHelper.d(TAG, "onNewCombatAvailable");

        AvailableMessage message = event.getMessage();
        Combat newCombat = NestedWorldDatabase
                .getInstance()
                .getDataBase()
                .getCombatDao()
                .queryBuilder()
                .where(CombatDao.Properties.CombatId.eq(message.getMessageId()))
                .unique();

        mAdapter.add(newCombat);
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_fight_list;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //Inuit ui
        changeActionBarName();
        setupAdapter();
        setupSwipeRefresh();

        //populate ui
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onRefresh() {
        //Start loading animation
        swipeRefreshLayout.setRefreshing(true);

        //Retrieve list of available combat from Orm
        List<Combat> combats = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getCombatDao()
                .loadAll();

        if (combats == null || combats.isEmpty()) {
            //TODO display "no combat" text
            mAdapter.clear();
        } else {
            //Clear old content
            mAdapter.clear();

            //Add new content
            mAdapter.addAll(combats);
        }

        //Stop loading animation
        swipeRefreshLayout.setRefreshing(false);
    }

    /*
    ** Private method
     */
    private void setupAdapter() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mAdapter = new FightAdapter(mContext);
        listView.setAdapter(mAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void changeActionBarName() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.fightList_title));
        }
    }
}
