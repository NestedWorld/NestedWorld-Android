package com.nestedworld.nestedworld.fragment.launch;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nestedworld.nestedworld.NestedWorldApp;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.ForgotPassword;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignIn;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = LoginFragment.class.getSimpleName();

    @Bind(R.id.editText_userEmail)
    EditText etEmail;
    @Bind(R.id.editText_userPassword)
    EditText etPassword;
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Bind(R.id.facebook_login_button)
    LoginButton facebookLoginButton;

    public static void load(final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.container, new LoginFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NestedWorldApp.get().getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {
        facebookLoginButton.setFragment(this);
        facebookLoginButton.registerCallback(NestedWorldApp.get().getCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e(TAG, "Success");
                        Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "Cancel");
                        Toast.makeText(mContext, "Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e(TAG, "Error");
                        Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /*
    ** ButterKnife
     */
    @OnClick(R.id.nav_back)
    public void back() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.button_login)
    public void login() {

        progressView.start();

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        NestedWorldApi.getInstance(mContext).signIn(email, password,
                new Callback<SignIn>() {
                    @Override
                    public void success(SignIn json, Response response) {
                        progressView.stop();

                        if (UserManager.get(mContext).setCurrentUser(email, password, json.token, null)) {
                            //display the mainMenu and stop the launchActivity
                            startActivity(MainMenuActivity.class);
                            ((FragmentActivity) mContext).finish();
                        } else {
                            Toast.makeText(mContext, R.string.error_create_account, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressView.stop();

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, error, getString(R.string.error_login));
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.textView_forgotPassword)
    public void forgotPassword() {
        final String email = etEmail.getText().toString();

        NestedWorldApi.getInstance(mContext).forgotPassword(email,
                new Callback<ForgotPassword>() {
                    @Override
                    public void success(ForgotPassword json, Response response) {
                        Toast.makeText(mContext, getString(R.string.password_send), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, error, getString(R.string.error_forgot_password));
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
