package com.nestedworld.nestedworld.ui.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.mainMenu.MainMenuActivity;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Response;

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

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
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
    protected void init(View rootView, Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife callback
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

        if (mContext == null) {
            return;
        }

        NestedWorldHttpApi.getInstance(mContext).signIn(email, password).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onSuccess(Response<SignInResponse> response) {
                progressView.stop();

                //Create a new session
                SessionHelper.newSession(email, password, response.body().token);

                //display the mainMenu and stop the launchActivity
                startActivity(MainMenuActivity.class);
                ((FragmentActivity) mContext).finish();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<SignInResponse> response) {
                progressView.stop();

                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_request_login), response);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.textView_forgotPassword)
    public void forgotPassword() {
        final String email = etEmail.getText().toString();

        if (mContext == null) {
            return;
        }

        NestedWorldHttpApi.getInstance(mContext).forgotPassword(email).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onSuccess(Response<ForgotPasswordResponse> response) {
                //check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                Toast.makeText(mContext, getString(R.string.registration_msg_passwordSend), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<ForgotPasswordResponse> response) {
                //check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_request_forgotPassword), response);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
