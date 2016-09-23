package com.nestedworld.nestedworld.ui.fight;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.event.socket.combat.OnAvailableMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.models.Combat;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.network.socket.models.request.result.ResultRequest;
import com.nestedworld.nestedworld.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.msgpack.value.ValueFactory;

import java.util.List;

import butterknife.Bind;

public class FightListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.listView_fightList)
    ListView listView;
    @Bind(R.id.swipeRefreshLayout_fight_list)
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
    protected void init(View rootView, Bundle savedInstanceState) {
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

        if (mContext instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.fightList_title));
            }
        }
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onNewCombatAvailable(OnAvailableMessageEvent event) {
        AvailableMessage message = event.getMessage();
        Combat newCombat = Select.from(Combat.class).where(Condition.prop("combatid").eq(message.getMessageId())).first();
        mAdapter.add(newCombat);
    }

    /**
     * * Custom adapter for displaying fight on the listView
     **/
    private final static class FightAdapter extends ArrayAdapter<Combat> {

        private static final String TAG = FightAdapter.class.getSimpleName();
        private static final int resource = R.layout.item_fight;

        FightAdapter(@NonNull final Context context) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            View view;
            FightHolder fightHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
                view = layoutInflater.inflate(resource, parent, false);

                fightHolder = new FightHolder();
                fightHolder.textViewFightDescription = (TextView) view.findViewById(R.id.textView_item_fight_dsc);
                fightHolder.buttonAccept = (Button) view.findViewById(R.id.button_item_fight_accept);
                fightHolder.buttonRefuse = (Button) view.findViewById(R.id.button_item_fight_refuse);

                view.setTag(fightHolder);
            } else {
                fightHolder = (FightHolder) convertView.getTag();
                view = convertView;
            }

            //get the currentCombat
            final Combat currentCombat = getItem(position);
            if (currentCombat != null) {
                //display the combat information
                fightHolder.textViewFightDescription.setText(currentCombat.origin);

                //set callback
                fightHolder.buttonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptCombat(currentCombat);
                    }
                });

                fightHolder.buttonRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refuseCombat(currentCombat);
                    }
                });
            }

            return view;
        }

        /*
        ** Utils
         */
        private void acceptCombat(@NonNull final Combat combat) {
            //Display some log
            LogHelper.d(TAG, "Combat accepted: " + combat.toString());

            //Yes just accept the combat, we have to choose our team
            //Display the team selection
            TeamSelectionFragment.load(((AppCompatActivity) getContext()).getSupportFragmentManager(), combat);
        }

        private void refuseCombat(@NonNull final Combat combat) {
            //Display some log
            LogHelper.d(TAG, "Combat refuse: " + combat.toString());

            //Delete combat in db
            combat.delete();

            //Delete combat in adapter
            remove(combat);

            //Tell the server we refuse the combat
            ServiceHelper.bindToSocketService(getContext(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    SocketService socketService = ((SocketService.LocalBinder) service).getService();
                    if (socketService.getApiInstance() != null) {
                        ValueFactory.MapBuilder map = ValueFactory.newMapBuilder();
                        map.put(ValueFactory.newString("accept"), ValueFactory.newBoolean(false));

                        ResultRequest resultRequest = new ResultRequest(map.build().map(), true);
                        socketService.getApiInstance().sendRequest(resultRequest, SocketMessageType.MessageKind.TYPE_RESULT, combat.combat_id);
                    } else {
                        onServiceDisconnected(null);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Toast.makeText(getContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
            });
        }

        private static class FightHolder {
            TextView textViewFightDescription;
            Button buttonAccept;
            Button buttonRefuse;
        }
    }
}
