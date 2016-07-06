package com.nestedworld.nestedworld.fragments.friend;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.fragments.chat.ChatFragment;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.request.combat.AskRequest;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import org.msgpack.value.Value;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendListFragment extends BaseFragment {

    @Bind(R.id.listView_chat_list)
    ListView listView;
    @Bind(R.id.progressView)
    ProgressView progressView;

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
    protected void init(View rootView, Bundle savedInstanceState) {
        populateFriendList();
    }

    private void populateFriendList() {

        List<Friend> friends = Select.from(Friend.class).list();

        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //init adapter for our listView
        final FriendsAdapter friendAdapter = new FriendsAdapter(mContext, friends);
        listView.setAdapter(friendAdapter);

        //add listener on the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatFragment.load(getFragmentManager(), friendAdapter.getItem(position));
            }
        });
    }

    /**
     * * Custom adapter for displaying friend on the listView
     **/
    private static class FriendsAdapter extends ArrayAdapter<Friend> {

        private static final int resource = R.layout.item_friend_home;
        private final Context mContext;

        public FriendsAdapter(@NonNull final Context context, @NonNull final List<Friend> friendList) {
            super(context, resource, friendList);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            FriendHolder friendHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
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
            User currentFriendInfo = currentFriend.info();

            if (currentFriendInfo == null) {
                return null;
            }

            //display the friend name
            friendHolder.friendName.setText(currentFriendInfo.pseudo);

            //display a rounded placeHolder for friend's avatar
            Resources resources = mContext.getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

            //display friend's avatar
            Glide.with(mContext)
                    .load(currentFriendInfo.avatar)
                    .placeholder(roundedBitmapDrawable)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .centerCrop()
                    .into(friendHolder.friendPicture);

            return view;
        }

        private class FriendHolder {
            public ImageView friendPicture;
            public TextView friendName;
        }
    }
}
