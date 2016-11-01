package com.nestedworld.nestedworld.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Response;

public class AddFriendDialog extends BaseDialogFragment {

    private final static String TAG = AddFriendDialog.class.getSimpleName();

    @BindView(R.id.textInputLayout_friendPseudo)
    TextInputLayout textInputLayoutFriendPseudo;
    @BindView(R.id.editText_friendPseudo)
    TextInputEditText textInputEditTextFriendPseudo;
    @BindView(R.id.progressView)
    ProgressView progressView;

    private Unbinder mUnbinder;

    /*
    ** Public method
     */
    public static void show(@NonNull final FragmentManager fragmentManager) {
        new AddFriendDialog().show(fragmentManager, TAG);
    }

    /*
    ** Life cycle
     */
    @Override
    protected Builder build(Builder initialBuilder) {
        return initialBuilder
                .setTitle(R.string.tabHome_title_addFriend)
                .setMessage(R.string.tabHome_title_addFriendDetail)
                .setPositiveButton(R.string.tabHome_msg_addFriend, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendAddFriendRequest();
                    }
                })
                .setView(getViewContent());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mUnbinder = null;
    }

    /*
    ** Internal method
     */
    @NonNull
    private View getViewContent() {
        //Create the view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.inflater_alertdialog_add_friend, null);

        //Retrieve widget
        mUnbinder = ButterKnife.bind(this, view);

        //Return the newly created view
        return view;
    }

    private void setOnLoadingMode(final boolean loadingMode) {
        if (loadingMode) {
            progressView.start();
            progressView.setVisibility(View.VISIBLE);
            textInputLayoutFriendPseudo.setVisibility(View.GONE);
        } else {
            progressView.stop();
            progressView.setVisibility(View.GONE);
            textInputLayoutFriendPseudo.setVisibility(View.VISIBLE);
        }
    }

    private void sendAddFriendRequest() {
        //Start loading animation
        setOnLoadingMode(true);

        //Retrieve input
        String pseudo = textInputEditTextFriendPseudo.getText().toString();

        //Send request
        NestedWorldHttpApi
                .getInstance()
                .addFriend(pseudo)
                .enqueue(new NestedWorldHttpCallback<AddFriendResponse>() {
                    @Override
                    public void onSuccess(@NonNull Response<AddFriendResponse> response) {
                        //Check if dialog hasn't been dismiss
                        if (mUnbinder == null) {
                            return;
                        }

                        //Stop loading animation
                        setOnLoadingMode(false);

                        //Handle response
                        onAddFriendSuccess();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<AddFriendResponse> response) {
                        //Check if dialog hasn't been dismiss
                        if (mUnbinder == null) {
                            return;
                        }

                        //Stop loading animation
                        setOnLoadingMode(false);

                        //Handle response
                        onAddFriendFailure(RetrofitErrorHandler.getErrorMessage(getContext(), errorKind, response));
                    }
                });
    }

    private void onAddFriendSuccess() {
        //Update friend list (ORM)
        new FriendsUpdater().start(null);

        //Display message
        Toast.makeText(getContext(), R.string.tab_home_msg_addFriendSuccess, Toast.LENGTH_LONG).show();
    }

    private void onAddFriendFailure(@Nullable String cause) {
        //Initialise a default error message
        String errorMessage = getString(R.string.tab_home_msg_addFriendFailed);

        //If we have an error from the server, we complete our default error message
        if (cause != null) {
            errorMessage += " : " + cause;
        }

        //Display the message
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }
}
