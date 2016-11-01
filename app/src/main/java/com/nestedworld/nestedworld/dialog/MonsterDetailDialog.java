package com.nestedworld.nestedworld.dialog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.AttackAdapter;
import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public final class MonsterDetailDialog extends BaseDialogFragment {

    private final static String TAG = MonsterDetailDialog.class.getSimpleName();
    private final Monster mMonster;

    @BindView(R.id.textView_monsterName)
    TextView textViewName;
    @BindView(R.id.textView_monsterAttack)
    TextView textViewAttack;
    @BindView(R.id.textView_monsterDefence)
    TextView textViewDefence;
    @BindView(R.id.textView_monsterHp)
    TextView textViewHp;
    @BindView(R.id.textView_monsterSpeed)
    TextView textViewSpeed;
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.textview_monster_no_attack)
    TextView textViewMonsterNoAttack;
    @BindView(R.id.listview_monter_attack)
    ListView listView;
    @BindView(R.id.imageview_monster_sprite)
    ImageView imageViewSprite;

    /*
    ** Constructor
     */
    private MonsterDetailDialog(@NonNull final Monster monster) {
        mMonster = monster;
    }

    /*
    ** Public method
     */
    public static void show(@NonNull final FragmentManager fragmentManager, @NonNull final Monster monster) {
        new MonsterDetailDialog(monster).show(fragmentManager, TAG);
    }

    /*
    ** Life cycle
     */
    @Override
    protected Builder build(Builder initialBuilder) {
        return initialBuilder
                .setTitle(mMonster.name)
                .setView(getMonsterDetailView());
    }

    /*
    ** Internal method
     */
    @NonNull
    private View getMonsterDetailView() {
        //Create the view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_monsterdetail, null);

        //Retrieve widget
        ButterKnife.bind(this, view);

        //Populate view
        populateView();
        retrieveMonsterAttack();

        //Return the newly created view
        return view;
    }

    private void populateView() {
        textViewName.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), mMonster.name));
        textViewAttack.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), mMonster.attack));
        textViewDefence.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), mMonster.defense));
        textViewHp.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), mMonster.hp));
        textViewSpeed.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterSpeed), mMonster.speed));
        Glide.with(getContext())
                .load(mMonster.enraged_sprite)
                .into(imageViewSprite);
    }

    private void populateMonsterAttack(@NonNull final List<MonsterAttackResponse.MonsterAttack> monsterAttacks) {
        if (monsterAttacks.isEmpty()) {
            textViewMonsterNoAttack.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textViewMonsterNoAttack.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            List<Attack> attacks = new ArrayList<>();
            for (MonsterAttackResponse.MonsterAttack monsterAttack : monsterAttacks) {
                attacks.add(monsterAttack.infos);
            }

            if (listView.getAdapter() == null) {
                listView.setAdapter(new AttackAdapter(getContext(), attacks));
            } else {
                AttackAdapter attackAdapter = (AttackAdapter) listView.getAdapter();
                attackAdapter.clear();
                attackAdapter.addAll(attacks);
            }
        }
    }

    private void retrieveMonsterAttack() {
        //Start loading animation
        progressView.start();

        //Retrieve monster spell
        NestedWorldHttpApi
                .getInstance()
                .getMonsterAttack(mMonster.monsterId)
                .enqueue(new NestedWorldHttpCallback<MonsterAttackResponse>() {
                    @Override
                    public void onSuccess(@NonNull Response<MonsterAttackResponse> response) {
                        //Stop loading animation
                        progressView.stop();

                        if (response.body() != null) {
                            populateMonsterAttack(response.body().attacks);
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
