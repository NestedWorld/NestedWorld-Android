package com.nestedworld.nestedworld.fragment.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.apiResponse.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import butterknife.Bind;

public class ChatListFragment extends BaseFragment {

    @Bind(R.id.listView_chat_list)
    ListView listViewChatList;
    @Bind(R.id.progressView)
    ProgressView progressView;
    private FriendAdapter friendAdapter;

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ChatListFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_chat;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        initFriendList();
    }

    private void initFriendList() {

        progressView.start();

        //TODO retrieve Friend list from API
        ArrayList<FriendsResponse.Friend> friendList = new ArrayList<>();
        friendList.add(new FriendsResponse.Friend("Guillaume"));
        friendList.add(new FriendsResponse.Friend("Florian"));
        friendList.add(new FriendsResponse.Friend("Thomas"));
        friendList.add(new FriendsResponse.Friend("Cerise"));
        friendList.add(new FriendsResponse.Friend("Vincent"));
        friendList.add(new FriendsResponse.Friend("Quentin"));

        //init adapter for our listView
        friendAdapter = new FriendAdapter(mContext, friendList);
        listViewChatList.setAdapter(friendAdapter);

        listViewChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatFragment.load(getFragmentManager(), friendAdapter.getItem(position));
            }
        });

        progressView.stop();
    }

    /*
    ** Utils
     */
    private static class FriendAdapter extends ArrayAdapter<FriendsResponse.Friend> {

        private static int resource = R.layout.item_friend;
        private Context context;
        private ArrayList<FriendsResponse.Friend> friendList;

        public FriendAdapter(Context context, @NonNull ArrayList<FriendsResponse.Friend> friendList) {
            super(context, resource, friendList);
            this.context = context;
            this.friendList = friendList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            FriendHolder friendHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
                view = layoutInflater.inflate(resource, parent, false);

                friendHolder = new FriendHolder();
                friendHolder.friendPicture = (ImageView) view.findViewById(R.id.imageView_item_friend);
                friendHolder.friendName = (TextView) view.findViewById(R.id.textView_item_friend);

                view.setTag(friendHolder);
            } else {
                friendHolder = (FriendHolder) convertView.getTag();
                view = convertView;
            }

            //TODO add friend picture
            FriendsResponse.Friend currentFriend = friendList.get(position);
            friendHolder.friendName.setText(currentFriend.name);

            return view;
        }

        static class FriendHolder {
            ImageView friendPicture;
            TextView friendName;
        }
    }
}
