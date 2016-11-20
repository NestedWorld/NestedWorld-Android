package com.nestedworld.nestedworld.ui.monster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.orm.query.Condition;
import com.orm.query.Select;

public class MonsterDetailActivity extends BaseAppCompatActivity {


    private final static String ARG_MONSTER = "MonsterDetailActivity_ARG_MONSTER";

    /*
    ** Public method
     */
    public static void start(@NonNull final Context context, @NonNull final Monster monster) {
        Intent intent = new Intent(context, MonsterDetailActivity.class);
        intent.putExtra(ARG_MONSTER, monster.monsterId);
        context.startActivity(intent);
    }

    /*
    ** Life cycle
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
            long monserId = getIntent().getExtras().getLong(ARG_MONSTER, -1);
            Monster monster = Select.from(Monster.class)
                    .where(Condition.prop("monster_id").eq(monserId))
                    .first();

            if (monster == null) {
                Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                finish();
            } else {
                MonsterDetailFragment.load(getSupportFragmentManager(), monster);
            }
        }
    }
}
