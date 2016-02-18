package com.nestedworld.nestedworld.activity.chat;

import android.os.Bundle;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.fragment.chat.ChatFragment;
import com.nestedworld.nestedworld.fragment.fight.FightFragment;

public class ChatActivity extends BaseAppCompatActivity {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
        ChatFragment.load(getSupportFragmentManager());
    }
}
