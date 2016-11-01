package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.FriendsAdapter;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.socket.generic.OnResultResponseEvent;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;
import com.nestedworld.nestedworld.network.socket.models.message.generic.ResultMessage;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

public class HomeFriendFragment extends BaseFragment {

    @BindView(R.id.listView_home_friends)
    ListView listView;
    @BindView(R.id.progressView)
    ProgressView progressView;

    private FriendsAdapter mAdapter;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home_friend_list;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupListView();
        populateFriendList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onResultMessage(OnResultResponseEvent resultResponseEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Parse response
        ResultMessage resultMessage = resultResponseEvent.getMessage();
        if (resultMessage.getIdKind() == SocketMessageType.MessageKind.TYPE_COMBAT_ASK) {
            AskMessage askMessage = new AskMessage(resultMessage.getMessage(), resultMessage.getMessageKind(), resultMessage.getIdKind());

            //Check if we have an error
            if (askMessage.getResult() != null && askMessage.getResult().equals("error")) {

                //Check if we have an error message
                if (askMessage.getMessage() != null) {
                    //display error from server
                    Toast.makeText(mContext, askMessage.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    //Display generic error
                    Toast.makeText(mContext, R.string.tabHome_msg_requestFightFail, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, R.string.tabHome_msg_requestFightSuccess, Toast.LENGTH_LONG).show();
            }
        }
    }


    /*
    ** Private method
     */
    private void setupListView() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //init adapter for our listView
        mAdapter = new FriendsAdapter(mContext);
        listView.setAdapter(mAdapter);
    }

    private void updateFriendList() {
        //Check if framgnent hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        progressView.start();

        new FriendsUpdater().start(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                populateFriendList();

                //Stop loading animation
                progressView.stop();
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //Stop loading animation
                progressView.stop();
            }
        });
    }

    private void populateFriendList() {
        //Retrieve entity from orm
        List<Friend> friends = Select.from(Friend.class).list();

        //Remove old entitu and add new one
        mAdapter.clear();
        mAdapter.addAll(friends);
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.fab_add_friend)
    public void addFriend() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Instantiate a popup
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(getString(R.string.tabHome_title_addFriend));
        alertDialog.setMessage(getString(R.string.tabHome_msg_friendPseudo));

        //Inflate popup
        final View view = getActivity().getLayoutInflater().inflate(R.layout.inflater_alertdialog_add_friend, null);
        final EditText editTextPseudo = (EditText) view.findViewById(R.id.editText_addFriend);
        alertDialog.setView(view);

        //Set callback
        alertDialog.setPositiveButton(getString(R.string.tabHome_msg_addFriend), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Start loading animation
                progressView.start();

                //Retrieve input
                String pseudo = editTextPseudo.getText().toString();

                //Send request
                NestedWorldHttpApi.getInstance().addFriend(pseudo).enqueue(new NestedWorldHttpCallback<AddFriendResponse>() {
                    @Override
                    public void onSuccess(@NonNull Response<AddFriendResponse> response) {
                        //Update Orm and adapter
                        updateFriendList();

                        //Display message
                        Toast.makeText(mContext, R.string.tab_home_msg_addFriendSuccess, Toast.LENGTH_LONG).show();

                        //Stop loading animation
                        progressView.stop();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<AddFriendResponse> response) {
                        //Initialise a default error message
                        String errorMessage = getString(R.string.tab_home_msg_addFriendFailed);

                        //Try to retrieve response.body.message
                        String serverError = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, response);

                        //If we have a serverError we complete our default error message
                        if (serverError != null) {
                            errorMessage += " : " + serverError;
                        }

                        //Display the message
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();

                        //Stop loading animation
                        progressView.stop();
                    }
                });
            }
        });
        alertDialog.setNegativeButton(getString(R.string.tabHome_msg_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        //Display popup
        alertDialog.show();
    }
}
