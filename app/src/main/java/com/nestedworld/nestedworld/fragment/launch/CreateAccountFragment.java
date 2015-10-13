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
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Register;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignIn;
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

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nestedworld.nestedworld.utils.input.InputChecker.checkEmailFormat;
import static com.nestedworld.nestedworld.utils.input.InputChecker.checkPasswordFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAccountFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = CreateAccountFragment.class.getSimpleName();

    @Bind(R.id.editText_pseudo)
    EditText etPseudo;
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
        final String pseudo = etPseudo.getText().toString();

        if (checkInput(email, password, pseudo)) {
            createAccount(email, password, pseudo);
        }
    }

    /*
    ** Utils
     */
    private void createAccount(final String email, final String password, final String pseudo) {
        progressView.start();

        NestedWorldApi.getInstance(mContext).register(email, password, pseudo,
                new Callback<Register>() {
                    @Override
                    public void success(Register json, Response response) {
                        login(email, password);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressView.stop();

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, error, getString(R.string.error_create_account));
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void login(final String email, final String password) {
        NestedWorldApi.getInstance(mContext).signIn(email, password, new Callback<SignIn>() {
            @Override
            public void success(SignIn json, Response response) {
                progressView.stop();

                //Store user Data
                if (UserManager.get(mContext).setCurrentUser(email, password, json.token, null)) {
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

                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, error, getString(R.string.error_login));
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    ** InputChecker
     */
    private boolean checkInput(final String email, final String password, final String pseudo) {
        if (!checkEmailFormat(email)) {
            etEmail.setError(getString(R.string.editText_email_invalid));
            return false;
        } else if (!checkPasswordFormat(password)) {
            etPassword.setError(getString(R.string.editText_password_invalid));
            return false;
        } else if (TextUtils.isEmpty(pseudo)) {
            etPseudo.setError(getString(R.string.editText_pseudo_invalid));
            return false;
        }
        return true;
    }
}
