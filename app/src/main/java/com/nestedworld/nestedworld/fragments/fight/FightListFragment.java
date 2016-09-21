package com.nestedworld.nestedworld.fragments.fight;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.Combat;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.request.result.ResultRequest;
import com.orm.query.Select;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class FightListFragment extends BaseFragment {

    @Bind(R.id.listView_fightList)
    ListView listView;

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
        changeActionBarName();
        populateFightList();
    }

    /*
    ** Private method
     */
    private void populateFightList() {

        List<Combat> combats = Select.from(Combat.class).list();

        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //init adapter for our listView
        final FightAdapter friendAdapter = new FightAdapter(mContext, combats);
        listView.setAdapter(friendAdapter);
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

    /**
     * * Custom adapter for displaying fight on the listView
     **/
    private static class FightAdapter extends ArrayAdapter<Combat> {

        private static final String TAG = FightAdapter.class.getSimpleName();
        private static final int resource = R.layout.item_fight;
        private final Context mContext;

        public FightAdapter(@NonNull final Context context, @NonNull final List<Combat> combatList) {
            super(context, resource, combatList);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            FightHolder fightHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
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

            return view;
        }

        /*
        ** Utils
         */
        private void acceptCombat(@NonNull final Combat combat) {
            //Display some log
            LogHelper.d(TAG, "Combat accepted: " + combat.toString());

            //Display the team selection
            TeamSelectionFragment.load(((AppCompatActivity) mContext).getSupportFragmentManager(), combat);
        }

        private void refuseCombat(@NonNull final Combat combat) {
            //Display some log
            LogHelper.d(TAG, "Combat refuse: " + combat.toString());

            //Delete combat in db
            combat.delete();

            //Delete combat in adapter
            remove(combat);

            //Tell the server we refuse the combat
            NestedWorldSocketAPI.getInstance(new ConnectionListener() {
                @Override
                public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                    ValueFactory.MapBuilder map = ValueFactory.newMapBuilder();
                    map.put(ValueFactory.newString("accept"), ValueFactory.newBoolean(false));

                    ResultRequest resultRequest = new ResultRequest(map.build().map(), true);
                    nestedWorldSocketAPI.sendRequest(resultRequest, SocketMessageType.MessageKind.TYPE_RESULT, combat.message_id);
                }

                @Override
                public void onConnectionLost() {
                    //Check if fragment hasn't been detach
                    if (mContext == null) {
                        return;
                    }

                    //Display an error message
                    Toast.makeText(mContext, R.string.error_network_tryAgain, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {

                }
            });
        }

        private static class FightHolder {
            public TextView textViewFightDescription;
            public Button buttonAccept;
            public Button buttonRefuse;
        }
    }
}
