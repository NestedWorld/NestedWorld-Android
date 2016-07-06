package com.nestedworld.nestedworld.fragments.mainMenu.tabs.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.request.combat.AskRequest;
import com.orm.query.Select;

import org.msgpack.value.Value;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                String pseudo = editTextPseudo.getText().toString();
                NestedWorldHttpApi.getInstance(mContext).addFriend(pseudo).enqueue(new Callback<AddFriendResponse>() {
                    @Override
                    public void onResponse(Call<AddFriendResponse> call, Response<AddFriendResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<AddFriendResponse> call, Throwable t) {

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

    /**
     * * Custom adapter for displaying friend on the listView
     **/

    private class FriendsAdapter extends ArrayAdapter<Friend> {

        private static final int resource = R.layout.item_friend_list;
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
                friendHolder.buttonDefy = (Button) view.findViewById(R.id.button_defy_friend);

                view.setTag(friendHolder);
            } else {
                friendHolder = (FriendHolder) convertView.getTag();
                view = convertView;
            }

            //get the currentFriend
            Friend currentFriend = getItem(position);
            final User currentFriendInfo = currentFriend.info();

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

            //set listener on defy button
            friendHolder.buttonDefy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NestedWorldSocketAPI.getInstance(new ConnectionListener() {
                        @Override
                        public void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI) {
                            nestedWorldSocketAPI.sendRequest(new AskRequest(currentFriendInfo.pseudo), SocketMessageType.MessageKind.TYPE_COMBAT_ASK);
                        }

                        @Override
                        public void onConnectionLost() {

                        }

                        @Override
                        public void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content) {
                            if (kind == SocketMessageType.MessageKind.TYPE_COMBAT_ASK) {
                                LogHelper.d(TAG, "Got response");
                                //TODO parse message
                            }
                        }
                    });
                }
            });

            return view;
        }

        private class FriendHolder {
            public ImageView friendPicture;
            public TextView friendName;
            public Button buttonDefy;
        }
    }
}
