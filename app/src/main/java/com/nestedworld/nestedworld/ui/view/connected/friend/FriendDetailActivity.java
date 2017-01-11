package com.nestedworld.nestedworld.ui.view.connected.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendDao;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendData;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserDetailResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserStatsResponse;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDetailActivity extends BaseAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView_friend_picture)
    ImageView imageViewFriendPicture;
    @BindView(R.id.textview_friendName)
    TextView textViewFriendName;
    @BindView(R.id.swipeRefreshLayout_friend_detail)
    SwipeRefreshLayout swipeRefreshLayout;

    /*
     * #############################################################################################
     * # Private field
     * #############################################################################################
     */
    private Friend mFriend;
    private FriendData mFriendData;

    /*
     * #############################################################################################
     * # SwipeRefreshLayout.OnRefreshListener implementation
     * #############################################################################################
     */
    @Override
    public void onRefresh() {
        retrieveFriendDetail();
        retrieveFriendStat();
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_friend_detail;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        setUpToolbar();
        retrieveFriendFromIntent();
        setupSwipeRefreshLayout();
        retrieveFriendDetail();
        retrieveFriendStat();
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void retrieveFriendFromIntent() {
        //Check if we have an intent
        final Intent intent = getIntent();
        if (intent == null) {
            LogHelper.e(TAG, "Couldn't launch activity without intent");
            finish();
            return;
        }

        //Check if intent contains the required friendId
        final Serializable friendId = Builder.getFieldValue(Builder.FRIEND_ID_KEY, intent);
        if (friendId == null) {
            LogHelper.e(TAG, "Couldn't launch activity without friendId");
            finish();
            return;
        }

        //Check the given friendId is valid
        final Friend friend = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getFriendDao()
                .queryBuilder()
                .where(FriendDao.Properties.Id.eq(friendId))
                .unique();
        if (friend == null) {
            LogHelper.e(TAG, "Couldn't launch activity without friend");
            finish();
            return;
        }

        //Check if the retrieved friend is valid (IE has friendData)
        final FriendData friendData = friend.getData();
        if (friendData == null) {
            LogHelper.e(TAG, "Couldn't launch activity without friendData");
            finish();
            return;
        }

        mFriend = friend;
        mFriendData = friendData;
    }

    private void retrieveFriendDetail() {
        NestedWorldHttpApi.getInstance()
                .getUserDetails(mFriendData.playerId)
                .enqueue(new Callback<UserDetailResponse>() {
                    @Override
                    public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<UserDetailResponse> call, Throwable t) {

                    }
                });
    }

    private void retrieveFriendStat() {
        NestedWorldHttpApi.getInstance()
                .getUserStats(mFriendData.playerId)
                .enqueue(new Callback<UserStatsResponse>() {
                    @Override
                    public void onResponse(Call<UserStatsResponse> call, Response<UserStatsResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<UserStatsResponse> call, Throwable t) {

                    }
                });
    }

    /*
     * #############################################################################################
     * # Builder
     * #############################################################################################
     */
    public final static class Builder {

        /*
         * #############################################################################################
         * # Public field
         * #############################################################################################
         */
        public final static String FRIEND_ID_KEY = "FriendDetailActivity.FRIEND_ID_KEY";
        /*
         * #############################################################################################
         * # Private field
         * #############################################################################################
         */
        private final Intent mIntent;

        /*
         * #############################################################################################
         * # Constructor
         * #############################################################################################
         */
        public Builder(@NonNull final Activity activity) {
            mIntent = new Intent(activity, FriendDetailActivity.class);
        }

        /*
         * #############################################################################################
         * # Public (static) method
         * #############################################################################################
         */
        @Nullable
        public static Serializable getFieldValue(@Nullable @Field final String key,
                                                 @NonNull final Intent intent) {
            return intent.getSerializableExtra(key);
        }

        /*
         * #############################################################################################
         * # Public method
         * #############################################################################################
         */
        public Builder setFieldValue(@Nullable @Field final String key,
                                     @NonNull final Serializable value) {
            mIntent.putExtra(key, value);
            return this;
        }

        public Intent build() {
            return mIntent;
        }

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({FRIEND_ID_KEY})
        @interface Field {
        }
    }
}
