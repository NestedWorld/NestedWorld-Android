package com.nestedworld.nestedworld.ui.registration;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;

public class RegistrationActivity extends BaseAppCompatActivity {
    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_registration;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        RegisterFragment.load(this.getSupportFragmentManager(), false);
    }
}