package com.nestedworld.nestedworld.activity.launch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activity.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.fragment.launch.LaunchFragment;

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
        //choose the corect view
        if (!checkForExistingSession()) {
            //we don't have a sesion so we display the launch screen
            LaunchFragment.load(this.getSupportFragmentManager());
        }
        else {
            //We have a sesion so we go to the main menu
            startActivity(MainMenuActivity.class);
            finish();
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