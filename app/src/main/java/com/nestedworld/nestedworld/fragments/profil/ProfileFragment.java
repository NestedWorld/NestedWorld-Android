package com.nestedworld.nestedworld.fragments.profil;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.registration.RegistrationActivity;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.database.DataBaseHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;

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
    @Bind(R.id.textView_backgroundUrl)
    TextView textViewBackgroundUrl;
    @Bind(R.id.textView_level)
    TextView textViewLevel;
    @Bind(R.id.textView_avatar)
    TextView textViewAvatar;

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
        //Retrieve the session
        Session session = SessionManager.get().getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the user
        User user = session.getUser();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            onFatalError();
            return;
        }

        /*We display some information*/
        Resources res = getResources();
        textViewGender.setText(String.format(res.getString(R.string.profile_msg_gender), user.gender));
        textViewPseudo.setText(String.format(res.getString(R.string.profile_msg_pseudo), user.pseudo));
        textViewBirthDate.setText(String.format(res.getString(R.string.profile_msg_birthDay), user.birth_date));
        textViewCity.setText(String.format(res.getString(R.string.profile_msg_city), user.city));
        textViewRegisteredAt.setText(String.format(res.getString(R.string.profile_msg_registeredAt), user.registered_at));
        textViewEmail.setText(String.format(res.getString(R.string.profile_msg_email), user.email));
        textViewBackgroundUrl.setText(String.format(res.getString(R.string.profile_msg_background), user.background));
        textViewLevel.setText(String.format(res.getString(R.string.profile_msg_level), user.level));
        textViewAvatar.setText(String.format(res.getString(R.string.profile_msg_avatar), user.avatar));
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_logout)
    public void logout() {
        //Check if fragment hasn't been detach
        if (mContext == null)
            return;

        /*Send logout request*/
        NestedWorldHttpApi.getInstance(mContext).logout().enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onSuccess(Response<LogoutResponse> response) {
                //Logout success
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<LogoutResponse> response) {
                //Logout failed
            }
        });

        //remove user
        SessionManager.get().deleteSession();

        //avoid leak with the static instance
        NestedWorldHttpApi.reset();
        NestedWorldSocketAPI.reset();

        //clean db
        DataBaseHelper.cleanDataBase();

        //go to launch screen & kill the current context
        Intent intent = new Intent(mContext, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
