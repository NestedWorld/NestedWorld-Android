package com.nestedworld.nestedworld.activity.launch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activity.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.fragment.launch.LaunchFragment;
import com.newrelic.agent.android.NewRelic;

import butterknife.Bind;

public class LaunchActivity extends BaseAppCompatActivity {
    @Bind(R.id.default_background)
    ImageView mImageViewBackground;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        mImageViewBackground.setBackgroundResource(R.drawable.logo);
        mImageViewBackground.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initLogics(Bundle savedInstanceState) {
        //init crash logger
        NewRelic.withApplicationToken("AAfa7011e7d073accc8bc537079365343349369b8f").start(this.getApplication());

        //choose the corect view
        if (!checkForExistingSession()) {
            LaunchFragment.load(this.getSupportFragmentManager());
        }
        else {
            startActivity(MainMenuActivity.class);
        }
    }

    /*
    ** Utils
     */
    private boolean checkForExistingSession() {
        //TODO make a real check
        return false;
    }
}