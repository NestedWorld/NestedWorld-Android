package com.nestedworld.nestedworld.activities.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;

import butterknife.Bind;
import retrofit.Response;
import retrofit.Retrofit;

public class LaunchActivity extends BaseAppCompatActivity {
    @Bind(R.id.imageView_logo_launch)
    ImageView imageView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_launch;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //choose the correct activity
        if (!checkForExistingSession()) {
            //we don't have a session
            //So we start the registration activity & kill current activity
            startActivity(RegistrationActivity.class);
            finish();
        } else {
            //We have a session so we try to update the user information
            updateUserInformation();
        }
    }

    /*
    ** Utils
     */
    private boolean checkForExistingSession() {
        return (UserManager.get(this).getCurrentAccount() != null);
    }

    private void updateUserInformation() {
        imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));

        NestedWorldHttpApi.getInstance(this).getUserInfo(new Callback<UserResponse>() {
            @Override
            public void onSuccess(Response<UserResponse> response, Retrofit retrofit) {
                //store user information under userManager
                UserManager.get(LaunchActivity.this).setUserData(LaunchActivity.this, response.body().user);

                //start the mainActivity & kill current activity
                startActivity(MainMenuActivity.class);
                LaunchActivity.this.finish();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserResponse> response) {
                //display error message
                String errorMessage = RetrofitErrorHandler.getErrorMessage(LaunchActivity.this, errorKind, getString(R.string.error_update_user_info), response);
                Toast.makeText(LaunchActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                //remove user
                UserManager.get(LaunchActivity.this).deleteCurrentAccount(LaunchActivity.this);

                //avoid leek with the static instance
                NestedWorldHttpApi.reset();

                //go to launch screen & kill current activity
                startActivity(RegistrationActivity.class);
                LaunchActivity.this.finish();
            }
        });
    }

}
