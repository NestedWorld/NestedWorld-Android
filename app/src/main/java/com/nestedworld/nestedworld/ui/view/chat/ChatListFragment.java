package com.nestedworld.nestedworld.ui.view.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.data.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.ui.adapter.array.ChatAdapter;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class ChatListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public final static String NAME = ChatListFragment.class.getSimpleName();

    @BindView(R.id.listView_chat_list)
    ListView listView;
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.swipeRefreshLayout_friend_list)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.textview_no_friend)
    TextView textViewNoFriend;

    private ChatAdapter mAdapter;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new ChatListFragment(), NAME)
                .addToBackStack(null)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_chat;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupActionBar();
        setupListView();
        populateFriendList();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    /*
    ** SwipeRefreshLayout.OnRefreshListener implementation
     */
    @Override
    public void onRefresh() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        swipeRefreshLayout.setRefreshing(true);

        new FriendsUpdater().start(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Do not update adapter here (will be done with EventBus)

                //Stop loading animation
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }
                //Stop loading animation
                swipeRefreshLayout.setRefreshing(false);

                //Display error message
                Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
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

    /*
    ** Internal method
     */
    private void setupActionBar() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.mainMenu_action_chat));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setupListView() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //init adapter for our listView
        mAdapter = new ChatAdapter(mContext);
        listView.setAdapter(mAdapter);

        //add listener on the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                Friend selectedFriend = mAdapter.getItem(position);
                if (selectedFriend != null) {
                    ChatFragment.load(((BaseAppCompatActivity) mContext).getSupportFragmentManager(), selectedFriend);
                } else {
                    Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @UiThread
    private void populateFriendList() {
        //Retrieve friend from ORM
        List<Friend> friends = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getFriendDao()
                .loadAll();

        if (friends == null || friends.isEmpty()) {
            mAdapter.clear();
            textViewNoFriend.setVisibility(View.VISIBLE);
        } else {
            //Hide "no friend" text
            textViewNoFriend.setVisibility(View.GONE);

            //Update adapter
            mAdapter.clear();
            mAdapter.addAll(friends);
        }
    }
}
