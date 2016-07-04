package com.nestedworld.nestedworld.fragments.mainMenu.tabs.home;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.User;
import com.orm.query.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeFriendFragment extends BaseFragment {

    @Bind(R.id.listView_home_friends)
    ListView listView;

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home_friend_list;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        populateFriendList();
    }

    /*
    ** Private method
     */
    private void populateFriendList() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        List<Friend> friends = Select.from(Friend.class).list();

        //init adapter for our listView
        final FriendsAdapter friendAdapter = new FriendsAdapter(mContext, friends);
        listView.setAdapter(friendAdapter);
    }

    /*
    ** Butterknife callback
     */
    @OnClick(R.id.button_add_friend)
    public void addFriend() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //TODO display popup for adding friend
        Toast.makeText(mContext, "Incoming feature...", Toast.LENGTH_LONG).show();
    }

    /**
     * * Custom adapter for displaying friend on the listView
     **/
    private static class FriendsAdapter extends ArrayAdapter<Friend> {

        private static final int resource = R.layout.item_friend;
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
