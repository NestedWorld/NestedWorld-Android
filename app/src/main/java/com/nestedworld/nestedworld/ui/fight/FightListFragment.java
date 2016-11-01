package com.nestedworld.nestedworld.ui.fight;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.FightAdapter;
import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.events.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.network.socket.models.request.result.ResultRequest;
import com.nestedworld.nestedworld.network.socket.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.msgpack.value.ValueFactory;

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FightListFragment());
        fragmentTransaction.commit();
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

    @Override
    public void onRefresh() {
        //Start loading animation
        swipeRefreshLayout.setRefreshing(true);

        //Retrieve list of available combat from Orm
        List<Combat> combats = Select.from(Combat.class).list();

        //Clear old content
        mAdapter.clear();

        //Add new content
        mAdapter.addAll(combats);

        //Stop loading animation
        swipeRefreshLayout.setRefreshing(false);
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

    /*
    ** EventBus
     */
    @Subscribe
    public void onNewCombatAvailable(OnAvailableMessageEvent event) {
        LogHelper.d(TAG, "onNewCombatAvailable");

        AvailableMessage message = event.getMessage();
        Combat newCombat = Select.from(Combat.class).where(Condition.prop("combat_id").eq(message.getMessageId())).first();
        mAdapter.add(newCombat);
    }
}
