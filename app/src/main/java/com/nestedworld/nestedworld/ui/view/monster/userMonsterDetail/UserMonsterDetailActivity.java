package com.nestedworld.nestedworld.ui.view.monster.userMonsterDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.UserMonster;
import com.nestedworld.nestedworld.data.database.entities.UserMonsterDao;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import butterknife.BindView;

public class UserMonsterDetailActivity extends BaseAppCompatActivity {
    private final static String ARG_MONSTER = "UserMonsterDetailActivity_ARG_MONSTER";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private UserMonster mUserMonster;

    /*
    ** Public method
     */
    public static void start(@NonNull final Context context, @NonNull final UserMonster userMonster) {
        Intent intent = new Intent(context, UserMonsterDetailActivity.class);
        intent.putExtra(ARG_MONSTER, userMonster.getUserMonsterId());
        context.startActivity(intent);
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_usermonster_detail;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        if (!getIntent().getExtras().containsKey(ARG_MONSTER)) {
            throw new IllegalArgumentException("You should provide a monsterId in intent");
        } else {
            long monsterId = getIntent().getExtras().getLong(ARG_MONSTER, -1);

            mUserMonster = NestedWorldDatabase
                    .getInstance()
                    .getDataBase()
                    .getUserMonsterDao()
                    .queryBuilder()
                    .where(UserMonsterDao.Properties.UserMonsterId.eq(monsterId))
                    .unique();

            if (mUserMonster == null) {
                Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                finish();
            } else {
                setupToolbar();
                UserMonsterDetailFragment.load(getSupportFragmentManager(), mUserMonster, mUserMonster.getMonster());
            }
        }
    }

    /*
    ** Internal method
     */
    private void setupToolbar() {
        //Set the toolbar as actionBar
        setSupportActionBar(toolbar);

        //Get back the Toolbar as actionBar and then custom it
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //customise the actionBar
            actionBar.setTitle(mUserMonster.getMonster().name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
