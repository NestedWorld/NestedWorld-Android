package com.nestedworld.nestedworld.ui.monster.userMonsterDetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.ArrayAdapter.AttackAdapter;
import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class UserMonsterDetailFragment extends BaseFragment {

    //Header related
    @BindView(R.id.imageView_monster)
    ImageView imageViewSprite;
    @BindView(R.id.textView_monster_name)
    TextView textViewMonsterName;
    @BindView(R.id.textview_monster_surname)
    TextView textViewMonsterSurname;

    //Sub header related
    @BindView(R.id.imageview_monster_type)
    ImageView imageViewType;
    @BindView(R.id.textView_lvl)
    TextView textViewLevel;
    @BindView(R.id.textView_exp)
    TextView textViewExp;

    //Body (characteristics) related
    @BindView(R.id.textView_monsterHp)
    TextView textViewHp;
    @BindView(R.id.progress_monster_hp)
    ProgressBar progressBarHp;
    @BindView(R.id.textView_monsterAttack)
    TextView textViewAttack;
    @BindView(R.id.progress_monster_attack)
    ProgressBar progressBarAttack;
    @BindView(R.id.textView_monsterDefence)
    TextView textViewDefence;
    @BindView(R.id.progress_monster_defence)
    ProgressBar progressBarDefence;
    @BindView(R.id.textView_monsterSpeed)
    TextView textViewSpeed;
    @BindView(R.id.progress_monster_speed)
    ProgressBar progressBarSpeed;

    //Body (attack) related
    @BindView(R.id.textview_monster_no_attack)
    TextView textViewMonsterNoAttack;
    @BindView(R.id.listview_monter_attack)
    ListView listView;
    @BindView(R.id.progressView)
    ProgressView progressView;

    private UserMonster mUserMonster;
    private Monster mMonster;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager supportFragmentManager, @NonNull final UserMonster monster, @NonNull final Monster info) {
        UserMonsterDetailFragment userMonsterDetailFragment = new UserMonsterDetailFragment()
                .setMonster(monster)
                .setMonsterInfo(info);

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, userMonsterDetailFragment)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_usermonsterdetail;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (mUserMonster == null) {
            throw new IllegalArgumentException("You should call setMonster()");
        } else {
            populateView();
            retrieveMonsterAttack();
        }
    }

    /*
    ** Internal method
     */
    private UserMonsterDetailFragment setMonster(@NonNull final UserMonster monster) {
        mUserMonster = monster;
        return this;
    }

    private UserMonsterDetailFragment setMonsterInfo(@NonNull final Monster monsterInfo) {
        mMonster = monsterInfo;
        return this;
    }

    private void populateView() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        Glide.with(getContext())
                .load(mMonster.baseSprite)
                .into(imageViewSprite);


        textViewMonsterName.setText(mMonster.name);
        textViewMonsterSurname.setText(mUserMonster.surname);
        imageViewType.setImageResource(mMonster.getElementImageResource());
        textViewLevel.setText(String.valueOf(mUserMonster.level));
        textViewExp.setText(String.valueOf(mUserMonster.experience));

        //Populate hp
        textViewHp.setText(String.format(getResources().getString(
                R.string.tabMonster_msg_monsterHp),
                (int) mMonster.hp,
                100));
        progressBarHp.setMax(100);
        progressBarHp.setProgress((int) mMonster.hp);

        //Populate attack
        textViewAttack.setText(String.format(getResources().getString(
                R.string.tabMonster_msg_monsterAttack),
                (int) mMonster.attack,
                100));
        progressBarAttack.setMax(100);
        progressBarAttack.setProgress((int) mMonster.attack);

        //Populate defence
        textViewDefence.setText(String.format(
                getResources().getString(R.string.tabMonster_msg_monsterDefence),
                (int) mMonster.defense,
                100));
        progressBarDefence.setMax(100);
        progressBarDefence.setProgress((int) mMonster.defense);

        //Populate speed
        textViewSpeed.setText(String.format(
                getResources().getString(R.string.tabMonster_msg_monsterSpeed),
                (int) mMonster.speed,
                100));
        progressBarSpeed.setMax(100);
        progressBarSpeed.setProgress((int) mMonster.speed);
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
                .getMonsterAttack(mUserMonster.monsterId)
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
