package com.nestedworld.nestedworld.adapter.ArrayAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.request.result.ResultRequest;
import com.nestedworld.nestedworld.network.socket.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;

import org.msgpack.value.ValueFactory;

/**
 * * Custom adapter for displaying fight on the listView
 **/
public class FightAdapter extends ArrayAdapter<Combat> {

    private static final String TAG = FightAdapter.class.getSimpleName();
    private static final int resource = R.layout.item_fight_list;

    /*
    ** Constructor
     */
    public FightAdapter(@NonNull final Context context) {
        super(context, resource);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;
        FightHolder fightHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = ((BaseAppCompatActivity) getContext()).getLayoutInflater();
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
        //TODO send attackPortal request
//        TeamSelectionFragment
//                .load(((BaseAppCompatActivity) getContext()).getSupportFragmentManager(),
//                        getContext().getResources().getInteger(R.integer.duel_monster_needed));
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
                    socketService.getApiInstance().sendRequest(resultRequest, SocketMessageType.MessageKind.TYPE_RESULT, combat.combatId);
                } else {
                    onServiceDisconnected(null);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getContext(), R.string.error_socket_disconnected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static class FightHolder {
        TextView textViewFightDescription;
        Button buttonAccept;
        Button buttonRefuse;
    }
}
