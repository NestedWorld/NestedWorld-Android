package com.nestedworld.nestedworld.ui.profil;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.helpers.application.ApplicationHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.welcome.WelcomeActivity;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends BaseFragment {

    @BindView(R.id.textView_gender)
    TextView textViewGender;
    @BindView(R.id.textView_pseudo)
    TextView textViewPseudo;
    @BindView(R.id.textView_birthDate)
    TextView textViewBirthDate;
    @BindView(R.id.textView_city)
    TextView textViewCity;
    @BindView(R.id.textView_registeredAt)
    TextView textViewRegisteredAt;
    @BindView(R.id.textView_email)
    TextView textViewEmail;
    @BindView(R.id.textView_backgroundUrl)
    TextView textViewBackgroundUrl;
    @BindView(R.id.textView_level)
    TextView textViewLevel;
    @BindView(R.id.textView_avatar)
    TextView textViewAvatar;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new ProfileFragment())
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_profil;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Retrieve the session
        Session session = SessionHelper.getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the player
        Player user = session.getUser();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            return;
        }

        /*We display some information*/
        Resources res = getResources();
        textViewGender.setText(String.format(res.getString(R.string.profile_msg_gender), user.gender));
        textViewPseudo.setText(String.format(res.getString(R.string.profile_msg_pseudo), user.pseudo));
        textViewBirthDate.setText(String.format(res.getString(R.string.profile_msg_birthDay), user.birthDate));
        textViewCity.setText(String.format(res.getString(R.string.profile_msg_city), user.city));
        textViewRegisteredAt.setText(String.format(res.getString(R.string.profile_msg_registeredAt), user.registeredAt));
        textViewEmail.setText(String.format(res.getString(R.string.profile_msg_email), user.email));
        textViewBackgroundUrl.setText(String.format(res.getString(R.string.profile_msg_background), user.background));
        textViewLevel.setText(String.format(res.getString(R.string.profile_msg_level), String.valueOf(user.level)));
        textViewAvatar.setText(String.format(res.getString(R.string.profile_msg_avatar), user.avatar));
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_logout)
    public void logout() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        /*Send logout request*/
        NestedWorldHttpApi.getInstance().logout().enqueue(new NestedWorldHttpCallback<LogoutResponse>() {
            @Override
            public void onSuccess(@NonNull Response<LogoutResponse> response) {
                //Logout success
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<LogoutResponse> response) {
                //Logout failed
            }
        });

        ApplicationHelper.logout(mContext);

        //go to launch screen & kill the current context
        Intent intent = new Intent(mContext, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
