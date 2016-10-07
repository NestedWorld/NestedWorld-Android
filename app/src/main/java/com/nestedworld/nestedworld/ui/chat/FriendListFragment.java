package com.nestedworld.nestedworld.ui.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import java.util.List;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.listView_chat_list)
    ListView listView;
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Bind(R.id.swipeRefreshLayout_friend_list)
    SwipeRefreshLayout swipeRefreshLayout;

    private FriendsAdapter mAdapter;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FriendListFragment());
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_chat;
    }

    @Override
    protected void init(@NonNull View rootView, @NonNull Bundle savedInstanceState) {
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

        new FriendsUpdater(mContext, new OnEntityUpdated() {
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
            public void onError(KIND errorKind) {
                //check if fragment hasn't been detach
                if (mContext != null) {
                    //Stop loading animation
                    swipeRefreshLayout.setRefreshing(false);

                    //Display error message
                    Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    /*
    ** Internal method
     */
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

    /**
     * * Custom adapter for displaying friend on the listView
     **/
    private static class FriendsAdapter extends ArrayAdapter<Friend> {

        private static final int resource = R.layout.item_friend_home;

        public FriendsAdapter(@NonNull final Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            View view;
            FriendHolder friendHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((AppCompatActivity) getContext()).getLayoutInflater();
                view = layoutInflater.inflate(resource, parent, false);

                friendHolder = new FriendHolder();
                friendHolder.friendPicture = (ImageView) view.findViewById(R.id.imageView_item_friend);
                friendHolder.friendName = (TextView) view.findViewById(R.id.textView_item_friend);

                view.setTag(friendHolder);
            } else {
                friendHolder = (FriendHolder) convertView.getTag();
                view = convertView;
            }

            //get the currentFriend
            Friend currentFriend = getItem(position);
            if (currentFriend == null) {
                return view;
            }

            //get the currentFriend information
            User currentFriendInfo = currentFriend.info();
            if (currentFriendInfo == null) {
                return view;
            }

            //display the friend name
            friendHolder.friendName.setText(currentFriendInfo.pseudo);

            //display a rounded placeHolder for friend's avatar
            Resources resources = getContext().getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

            //display friend's avatar
            Glide.with(getContext())
                    .load(currentFriendInfo.avatar)
                    .placeholder(roundedBitmapDrawable)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .centerCrop()
                    .into(friendHolder.friendPicture);

            return view;
        }

        private static class FriendHolder {
            public ImageView friendPicture;
            public TextView friendName;
        }
    }
}
