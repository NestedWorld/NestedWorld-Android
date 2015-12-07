package com.nestedworld.nestedworld.fragment.profil;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.launch.LaunchActivity;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Logout;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Response;
import retrofit.Retrofit;

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
        return R.layout.fragment_profil;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        final UserManager userManager = UserManager.get(mContext);

        //TODO afficher les bonnes informations (recu dans le /Users)
        textViewGender.setText("null");
        textViewPseudo.setText("kassisdion");
        textViewBirthDate.setText("None");
        textViewCity.setText("null");
        textViewRegisteredAt.setText("2015-10-11T13:58:46.308922+00:00");
        textViewEmail.setText("florian.faisant@gmail.com");
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }

    /*
    ** Butterknife
     */
    @OnClick(R.id.button_logout)
    public void logout() {

        NestedWorldApi.getInstance(mContext).logout(
                new com.nestedworld.nestedworld.api.callback.Callback<Logout>() {
                    @Override
                    public void onSuccess(Response<Logout> response, Retrofit retrofit) {

                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<Logout> response) {

                    }
                });

        //remove user
        UserManager.get(mContext).deleteCurrentAccount(mContext);

        //go to launch screen & kill the current context
        startActivity(LaunchActivity.class);
        ((AppCompatActivity) mContext).finish();
    }
}
