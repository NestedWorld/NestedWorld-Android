package com.nestedworld.nestedworld.ui.view.connected.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendDao;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendData;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserDetailResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserStatsResponse;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.adapter.fragmentStatePager.TabsAdapter;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDetailActivity extends BaseAppCompatActivity {

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
    @BindView(R.id.imageView_friend_background)
    ImageView imageViewFriendBackground;
    @BindView(R.id.textview_friendLevel)
    TextView textViewFriendLevel;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    /*
     * #############################################################################################
     * # Private field
     * #############################################################################################
     */
    private Friend mFriend;
    private FriendData mFriendData;

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
        //parse intent (should be called firstly)
        retrieveFriendFromIntent();

        //Setup view
        setupTabs();
        setUpToolbar();
        setupLocalFriendInfo();

        //send needed request
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

        //Get back the Toolbar as actionBar and then custom it
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //customise the actionBar
            actionBar.setTitle(R.string.friendDetail_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setupTabs() {
        //Setup adapter
        final TabsAdapter viewPagerAdapter = new TabsAdapter(getSupportFragmentManager())
                .setDisplayPageTitle(true)
                .addFragment(new FriendCatchFragment(), getString(R.string.friendDetail_title_catch))
                .addFragment(new FriendMonsterFragment(), getString(R.string.friendDetail_title_monster));

        viewPager.setAdapter(viewPagerAdapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);
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
                    public void onResponse(Call<UserDetailResponse> call,
                                           Response<UserDetailResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<UserDetailResponse> call,
                                          Throwable t) {

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

    private void setupLocalFriendInfo() {
        //Setup friend lvl/pseudo
        textViewFriendName.setText(mFriendData.pseudo);
        textViewFriendLevel.setText(String.format(getString(R.string.friendDetail_msg_level), mFriendData.level));

        //Setup friend avatar
        if (mFriendData.avatar != null) {
            try {
                Glide.with(this)
                        .load(mFriendData.avatar)
                        .placeholder(R.drawable.default_avatar_rounded)
                        .error(R.drawable.default_avatar_rounded)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(imageViewFriendPicture);
            } catch (OutOfMemoryError e) {
                //can not display picture, probably due to CropCircleTransaction()
            }
        }

        //Setup header background
        if (mFriendData.background != null) {
            try {
                Glide.with(this)
                        .load(mFriendData.background)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .centerCrop()
                        .into(imageViewFriendBackground);
            } catch (OutOfMemoryError e) {
                //can not display picture, probably due to CropCircleTransaction()
            }
        }
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
