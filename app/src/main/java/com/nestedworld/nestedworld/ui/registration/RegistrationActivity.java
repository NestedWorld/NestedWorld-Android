package com.nestedworld.nestedworld.ui.registration;

import android.os.Bundle;

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
    protected void init(Bundle savedInstanceState) {
        RegisterFragment.load(this.getSupportFragmentManager(), false);
    }
}