package com.nestedworld.nestedworld.activities.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.models.UserMonster;
import com.orm.SugarRecord;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import retrofit2.Response;

public class LaunchActivity extends BaseAppCompatActivity  {
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
        return (UserManager.get(this).getUserEntity() != null);
    }
}
