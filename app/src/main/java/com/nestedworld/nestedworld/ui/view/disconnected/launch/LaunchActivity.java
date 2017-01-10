package com.nestedworld.nestedworld.ui.view.disconnected.launch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.connected.main.MainMenuActivity;
import com.nestedworld.nestedworld.ui.view.disconnected.welcome.WelcomeActivity;

import butterknife.BindView;

public class LaunchActivity extends BaseAppCompatActivity {
    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.imageView_logo_launch)
    ImageView imageView;

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
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
    protected void init(@Nullable Bundle savedInstanceState) {
        //start animation
        imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));

        //Run in background (avoid blocking animation)
        new Thread(new Runnable() {
            @Override
            public void run() {
                //choose the correct activity
                if (!checkForExistingSession()) {
                    //we don't have a session
                    //So we start the registration activity & kill current activity
                    LogHelper.d(TAG, "Going for WelcomeActivity");
                    startActivity(WelcomeActivity.class);
                    finish();
                } else {
                    //We have a session so we start the main activity
                    LogHelper.d(TAG, "Going for MainMenuActivity");
                    startActivity(MainMenuActivity.class);
                    finish();
                }
            }
        }).start();
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private boolean checkForExistingSession() {
        return (SessionHelper.getSession() != null);
    }
}
