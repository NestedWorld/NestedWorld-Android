package com.nestedworld.nestedworld.fragment.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignInResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Response;
import retrofit.Retrofit;

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
    protected void initUI(Bundle savedInstanceState) {
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

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
    private void createAccount(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo) {
        progressView.start();

        NestedWorldApi.getInstance(mContext).register(
                email,
                password,
                pseudo,
                new com.nestedworld.nestedworld.api.callback.Callback<RegisterResponse>() {
                    @Override
                    public void onSuccess(Response<RegisterResponse> response, Retrofit retrofit) {
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

        NestedWorldApi.getInstance(mContext).signIn(
                email,
                password,
                new com.nestedworld.nestedworld.api.callback.Callback<SignInResponse>() {
                    @Override
                    public void onSuccess(Response<SignInResponse> response, Retrofit retrofit) {
                        progressView.stop();

                        //Store user Data
                        if (UserManager.get(mContext).setCurrentUser(mContext, email, password, response.body().token, null)) {
                            //display MainMenu and then stop le launchMenu
                            startActivity(MainMenuActivity.class);
                            ((FragmentActivity) mContext).finish();
                        } else {
                            Toast.makeText(mContext, R.string.error_create_account, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<SignInResponse> response) {
                        progressView.stop();

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_login), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /*
    ** InputChecker
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
