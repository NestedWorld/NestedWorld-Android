package com.nestedworld.nestedworld.fragment.profil;

import com.nestedworld.nestedworld.NestedWorldApp;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.launch.LaunchActivity;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Logout;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends BaseFragment {
    public final static String FRAGMENT_NAME = ProfileFragment.class.getSimpleName();

    @Bind(R.id.textView_username)
    TextView textViewUsername;

    @Bind(R.id.textView_token)
    TextView textViewToken;

    public static void load(final FragmentManager fragmentManager, final boolean toBackStack) {
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
        textViewUsername.setText(userManager.getCurrentAccountName());
        textViewToken.setText(userManager.getCurrentAuthToken(mContext));
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }

    /*
    ** Butterknife
     */
    @OnClick(R.id.button_logout)
    public void logout() {

        NestedWorldApi.getInstance().logout(UserManager.get(mContext).getCurrentAuthToken(mContext),
                new Callback<Logout>() {
                    @Override
                    public void success(Logout logout, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
        //remove user
        UserManager.get(mContext).deleteCurrentAccount(mContext);

        //go to launch screen & kill the current context
        startActivity(LaunchActivity.class);
        ((AppCompatActivity) mContext).finish();
    }
}
