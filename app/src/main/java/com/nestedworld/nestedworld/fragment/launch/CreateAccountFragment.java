package com.nestedworld.nestedworld.fragment.launch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.User;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateAccountFragment extends BaseFragment {

    @Bind(R.id.editText_userEmail)
    EditText etEmail;
    @Bind(R.id.editText_userPassword)
    EditText etPassword;
    @Bind(R.id.progressView)
    ProgressView progressView;

    public static void load(final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.container, new CreateAccountFragment());
        fragmentTransaction.addToBackStack("CreateAccountFragment");
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
    protected void initVariable(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

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

        NestedWorldApi.getInstance(getContext()).signUp(email, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                progressView.stop();
                Toast.makeText(getContext(), "CreateAccount successfull", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                progressView.stop();
                Toast.makeText(getContext(), "CreateAccount failed", Toast.LENGTH_LONG).show();
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
        }
        else if (!checkPasswordFormat(password)) {
            etPassword.setError(getString(R.string.editText_password_invalid));
            return false;
        }
        return true;
    }

    private boolean checkEmailFormat(final String email) {
        boolean isValid = false;

        final String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean checkPasswordFormat(final String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }
}
