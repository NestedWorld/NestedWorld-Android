package com.nestedworld.nestedworld.Activity;

import android.os.Bundle;

import com.nestedworld.nestedworld.Activity.Base.BaseActivity;
import com.nestedworld.nestedworld.R;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}

