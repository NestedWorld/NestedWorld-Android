package com.nestedworld.nestedworld.ui.mainMenu.tabs.monster;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.models.Attack;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MonsterDetailDialog extends DialogFragment {

    @Bind(R.id.textView_monsterName)
    TextView textViewName;
    @Bind(R.id.textView_monsterAttack)
    TextView textViewAttack;
    @Bind(R.id.textView_monsterDefence)
    TextView textViewDefence;
    @Bind(R.id.textView_monsterHp)
    TextView textViewHp;
    @Bind(R.id.textView_monsterSpeed)
    TextView textViewSpeed;
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Bind(R.id.textview_monster_no_attack)
    TextView textViewMonterNoAttack;
    @Bind(R.id.listview_monter_attack)
    ListView listView;

    private Monster mMonster;
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

        //Retrieve monster by parsing fragment args
        parseArg();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create view
        View view = inflater.inflate(R.layout.fragment_tab_monsters_details, container, false);

        if (mMonster != null) {
            getDialog().setTitle(mMonster.name);

            //Retrieve widget
            ButterKnife.bind(this, view);

            //Start loading animation
            progressView.start();

            //Populate
            populateView();
            populateAttack();
        } else {
            //Display error message
            Toast.makeText(getActivity(), R.string.error_unexpected, Toast.LENGTH_LONG).show();

            //Close dialog
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

    private void populateView() {
        textViewName.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), mMonster.name));
        textViewAttack.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), mMonster.attack));
        textViewDefence.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), mMonster.defense));
        textViewHp.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), mMonster.hp));
        textViewSpeed.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterSpeed), mMonster.speed));
    }

    private void populateAttack() {
        //Start loading animation
        progressView.start();

        //Retrieve monster spell
        NestedWorldHttpApi
                .getInstance(getContext())
                .getMonsterAttack(mMonster.monster_id)
                .enqueue(new Callback<MonsterAttackResponse>() {
                    @Override
                    public void onSuccess(Response<MonsterAttackResponse> response) {
                        //Stop loading animation
                        progressView.stop();

                        if (response != null && response.body() != null) {
                            if (response.body().attacks.isEmpty()) {
                                textViewMonterNoAttack.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            } else {
                                textViewMonterNoAttack.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                listView.setAdapter(new AttackAdapter(getContext(), response.body().attacks));
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

    private static class AttackAdapter extends ArrayAdapter<Attack> {

        @LayoutRes
        private final static int layoutRes = R.layout.item_attack;

        /*
        ** Constructor
         */
        public AttackAdapter(@NonNull final Context context, @NonNull final List<Attack> objects) {
            super(context, layoutRes, objects);
        }

        /*
        ** Life cycle
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            //Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(layoutRes, parent, false);
            }

            //Get current attack
            Attack currentAttack = getItem(position);
            if (currentAttack == null) {
                return view;
            }

            populateView(view, currentAttack);

            return view;
        }

        /*
        ** Internal method
         */
        private void populateView(@NonNull final View view, @NonNull final Attack attack) {
            TextView textViewAttackType = (TextView) view.findViewById(R.id.textview_attackType);
            TextView textViewAttackName = (TextView) view.findViewById(R.id.textView_attackName);

            String attackType = attack.type == null ? "unknown" : attack.type;
            String attackName = attack.name == null ? "unknown" : attack.name;

            textViewAttackType.setText(String.format(getContext().getString(R.string.item_attack_msg_attackType), attackType));
            textViewAttackName.setText(String.format(getContext().getString(R.string.item_attack_msg_attackName), attackName));
        }
    }
}
