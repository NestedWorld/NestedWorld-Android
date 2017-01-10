package com.nestedworld.nestedworld.ui.view.connected.monster.monsterDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.database.entities.MonsterDao;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import butterknife.BindView;

public class MonsterDetailActivity extends BaseAppCompatActivity {

    private final static String ARG_MONSTER = "MonsterDetailActivity_ARG_MONSTER";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Monster mMonster;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void start(@NonNull final Context context,
                             @NonNull final Monster monster) {
        final Intent intent = new Intent(context, MonsterDetailActivity.class);
        intent.putExtra(ARG_MONSTER, monster.monsterId);
        context.startActivity(intent);
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_monster_detail;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        if (!getIntent().getExtras().containsKey(ARG_MONSTER)) {
            throw new IllegalArgumentException("You should provide a monsterId in intent");
        } else {
            final long monsterId = getIntent().getExtras().getLong(ARG_MONSTER, -1);
            mMonster = NestedWorldDatabase.getInstance()
                    .getDataBase()
                    .getMonsterDao()
                    .queryBuilder()
                    .where(MonsterDao.Properties.MonsterId.eq(monsterId))
                    .unique();
            if (mMonster == null) {
                Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                finish();
            } else {
                setupToolbar();
                MonsterDetailFragment.load(getSupportFragmentManager(), mMonster);
            }
        }
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setupToolbar() {
        //Set the toolbar as actionBar
        setSupportActionBar(toolbar);

        //Get back the Toolbar as actionBar and then custom it
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //customise the actionBar
            actionBar.setTitle(mMonster.name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
