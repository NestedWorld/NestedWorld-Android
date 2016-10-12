package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.updater.FriendsUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.socket.combat.OnAskMessageEvent;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;
import com.nestedworld.nestedworld.network.socket.models.request.combat.AskRequest;
import com.nestedworld.nestedworld.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Response;

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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupListView();
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
    public void onAskMessage(OnAskMessageEvent messageEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve message
        AskMessage askMessage = messageEvent.getMessage();

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

    /*
    ** Private method
     */
    private void setupListView() {
        //Check if framgnent hasn't been detach
        if (mContext == null) {
            return;
        }

        //init adapter for our listView
        mAdapter = new FriendsAdapter(mContext);
        listView.setAdapter(mAdapter);
    }

    private void updateFriendList() {
        //Check if framgnent hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        progressView.start();

        new FriendsUpdater(mContext).update(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                populateFriendList();

                //Stop loading animation
                progressView.stop();
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //Stop loading animation
                progressView.stop();
            }
        });
    }

    private void populateFriendList() {
        //Retrieve entity from orm
        List<Friend> friends = Select.from(Friend.class).list();

        //Remove old entitu and add new one
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
                //Start loading animation
                progressView.start();

                //Retrieve input
                String pseudo = editTextPseudo.getText().toString();

                //Send request
                NestedWorldHttpApi.getInstance(mContext).addFriend(pseudo).enqueue(new Callback<AddFriendResponse>() {
                    @Override
                    public void onSuccess(Response<AddFriendResponse> response) {
                        //Update Orm and adapter
                        updateFriendList();

                        //Display message
                        Toast.makeText(mContext, R.string.tab_home_msg_addFriendSuccess, Toast.LENGTH_LONG).show();

                        //Stop loading animation
                        progressView.stop();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<AddFriendResponse> response) {
                        //Initialise a default error message
                        String errorMessage = getString(R.string.tab_home_msg_addFriendFailed);

                        //Try to retrieve response.body.message
                        String serverError = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, response);

                        //If we have a serverError we complete our default error message
                        if (serverError != null) {
                            errorMessage += " : " + serverError;
                        }

                        //Display the message
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();

                        //Stop loading animation
                        progressView.stop();
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
    private static class FriendsAdapter extends ArrayAdapter<Friend> {

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

            View view;
            FriendHolder friendHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
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
            if (currentFriend == null) {
                return view;
            }

            final User currentFriendInfo = currentFriend.info();
            if (currentFriendInfo == null) {
                return view;
            }

            populateView(friendHolder, currentFriendInfo);

            return view;
        }

        /*
        ** Internal method
         */
        private void populateView(@NonNull final FriendHolder friendHolder, @NonNull final User currentFriendInfo) {
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

            //set listener on defy button
            friendHolder.buttonDefy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDefyFriendClick(currentFriendInfo);
                }
            });
        }

        private void onDefyFriendClick(@NonNull final User currentFriendInfo) {
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
                    Toast.makeText(getContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
                }
            });
        }

        /*
        ** Inner class
         */
        private static class FriendHolder {
            public ImageView friendPicture;
            public TextView friendName;
            public Button buttonDefy;
        }
    }
}
