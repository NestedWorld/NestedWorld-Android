package com.nestedworld.nestedworld.fragments.registration;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Response;

import static com.nestedworld.nestedworld.helper.input.InputChecker.checkEmailFormat;
import static com.nestedworld.nestedworld.helper.input.InputChecker.checkPasswordFormat;

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

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
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
    protected void init(View rootView, Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife callback
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
    private void createAccount(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo) {
        progressView.start();

        if (mContext == null)
            return;
        NestedWorldHttpApi.getInstance(mContext).register(
                email,
                password,
                pseudo,
                new com.nestedworld.nestedworld.api.http.callback.Callback<RegisterResponse>() {
                    @Override
                    public void onSuccess(Response<RegisterResponse> response) {
                        //Account successfully created, we can log in
                        login(email, password);
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<RegisterResponse> response) {
                        progressView.stop();

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_create_account), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void login(@NonNull final String email, @NonNull final String password) {

        if (mContext == null)
            return;
        NestedWorldHttpApi.getInstance(mContext).signIn(
                email,
                password,
                new com.nestedworld.nestedworld.api.http.callback.Callback<SignInResponse>() {
                    @Override
                    public void onSuccess(Response<SignInResponse> response) {
                        if (mContext == null) {
                            return;
                        }

                        if (UserManager.get(mContext).newAccount(mContext, email, password, response.body().token)) {
                            //display MainMenu and stop this activity
                            startActivity(MainMenuActivity.class);
                            ((Activity) mContext).finish();
                        } else {
                            Toast.makeText(mContext, R.string.error_create_account, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<SignInResponse> response) {
                        if (progressView != null) {
                            progressView.stop();
                        }

                        if (mContext != null) {
                            final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_login), response);
                            Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    /*
    ** Utils
     */
    private boolean checkInput(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo) {
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
