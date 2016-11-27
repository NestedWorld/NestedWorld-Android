package com.nestedworld.nestedworld.adapter.ArrayAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.request.combat.AskRequest;
import com.nestedworld.nestedworld.network.socket.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * * Custom adapter for displaying friend on the listView
 **/
public class FriendsAdapter extends ArrayAdapter<Friend> {

    private static final int resource = R.layout.item_friend_list;

    /*
    ** Constructor
     */
    public FriendsAdapter(@NonNull final Context context) {
        super(context, resource);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        FriendHolder friendHolder;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            LayoutInflater layoutInflater = ((BaseAppCompatActivity) getContext()).getLayoutInflater();
            view = layoutInflater.inflate(resource, parent, false);

            friendHolder = new FriendHolder();
            friendHolder.friendPicture = (CircleImageView) view.findViewById(R.id.imageView_item_friend);
            friendHolder.friendPictureOverlay = (CircleImageView) view.findViewById(R.id.imageView_item_friend_overlay);
            friendHolder.friendName = (TextView) view.findViewById(R.id.textView_item_friend);
            friendHolder.buttonDefy = (Button) view.findViewById(R.id.button_defy_friend);

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

        //get current friend information
        Player currentFriendInfo = currentFriend.getPlayer();
        if (currentFriendInfo == null) {
            return view;
        }

        populateView(friendHolder, currentFriendInfo);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final FriendHolder friendHolder, @NonNull final Player friend) {
        Context context = getContext();

        //display the friend name
        friendHolder.friendName.setText(friend.pseudo);

        //display a rounded placeHolder for friend's avatar
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

        //display friend's avatar
        Glide.with(context)
                .load(friend.avatar)
                .placeholder(roundedBitmapDrawable)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(friendHolder.friendPicture);

        //set overlay and stroke
        if (friend.isConnected) {
            friendHolder.friendPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_color));
            friendHolder.friendPictureOverlay.setVisibility(View.VISIBLE);
        } else {
            friendHolder.friendPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_accent));
            friendHolder.friendPictureOverlay.setVisibility(View.GONE);
        }

        //set listener on defy button
        friendHolder.buttonDefy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDefyFriendClick(friend);
            }
        });
    }

    private void onDefyFriendClick(@NonNull final Player currentFriendInfo) {
        //Retrieve SocketService for using NestedworldSocketAPI
        ServiceHelper.bindToSocketService(getContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Retrieve NestedworldSocketApi from service instance
                SocketService socketService = ((SocketService.LocalBinder) service).getService();
                NestedWorldSocketAPI nestedWorldSocketAPI = socketService.getApiInstance();

                if (nestedWorldSocketAPI != null) {
                    //Send request and display message
                    nestedWorldSocketAPI.sendRequest(new AskRequest(currentFriendInfo.pseudo), SocketMessageType.MessageKind.TYPE_COMBAT_ASK);
                    Toast.makeText(getContext(), R.string.tabHome_msg_requestFightSend, Toast.LENGTH_LONG).show();
                } else {
                    onServiceDisconnected(null);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //Display an error message
                Toast.makeText(getContext(), R.string.error_socket_disconnected, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    ** Inner class
     */
    private static class FriendHolder {
        public CircleImageView friendPicture;
        public TextView friendName;
        public Button buttonDefy;
        public CircleImageView friendPictureOverlay;
    }
}