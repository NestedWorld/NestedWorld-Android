package com.nestedworld.nestedworld.ui.mainMenu.tabs.home;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = HomeFragment.class.getSimpleName();

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
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new HomeFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        initTabs();
        populateUserInfo();
    }

    /*
    ** Private method
     */
    private void initTabs() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        final TabsAdapter adapter = new TabsAdapter(getChildFragmentManager());
        adapter.addFragment(getString(R.string.tabHome_title_monsterList), new HomeMonsterFragment());
        adapter.addFragment(getString(R.string.tabHome_title_friendList), new HomeFriendFragment());

        viewPager.setAdapter(adapter);

        //Add view pager to the tabLayout
        tabLayout.setupWithViewPager(viewPager);
    }


    private void populateUserInfo() {
        //Retrieve the session
        Session session = SessionHelper.getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the user
        User user = session.getUser();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            onFatalError();
            return;
        }

        //Display user information
        Resources res = getResources();
        textViewUserLevel.setText(String.format(res.getString(R.string.tabHome_msg_userLvl), user.level));
        textViewAllyOnline.setText(String.format(res.getString(R.string.tabHome_msg_allyOnline), Friend.getNumberOfAllyOnline()));
        textViewUsername.setText(user.pseudo);
        textViewMonsterCaptured.setText(String.format(res.getString(R.string.tabHome_msg_monsterCaptured), Select.from(UserMonster.class).list().size()));

        //TODO display credits and areaCaptured
        textViewCreditsNumber.setText("0");
        textViewAreaCaptured.setText("0");

        //Make placeHolder rounded
        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

        //Display user picture
        Glide.with(mContext)
                .load(user.avatar)
                .placeholder(roundedBitmapDrawable)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(imageViewUser);

        //Display user background
        Glide.with(mContext)
                .load(user.background)
                .placeholder(R.color.apptheme_color)
                .into(imageViewUserBackground);
    }

    /**
     * Custom FragmentPagerAdapter
     * It's use for displaying the TABS
     */
    private static class TabsAdapter extends FragmentPagerAdapter {
        protected final String TAG = getClass().getSimpleName();

        private final List<CustomTab> tabList = new ArrayList<>();

        /*
        ** Constructor
         */
        public TabsAdapter(@NonNull final FragmentManager fm) {
            super(fm);
        }

        /*
        ** Public method
         */
        public void addFragment(@NonNull final String title, @NonNull final Fragment fragment) {
            tabList.add(new CustomTab(title, fragment));
        }

        /*
        ** Parents method
         */
        @Override
        public Fragment getItem(int position) {
            return tabList.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabList.get(position).getTitle();
        }

        /**
         * Custom class for easy tab management
         */
        private static class CustomTab {
            private final Fragment mFragment;
            private String mTitle = "";

            public CustomTab(@NonNull final String title, @NonNull final Fragment fragment) {
                mTitle = title;
                mFragment = fragment;
            }

            public Fragment getFragment() {
                return mFragment;
            }

            public String getTitle() {
                return mTitle;
            }
        }
    }
}