package com.nestedworld.nestedworld.ui.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.mainMenu.MainMenuActivity;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = LoginFragment.class.getSimpleName();

    @BindView(R.id.editText_userEmail)
    TextInputEditText etEmail;
    @BindView(R.id.textInputLayout_userEmail)
    TextInputLayout textInputLayoutUserEmail;
    @BindView(R.id.editText_userPassword)
    TextInputEditText etPassword;
    @BindView(R.id.textInputLayout_userPassword)
    TextInputLayout textInputLayoutUserPassword;
    @BindView(R.id.progressView)
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
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife callback
     */
    @OnClick(R.id.nav_back)
    public void back() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).onBackPressed();
    }

    @OnClick(R.id.button_login)
    public void login() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve user input
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Check input
        if (!checkInputForLogin(email, password)) {
            return;
        }

        //Send login request
        sendLoginRequest(email, password);
    }

    @OnClick(R.id.textView_forgotPassword)
    public void forgotPassword() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve user input
        String email = etEmail.getText().toString();

        //Check user input
        if (!checkInputForForgotPassword(email)) {
            return;
        }

        //Send request
        NestedWorldHttpApi.getInstance().forgotPassword(email).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onSuccess(Response<ForgotPasswordResponse> response) {
                //check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Warn user an email has been send
                Toast.makeText(mContext, getString(R.string.registration_msg_passwordSend), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<ForgotPasswordResponse> response) {
                //check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Get error message
                String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_request_forgotPassword), response);

                //Display error message
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
    ** Internal method
     */
    private boolean checkInputForLogin(@NonNull final String email, @NonNull final String password) {
        //Check email
        if (!email.isEmpty()) {
            textInputLayoutUserEmail.setErrorEnabled(false);
        } else {
            textInputLayoutUserEmail.setErrorEnabled(false);
            textInputLayoutUserEmail.setError("Please enter your email");
            return false;
        }

        //Check password
        if (!password.isEmpty()) {
            textInputLayoutUserPassword.setErrorEnabled(false);
        } else {
            textInputLayoutUserPassword.setErrorEnabled(false);
            textInputLayoutUserPassword.setError("Please enter yout password");
            return false;
        }

        return true;
    }

    private boolean checkInputForForgotPassword(@NonNull final String email) {
        if (email.isEmpty()) {
            textInputLayoutUserEmail.setErrorEnabled(true);
            textInputLayoutUserEmail.setError("Please enter your email");
            return false;
        } else {
            textInputLayoutUserEmail.setErrorEnabled(false);
            textInputLayoutUserPassword.setErrorEnabled(false);
            return true;
        }
    }

    private void sendLoginRequest(@NonNull final String email, @NonNull final String password) {
        NestedWorldHttpApi.getInstance().signIn(email, password).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onSuccess(Response<SignInResponse> response) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Stop the loading animation
                progressView.stop();

                //Create a new session
                SessionHelper.newSession(email, password, response.body().token);

                //display the mainMenu and stop the launchActivity
                startActivity(MainMenuActivity.class);
                ((FragmentActivity) mContext).finish();
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<SignInResponse> response) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Stop loading animation
                progressView.stop();

                //Get error message
                String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_request_login), response);

                //Display error message
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
