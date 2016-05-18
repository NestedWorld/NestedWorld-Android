package com.nestedworld.nestedworld.activities.launch;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.helper.user.UserManager;

import butterknife.Bind;

public class LaunchActivity extends BaseAppCompatActivity {
    @Bind(R.id.imageView_logo_launch)
    ImageView imageView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //start animation
        imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));

        //choose the correct activity
        if (!checkForExistingSession()) {
            //we don't have a session
            //So we start the registration activity & kill current activity
            startActivity(RegistrationActivity.class);
            finish();
        } else {
            //We have a session so we start the main activity
            startActivity(MainMenuActivity.class);
            finish();
        }
    }

    /*
    ** Utils
     */
    private boolean checkForExistingSession() {
        return (UserManager.get().getUser(this) != null);
    }
}
