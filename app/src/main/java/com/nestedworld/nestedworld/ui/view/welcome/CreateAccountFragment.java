package com.nestedworld.nestedworld.ui.view.welcome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.data.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.helpers.input.InputChecker;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;
import com.nestedworld.nestedworld.ui.view.main.MainMenuActivity;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAccountFragment extends BaseFragment {
    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.editText_pseudo)
    TextInputEditText etPseudo;
    @BindView(R.id.textInputLayout_pseudo)
    TextInputLayout textInputLayoutPseudo;
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
    @BindView(R.id.imageView_createAccount_background)
    ImageView imageViewBackground;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_out, R.anim.fade_in)
                .replace(R.id.container, new CreateAccountFragment())
                .addToBackStack(null)
                .commit();
    }

    /*
     * #############################################################################################
     * # Widget callback binding
     * #############################################################################################
     */
    @OnClick(R.id.button_inscription)
    public void sendCreateAccountRequest() {

        //Retrieve player input
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String pseudo = etPseudo.getText().toString();

        //Check input
        if (!checkInputRegistration(email, password, pseudo)) {
            return;
        }

        //Send request
        sendCreateAccountRequest(email, password, pseudo);
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_create_account;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private boolean checkInputRegistration(@NonNull final String email,
                                           @NonNull final String password,
                                           @NonNull final String pseudo) {
        //Check email
        if (!InputChecker.checkEmailFormat(email)) {
            textInputLayoutUserEmail.setError(getString(R.string.error_emailInvalid));
            return false;
        } else {
            textInputLayoutUserEmail.setErrorEnabled(false);
        }

        //Check password
        if (!InputChecker.checkPasswordFormat(password)) {
            textInputLayoutUserPassword.setError(getString(R.string.error_passwordTooShort));
            return false;
        } else {
            textInputLayoutUserPassword.setErrorEnabled(false);
        }

        //Check pseudo
        if (!InputChecker.checkPseudoFormat(pseudo)) {
            textInputLayoutPseudo.setError(getString(R.string.error_pseudoEmpty));
            return false;
        } else {
            textInputLayoutPseudo.setErrorEnabled(false);
        }

        return true;
    }

    private void sendCreateAccountRequest(@NonNull final String email,
                                          @NonNull final String password,
                                          @NonNull final String pseudo) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        progressView.start();

        //Send request
        NestedWorldHttpApi
                .getInstance()
                .register(email, password, pseudo)
                .enqueue(new NestedWorldHttpCallback<RegisterResponse>() {
                    @Override
                    public void onSuccess(@NonNull Response<RegisterResponse> response) {
                        //Check if fragment hasn't been detach
                        if (mContext == null) {
                            return;
                        }

                        //Account successfully created, we can log in
                        sendLoginRequest(email, password);
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<RegisterResponse> response) {
                        //Check if fragment hasn't been detach
                        if (mContext == null) {
                           return;
                        }

                        //Stop loading animation
                        progressView.stop();

                        //Get error message
                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_createAccount), response);

                        //Display error message
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendLoginRequest(@NonNull final String email,
                                  @NonNull final String password) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Send request
        NestedWorldHttpApi
                .getInstance()
                .signIn(email, password)
                .enqueue(new NestedWorldHttpCallback<SignInResponse>() {
                    @Override
                    public void onSuccess(@NonNull Response<SignInResponse> response) {
                        //Check if fragment hasn't been detach
                        if (mContext == null) {
                            return;
                        }

                        //Create a new session
                        SessionHelper.newSession(email, password, response.body().token);

                        //display MainMenu and stop this activity
                        startActivity(MainMenuActivity.class);
                        ((BaseAppCompatActivity) mContext).finish();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind,
                                        @Nullable Response<SignInResponse> response) {
                        //Check if fragment hasn't been detach
                        if (mContext == null) {
                            return;
                        }

                        //Stop loading animation
                        progressView.stop();

                        //Get error message
                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_request_login), response);

                        //Display error message
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
