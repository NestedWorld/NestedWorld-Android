package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.FriendsAdapter;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.dialog.AddFriendDialog;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.events.socket.generic.OnResultResponseEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;
import com.nestedworld.nestedworld.network.socket.models.message.generic.ResultMessage;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
        setupListView();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

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
    public void onFriendUpdated(OnFriendsUpdatedEvent onFriendsUpdatedEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateFriendList();
            }
        });
    }

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

    private void populateFriendList() {
        //Retrieve entity from orm
        List<Friend> friends = Select.from(Friend.class).list();

        //Remove old entity and add new one
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

        AddFriendDialog.show(getChildFragmentManager());
    }
}
