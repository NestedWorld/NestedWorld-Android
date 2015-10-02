package com.nestedworld.nestedworld.fragment.profil;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activity.launch.LaunchActivity;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;

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
        textViewUsername.setText("" + userManager.getCurrentAccountName());
        textViewToken.setText("" + userManager.getCurrentAuthToken());
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }

    /*
    ** Butterknife
     */
    @OnClick(R.id.button_logout)
    public void logout() {
        final UserManager userManager = UserManager.get(mContext);
        if (userManager.deleteCurrentAccount()) {
            startActivity(LaunchActivity.class);
        } else {
            //TODO use a string.xml reference
            Toast.makeText(mContext, "Impossible de vous deconnecter", Toast.LENGTH_LONG).show();
        }
    }
}
