package com.nestedworld.nestedworld.ui.mainMenu.tabs.monster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import retrofit2.Response;

public class MonsterDetailDialog extends DialogFragment {

    private Monster mMonster;
    private TextView textViewName;
    private TextView textViewAttack;
    private TextView textViewDefence;
    private TextView textViewHp;
    private TextView textViewSpeed;
    private ProgressView progressView;
    private ListView listView;
    /*
    ** Static method
     */
    public static MonsterDetailDialog newInstance(@NonNull final Monster monster) {
        MonsterDetailDialog monsterDetailDialog = new MonsterDetailDialog();

        // Supply monsterId as an argument.
        Bundle args = new Bundle();
        args.putLong("monsterId", monster.monster_id);

        monsterDetailDialog.setArguments(args);

        return monsterDetailDialog;
    }

    /*
    ** Life cycle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseArg();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create view
        View view = inflater.inflate(R.layout.fragment_tab_monsters_details, container, false);

        if (mMonster != null) {
            getDialog().setTitle(mMonster.name);
            retrieveWidget(view);
            populateView();
            populateAttack();
        } else {
            Toast.makeText(getActivity(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            getDialog().dismiss();
        }

        return view;
    }

    /*
    ** Internal method
     */
    private void parseArg() {
        if (getArguments().containsKey("monsterId")) {
            long monsterId = getArguments().getLong("monsterId");

            Monster monster = Select.from(Monster.class).where(Condition.prop("monsterid").eq(monsterId)).first();
            if (monster != null) {
                mMonster = monster;
            }
        }
    }

    private void retrieveWidget(@NonNull final View view) {
        //TODO should use butterknife (butterknife.bind(this, view)
        textViewName = (TextView) view.findViewById(R.id.textView_monsterName);
        textViewAttack = (TextView) view.findViewById(R.id.textView_monsterAttack);
        textViewDefence = (TextView) view.findViewById(R.id.textView_monsterDefence);
        textViewHp = (TextView) view.findViewById(R.id.textView_monsterHp);
        textViewSpeed = (TextView) view.findViewById(R.id.textView_monsterSpeed);
        progressView = (ProgressView) view.findViewById(R.id.progressView);
        listView = (ListView) view.findViewById(R.id.listview_monter_attack);
    }

    private void populateView() {
        textViewName.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), mMonster.name));
        textViewAttack.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), mMonster.attack));
        textViewDefence.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), mMonster.defense));
        textViewHp.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), mMonster.hp));
        textViewSpeed.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), mMonster.speed));
    }

    private void populateAttack() {
        //Start loading animation
        progressView.start();

        //Retrieve monster spell
        NestedWorldHttpApi
                .getInstance(getContext())
                .getMonsterAttack(mMonster.getId())
                .enqueue(new Callback<MonsterAttackResponse>() {
                    @Override
                    public void onSuccess(Response<MonsterAttackResponse> response) {
                        //Stop loading animation
                        progressView.stop();

                        if (response != null && response.body() != null) {
                            if (response.body().attacks.isEmpty()) {
                                //TODO display message "your monster didn't have any attack"
                            } else {
                                //TODO display monster attack inside listview
                            }
                        } else {
                            onError(KIND.UNEXPECTED, response);
                        }
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<MonsterAttackResponse> response) {
                        //Stop loading animation
                        progressView.stop();
                    }
                });
    }
}
