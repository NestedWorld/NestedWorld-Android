package com.nestedworld.nestedworld.ui.fight.teamSelection;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.customView.viewpager.ViewPagerWithIndicator;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeamSelectionFragment extends BaseFragment {

    private final List<UserMonster> mSelectedMonster = new ArrayList<>();
    @BindViews({
            R.id.imageview_selectedmonster_1,
            R.id.imageview_selectedmonster_2,
            R.id.imageview_selectedmonster_3,
            R.id.imageview_selectedmonster_4})
    List<ImageView> selectedMonsterView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.ViewPagerArrowIndicator)
    ViewPagerWithIndicator viewPagerArrowIndicator;
    @BindView(R.id.button_select_monster)
    Button button_select_monster;
    @BindView(R.id.button_go_fight)
    Button button_go_fight;
    @BindView(R.id.imageView_user_picture)
    ImageView imageViewUserPicture;
    @BindView(R.id.imageview_opponent_picture)
    ImageView imageViewOpponentPicture;
    private List<UserMonster> mUserMonsters;
    private int mNeededMonster;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //DO What you want
        }

        @Override
        public void onPageSelected(int position) {
            updateArrowState();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //DO What you want
        }
    };

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, final int monsterNeeded) {
        //Instantiate new fragment
        Fragment newFragment = new TeamSelectionFragment();

        //Add fragment param
        Bundle args = new Bundle();
        args.putLong("MONSTER_NEEDED_KEY", monsterNeeded);
        newFragment.setArguments(args);

        //Display the fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_team_selection;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Check if fragment has been attach
        if (mContext == null) {
            return;
        }

        //Change action bar title
        setupActionBar();

        //Retrieve args
        if (!getArguments().containsKey("MONSTER_NEEDED_KEY")) {
            throw new IllegalArgumentException("Must provide needed monster count");
        }
        mNeededMonster = getArguments().getInt("mNeededMonster");

        //Retrieve userMonster
        mUserMonsters = Select.from(UserMonster.class).list();

        //Init the viewPager (it will display player's monster)
        setUpViewPager();

        //init header block (with players information)
        setupHeader();

        //Init button 'start_fight' text (it will display the number of selected monster)
        button_go_fight.setText(String.format(getResources().getString(R.string.teamSelection_msg_progress), 0, mNeededMonster));

        //Init button 'select_monster'
        button_select_monster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMonsterSelected();
                updateArrowState();//Update the 'select monster' button
            }
        });
    }

    /*
    ** Private method
     */
    private void setupHeader() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve the session
        Session session = SessionHelper.getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();
            return;
        }

        //Retrieve the player
        Player user = session.getUser();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            ((BaseAppCompatActivity) mContext).finish();
            return;
        }

        if (user.avatar != null) {
            Glide.with(mContext)
                    .load(user.avatar)
                    .placeholder(R.drawable.default_avatar_rounded)
                    .error(R.drawable.default_avatar_rounded)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(imageViewUserPicture);
        }

        //TODO display real opponent picture
        Glide.with(mContext)
                .load(R.drawable.default_avatar_rounded)
                .placeholder(R.drawable.default_avatar_rounded)
                .error(R.drawable.default_avatar_rounded)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(imageViewOpponentPicture);
    }

    private void setUpViewPager() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Set up viewPager
        UserMonsterPagerAdapter mUserMonsterPagerAdapter = new UserMonsterPagerAdapter(mContext, mUserMonsters);
        viewPager.setAdapter(mUserMonsterPagerAdapter);
        viewPagerArrowIndicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void setupActionBar() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.teamSelection_title));
        }
    }

    private void onMonsterSelected() {
        //Get the selected monster
        UserMonster selectedMonster = mUserMonsters.get(viewPager.getCurrentItem());
        Monster selectedMonserInfo = selectedMonster.info();

        //Display some log
        LogHelper.d(TAG, "Monster selected: " + selectedMonster.toString());

        //Show the selected monster
        if (selectedMonserInfo != null) {
            Glide.with(mContext)
                    .load(selectedMonserInfo.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .error(R.drawable.default_monster)
                    .into(selectedMonsterView.get(mSelectedMonster.size()));
        }

        //Add the monster in the list (of selected monster)
        mSelectedMonster.add(selectedMonster);

        //Update 'start_fight' button with the current state
        button_go_fight.setText(String.format(getResources().getString(R.string.teamSelection_msg_progress), mSelectedMonster.size(), mNeededMonster));

        //If we've selected enough monster, we enable the button
        if (mSelectedMonster.size() == 4) {
            onEnoughMonsterSelected();
        }
    }

    private void onEnoughMonsterSelected() {
        //Change text on the button
        button_go_fight.setText(getResources().getString(R.string.teamSelection_msg_startFight));

        //Disable monster selection
        button_select_monster.setOnClickListener(null);
    }

    private void updateArrowState() {
        if (mSelectedMonster.contains(mUserMonsters.get(viewPager.getCurrentItem()))) {
            button_select_monster.setBackgroundResource(R.drawable.ic_arrow_downward_red_24dp);
            button_select_monster.setClickable(false);
        } else {
            button_select_monster.setBackgroundResource(R.drawable.ic_arrow_downward_accent_24dp);
            button_select_monster.setClickable(true);
        }
    }

    /**
     * Simple pager Adapter for displaying our monster
     */
    private static class UserMonsterPagerAdapter extends PagerAdapter {

        private final List<UserMonster> mUserMonsters;
        private final Context mContext;

        public UserMonsterPagerAdapter(@NonNull Context context, @NonNull List<UserMonster> userMonsters) {
            mUserMonsters = userMonsters;
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            //Create the view
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_monster_selector, container, false);

            //Retrieve the monster we'll display
            UserMonster monster = mUserMonsters.get(position);
            Monster monsterInfo = monster.info();

            if (monsterInfo == null) {
                return null;
            }

            //Populate monster information
            Resources res = mContext.getResources();
            ((TextView) view.findViewById(R.id.textview_monster_name)).setText(String.format(res.getString(R.string.teamSelection_msg_monsterName), monsterInfo.name));
            ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(res.getString(R.string.teamSelection_msg_monsterLvl), monster.level));
            ((TextView) view.findViewById(R.id.textview_monster_hp)).setText(String.format(res.getString(R.string.teamSelection_msg_monsterHp), monsterInfo.hp));
            ((TextView) view.findViewById(R.id.textview_monster_attack)).setText(String.format(res.getString(R.string.teamSelection_msg_monsterAttack), monsterInfo.attack));
            ((TextView) view.findViewById(R.id.textview_monster_defense)).setText(String.format(res.getString(R.string.teamSelection_msg_monsterDefence), monsterInfo.defense));

            //Display monster picture
            ImageView imageViewMonster = (ImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(mContext)
                    .load(monsterInfo.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .centerCrop()
                    .into(imageViewMonster);

            //Add color shape around monster picture
            view.findViewById(R.id.user_monster_shape).setBackgroundColor(ContextCompat.getColor(mContext, monster.getColorResource()));

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mUserMonsters.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            UserMonster monster = mUserMonsters.get(position);
            Monster monsterInfo = monster.info();
            if (monsterInfo == null) {
                return "";
            }
            return monsterInfo.name;
        }
    }
}