package com.nestedworld.nestedworld.ui.monster.userMonsterDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.orm.query.Condition;
import com.orm.query.Select;

import butterknife.BindView;

public class UserMonsterDetailActivity extends BaseAppCompatActivity{
    private final static String ARG_MONSTER = "UserMonsterDetailActivity_ARG_MONSTER";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private UserMonster mMonster;
    private Monster mMonsterInfo;

    /*
    ** Public method
     */
    public static void start(@NonNull final Context context, @NonNull final UserMonster monster) {
        Intent intent = new Intent(context, UserMonsterDetailActivity.class);
        intent.putExtra(ARG_MONSTER, monster.getId());
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
            mMonster = Select.from(UserMonster.class)
                    .where(Condition.prop("id").eq(monsterId))
                    .first();

            if (mMonster != null) {
                mMonsterInfo = Select.from(Monster.class)
                        .where(Condition.prop("monster_id").eq(mMonster.monsterId))
                        .first();
            }

            if (mMonster == null || mMonsterInfo == null) {
                Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                finish();
            } else {
                setupToolbar();
                UserMonsterDetailFragment.load(getSupportFragmentManager(), mMonster, mMonsterInfo);
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
            actionBar.setTitle(mMonsterInfo.name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
