package com.nestedworld.nestedworld.adapter.ArrayAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ChatAdapter extends ArrayAdapter<Friend> {

    private static final int resource = R.layout.item_chat_list;

    /*
    ** Constructor
     */
    public ChatAdapter(@NonNull final Context context) {
        super(context, resource);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        ChatHolder chatHolder;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            LayoutInflater layoutInflater = ((BaseAppCompatActivity) getContext()).getLayoutInflater();
            view = layoutInflater.inflate(resource, parent, false);

            chatHolder = new ChatHolder();
            chatHolder.chatPicture = (CircleImageView) view.findViewById(R.id.imageView_chat_picture);
            chatHolder.chatPictureOverlay = (CircleImageView) view.findViewById(R.id.imageView_chat_picture_overlay);
            chatHolder.chatName = (TextView) view.findViewById(R.id.textView_chat_name);

            view.setTag(chatHolder);
        } else {
            chatHolder = (ChatHolder) convertView.getTag();
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

        populateView(chatHolder, currentFriendInfo);

        return view;
    }

    /*
    ** Internal method
     */
    private void populateView(@NonNull final ChatHolder chatHolder, @NonNull final Player friend) {
        Context context = getContext();

        //display the friend name
        chatHolder.chatName.setText(friend.pseudo);

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
                .into(chatHolder.chatPicture);

        //set overlay and stroke
        if (friend.isConnected) {
            chatHolder.chatPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_color));
            chatHolder.chatPictureOverlay.setVisibility(View.VISIBLE);
        } else {
            chatHolder.chatPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_accent));
            chatHolder.chatPictureOverlay.setVisibility(View.GONE);
        }
    }
    /*
    ** Inner class
     */
    private static class ChatHolder {
        public CircleImageView chatPicture;
        public TextView chatName;
        public CircleImageView chatPictureOverlay;
    }
}