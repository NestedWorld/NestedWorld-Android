package com.nestedworld.nestedworld.ui.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.FriendsAdapter;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public final static String NAME = FriendListFragment.class.getSimpleName();

    @BindView(R.id.listView_chat_list)
    ListView listView;
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.swipeRefreshLayout_friend_list)
    SwipeRefreshLayout swipeRefreshLayout;

    private FriendsAdapter mAdapter;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FriendListFragment(), NAME)
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
        setupActionBar();
        setupListView();
        populateFriendList();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

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
                if (mContext != null) {
                    //Update adapter
                    populateFriendList();

                    //Stop loading animation
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //check if fragment hasn't been detach
                if (mContext != null) {
                    //Stop loading animation
                    swipeRefreshLayout.setRefreshing(false);

                    //Display error message
                    Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
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
        mAdapter = new FriendsAdapter(mContext);
        listView.setAdapter(mAdapter);

        //add listener on the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selectedFriend = mAdapter.getItem(position);
                if (selectedFriend != null) {
                    ChatFragment.load(getFragmentManager(), selectedFriend);
                } else {
                    Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populateFriendList() {
        //Retrieve friend from ORM
        List<Friend> friends = Select.from(Friend.class).list();

        //Update adapter
        mAdapter.clear();
        mAdapter.addAll(friends);
    }
}
