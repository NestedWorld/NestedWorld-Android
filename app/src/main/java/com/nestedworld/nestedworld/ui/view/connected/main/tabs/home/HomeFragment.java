package com.nestedworld.nestedworld.ui.view.connected.main.tabs.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.session.Session;
import com.nestedworld.nestedworld.data.database.entities.session.SessionData;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.events.http.OnUserUpdatedEvent;
import com.nestedworld.nestedworld.helpers.aws.AwsHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.adapter.fragmentStatePager.TabsAdapter;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BaseFragment {

    private final static int PICK_PROFIL_IMAGE_REQUEST = 1;
    private final static int PICK_BACKGROUND_IMAGE_REQUEST = 2;

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.textView_username)
    TextView textViewUsername;
    @BindView(R.id.textView_userLevel)
    TextView textViewUserLevel;
    @BindView(R.id.textView_creditsNumber)
    TextView textViewCreditsNumber;
    @BindView(R.id.textView_monsterCaptured)
    TextView textViewMonsterCaptured;
    @BindView(R.id.textView_areaCaptured)
    TextView textViewAreaCaptured;
    @BindView(R.id.textView_allyOnline)
    TextView textViewAllyOnline;
    @BindView(R.id.imageView_user)
    ImageView imageViewUser;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.imageView_user_background)
    ImageView imageViewUserBackground;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .addToBackStack(null)
                .commit();
    }

    /*
     * #############################################################################################
     * # EventBus
     * #############################################################################################
     */
    @Subscribe
    public void onUserUpdated(OnUserUpdatedEvent onUserUpdatedEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayUserInfo();
            }
        });
    }

    /*
     * #############################################################################################
     * # Butterknife callback
     * #############################################################################################
     */
    @OnClick(R.id.imageView_user)
    public void selectProfilPicture() {
        startImagePickerIntent(PICK_PROFIL_IMAGE_REQUEST);
    }

    @OnClick(R.id.imageView_user_background)
    public void selectBackgroundPicture() {
        startImagePickerIntent(PICK_BACKGROUND_IMAGE_REQUEST);
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setupTabs();
        displayUserInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if ((requestCode == PICK_BACKGROUND_IMAGE_REQUEST) || (requestCode == PICK_PROFIL_IMAGE_REQUEST)) {
                if (data != null && data.getData() != null) {
                    final Uri uri = data.getData();
                    handleImagePickerResult(requestCode, uri);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setupTabs() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Setup adapter
        final TabsAdapter viewPagerAdapter = new TabsAdapter(getChildFragmentManager())
                .setDisplayPageTitle(true)
                .addFragment(new HomeMonsterFragment(), getString(R.string.tabHome_title_monsterList))
                .addFragment(new HomeFriendFragment(), getString(R.string.tabHome_title_friendList));

        viewPager.setAdapter(viewPagerAdapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);
    }

    @UiThread
    private void displayUserInfo() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve the session
        final Session session = SessionHelper.getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the player
        final SessionData user = session.getSessionData();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            return;
        }

        //Display player information
        final Resources res = getResources();
        textViewUserLevel.setText(String.format(res.getString(R.string.tabHome_msg_userLvl), user.level));
        textViewAllyOnline.setText(String.format(res.getString(
                R.string.tabHome_msg_allyOnline),
                NestedWorldDatabase.getInstance()
                        .getDataBase()
                        .getFriendDao()
                        .loadAll()
                        .size()));
        textViewUsername.setText(user.pseudo);
        textViewMonsterCaptured.setText(String.format(res.getString(
                R.string.tabHome_msg_monsterCaptured),
                NestedWorldDatabase.getInstance()
                        .getDataBase()
                        .getUserMonsterDao()
                        .count()));

        //TODO display credits and areaCaptured
        textViewCreditsNumber.setText("0");
        textViewAreaCaptured.setText("0");

        //Display player picture
        if (user.avatar != null) {
            try {
                Glide.with(mContext)
                        .load(user.avatar)
                        .placeholder(R.drawable.default_avatar_rounded)
                        .error(R.drawable.default_avatar_rounded)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(imageViewUser);
            } catch (OutOfMemoryError e) {
                //can not display picture, probably due to CropCircleTransaction()
            }
        }

        //Display player background
        if (user.background != null) {
            try {
                Glide.with(mContext)
                        .load(user.background)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .centerCrop()
                        .into(imageViewUserBackground);
            } catch (OutOfMemoryError e) {
                //can no display picture, don't really know why...
            }
        }
    }

    private void startImagePickerIntent(final int requestCode) {
        final Intent intent = new Intent();

        //We only want image
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    private void handleImagePickerResult(final int requestCode,
                                         @NonNull final Uri uri) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            switch (requestCode) {
                case PICK_BACKGROUND_IMAGE_REQUEST:
                    AwsHelper.upload(mContext, new File(uri.getPath()));
                    imageViewUserBackground.setImageBitmap(bitmap);
                    break;
                case PICK_PROFIL_IMAGE_REQUEST:
                    AwsHelper.upload(mContext, new File(uri.getPath()));
                    imageViewUser.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}