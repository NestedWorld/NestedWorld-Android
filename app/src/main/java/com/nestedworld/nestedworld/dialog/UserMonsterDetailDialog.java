package com.nestedworld.nestedworld.dialog;

import android.content.Context;
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
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public final class UserMonsterDetailDialog extends BaseDialogFragment {

    private final static String TAG = UserMonsterDetailDialog.class.getSimpleName();
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
    private UserMonster mUserMonster;

    /*
    ** Public method
     */
    public static void show(@NonNull final FragmentManager fragmentManager, @NonNull final UserMonster userMonster) {
        new UserMonsterDetailDialog().setUserMonster(userMonster).show(fragmentManager, TAG);
    }

    /*
    ** Constructor
     */
    private UserMonsterDetailDialog setUserMonster(@NonNull final UserMonster userMonster) {
        mUserMonster = userMonster;
        return this;
    }

    /*
    ** Life cycle
     */
    @Override
    protected Builder build(Builder initialBuilder) {
        if (mUserMonster != null) {
            Monster userMonsterInfo = mUserMonster.info();
            if (userMonsterInfo != null) {
                initialBuilder
                        .setTitle(mUserMonster.info().name)
                        .setView(getMonsterDetailView());
            } else {
                dismiss();
            }
        } else {
            dismiss();
        }
        return initialBuilder;
    }

    /*
    ** Internal method
     */
    @NonNull
    private View getMonsterDetailView() {
        //Create the view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_monsterdetail, null);

        //Retrieve widget
        ButterKnife.bind(this, view);

        //Populate view
        populateView();
        retrieveMonsterAttack();

        //Return the newly created view
        return view;
    }

    private void populateView() {
        //TODO display experience / surname
        textViewName.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterName), mUserMonster.surname));

        Monster userMonsterInfo = mUserMonster.info();
        if (userMonsterInfo != null) {
            textViewAttack.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterAttack), userMonsterInfo.attack));
            textViewDefence.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterDefence), userMonsterInfo.defense));
            textViewHp.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterHp), userMonsterInfo.hp));
            textViewSpeed.setText(String.format(getResources().getString(R.string.tabMonster_msg_monsterSpeed), userMonsterInfo.speed));
            Glide.with(getContext())
                    .load(userMonsterInfo.baseSprite)
                    .into(imageViewSprite);

        }
    }

    private void populateMonsterAttack(@NonNull final List<MonsterAttackResponse.MonsterAttack> monsterAttacks) {
        //Check if fragment hasn't been detach
        Context context = getContext();
        if (context == null) {
            return;
        }

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
                listView.setAdapter(new AttackAdapter(context, attacks));
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
                .getMonsterAttack(mUserMonster.info().monsterId)
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
