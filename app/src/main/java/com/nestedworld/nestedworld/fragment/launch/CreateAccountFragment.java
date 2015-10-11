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
import com.nestedworld.nestedworld.api.models.User;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.nestedworld.nestedworld.utils.log.LogHelper;
import com.rey.material.widget.ProgressView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAccountFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = CreateAccountFragment.class.getSimpleName();

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
        fragmentTransaction.replace(R.id.container, new CreateAccountFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_create_account;
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
                        LogHelper.d(TAG, "Success");
                        Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        LogHelper.d(TAG, "Cancel");
                        Toast.makeText(mContext, "Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LogHelper.d(TAG, "Error");
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

    @OnClick(R.id.button_inscription)
    public void createAccount() {
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        if (checkInput(email, password)) {
            createAccount(email, password);
        }
    }

    /*
    ** Utils
     */
    private void createAccount(final String email, final String password) {
        progressView.start();

        NestedWorldApi.getInstance(mContext).signUp(email, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                progressView.stop();

                //Store user Data
                Bundle bundle = new Bundle();
                bundle.putString("token", user.getToken());
                if (UserManager.get(mContext).setCurrentUser(email, password, user.getToken(), null)) {
                    //display MainMenu and then stop le launchMenu
                    startActivity(MainMenuActivity.class);
                    ((FragmentActivity) mContext).finish();
                } else {
                    Toast.makeText(mContext, R.string.error_create_account, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressView.stop();

                final String errorMessage = RetrofitErrorHandler.getErrorMessage(error, mContext);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    ** InputChecker
     */
    private boolean checkInput(final String email, final String password) {
        if (!checkEmailFormat(email)) {
            etEmail.setError(getString(R.string.editText_email_invalid));
            return false;
        } else if (!checkPasswordFormat(password)) {
            etPassword.setError(getString(R.string.editText_password_invalid));
            return false;
        }
        return true;
    }

    private boolean checkEmailFormat(final String email) {
        boolean isValid = false;

        final String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        final Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean checkPasswordFormat(final String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }
}
