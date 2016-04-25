package com.nestedworld.nestedworld.activities.launch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragments.launch.LaunchFragment;

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
    protected void init(Bundle savedInstanceState) {
        /*Init UI*/
        mImageViewBackground.setBackgroundResource(R.drawable.logo);
        mImageViewBackground.setVisibility(View.VISIBLE);

        /*Init logics*/
        //choose the correct view
        if (!checkForExistingSession()) {
            //we don't have a session so we display the launch screen
            LaunchFragment.load(this.getSupportFragmentManager(), false);
        } else {
            //We have a session so we go to the main menu
            startActivity(MainMenuActivity.class);
            finish();
        }
    }

    /*
    ** Utils
     */
    private boolean checkForExistingSession() {
        return (UserManager.get(this).getCurrentAccount() != null);
    }
}