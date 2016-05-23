package com.nestedworld.nestedworld.fragments.profil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.helpers.user.UserManager;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.models.User;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = ProfileFragment.class.getSimpleName();

    @Bind(R.id.textView_gender)
    TextView textViewGender;
    @Bind(R.id.textView_pseudo)
    TextView textViewPseudo;
    @Bind(R.id.textView_birthDate)
    TextView textViewBirthDate;
    @Bind(R.id.textView_city)
    TextView textViewCity;
    @Bind(R.id.textView_registeredAt)
    TextView textViewRegisteredAt;
    @Bind(R.id.textView_email)
    TextView textViewEmail;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, final boolean toBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ProfileFragment());
        if (toBackStack) {
            fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        }
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_profil;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        /*We retrieve the userData as the string and we decode the string*/
        if (mContext != null) {
            final User user = UserManager.get().getUser(mContext);
            if (user != null) {
                /*We display some information from the decoded user*/
                textViewGender.setText(user.gender);
                textViewPseudo.setText(user.pseudo);
                textViewBirthDate.setText(user.birth_date);
                textViewCity.setText(user.city);
                textViewRegisteredAt.setText(user.registered_at);
                textViewEmail.setText(user.email);
            }
        }
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_logout)
    public void logout() {
        if (mContext == null)
            return;
        NestedWorldHttpApi.getInstance(mContext).logout(
                new com.nestedworld.nestedworld.network.http.callback.Callback<LogoutResponse>() {
                    @Override
                    public void onSuccess(Response<LogoutResponse> response) {
                        //Server has accept our logout
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<LogoutResponse> response) {
                        //Server refuse our logout
                    }
                });

        //remove user
        UserManager.get().deleteCurrentUser(mContext);

        //avoid leek with the static instance
        NestedWorldHttpApi.reset();
        NestedWorldSocketAPI.reset();

        //go to launch screen & kill the current context
        startActivity(RegistrationActivity.class);
        ((AppCompatActivity) mContext).finish();
    }
}
