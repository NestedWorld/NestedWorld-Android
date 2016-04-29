package com.nestedworld.nestedworld.activities.registration;

import android.os.Bundle;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.fragments.registration.RegisterFragment;

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