package com.nestedworld.nestedworld.ui.fight.teamSelection;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.customView.viewpager.ViewPagerWithIndicator;
import com.nestedworld.nestedworld.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.database.models.CombatDao;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatStartMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.network.socket.models.request.result.ResultRequest;
import com.nestedworld.nestedworld.network.socket.service.SocketService;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.fight.battle.BattleFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import de.hdodenhof.circleimageview.CircleImageView;
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
    @BindView(R.id.imageView_user_background)
    ImageView imageViewUserPicture;
    @BindView(R.id.imageview_opponent_picture)
    ImageView imageViewOpponentPicture;
    @BindView(R.id.progressView)
    ProgressView progressView;
    private List<UserMonster> mUserMonsters;
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //DO What you want
        }

        @Override
        public void onPageSelected(int position) {
            //Check if fragment hasn't been detach
            if (mContext == null) {
                return;
            }

            updateArrowState();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //DO What you want
        }
    };
    private int mNeededMonster;
    private Combat mCurrentCombat;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager, @NonNull final Combat combat, final int monsterNeeded) {
        //Instantiate new fragment
        Fragment newFragment = new TeamSelectionFragment();

        //Add fragment param
        Bundle args = new Bundle();
        args.putInt("MONSTER_NEEDED_KEY", monsterNeeded);
        args.putString("combatId", combat.getCombatId());
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

        setHasOptionsMenu(true);//Force toolbar redraw

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (!parseArgs()) {
            //Cannot get selected Combat, display error and finish current activity
            Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
            ((BaseAppCompatActivity) mContext).finish();
        } else {
            //Change action bar title
            setupActionBar();

            //Retrieve userMonster
            mUserMonsters = NestedWorldDatabase.getInstance()
                    .getDataBase()
                    .getUserMonsterDao()
                    .loadAll();

            if (mUserMonsters.size() < mNeededMonster) {
                Toast.makeText(mContext, "You don't have enough monster (" + mNeededMonster + "required)", Toast.LENGTH_LONG).show();
                ((BaseAppCompatActivity) mContext).finish();
            }

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_team_selection, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                handleHelpClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    /*
    ** Eventbus
     */
    @Subscribe
    public void onNewCombatStart(OnCombatStartMessageEvent event) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        LogHelper.d(TAG, "onNewCombatStart");

        StartMessage startMessage = event.getMessage();

        if (startMessage.id.equals(mCurrentCombat.combatId)) {
            LogHelper.d(TAG, "onNewCombatStart > accept");

            //Delete the combat from Orm
            NestedWorldDatabase
                    .getInstance()
                    .getDataBase()
                    .delete(mCurrentCombat);

            //Start fight fragment
            BattleFragment.load(getFragmentManager(), startMessage, mSelectedMonster);
        } else {
            LogHelper.d(TAG, "onNewCombatStart > refuse");
        }
    }

    /*
    ** Private method
     */
    private void parseArgsNeededMonster() {
        //Retrieve needed monster
        if (!getArguments().containsKey("MONSTER_NEEDED_KEY")) {
            throw new IllegalArgumentException("Must provide needed monster count");
        }
        mNeededMonster = getArguments().getInt("MONSTER_NEEDED_KEY", -1);
    }

    private void parseArgsWantedCombat() {
        if (!getArguments().containsKey("combatId")) {
            throw new IllegalArgumentException("Must provide wanted combat");
        }
        String combatId = getArguments().getString("combatId", "");
        mCurrentCombat = NestedWorldDatabase
                .getInstance()
                .getDataBase()
                .getCombatDao()
                .queryBuilder()
                .where(CombatDao.Properties.CombatId.eq(combatId))
                .unique();
    }

    private boolean parseArgs() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return false;
        }

        parseArgsNeededMonster();
        parseArgsWantedCombat();

        if ((mNeededMonster < 0) || mCurrentCombat == null) {
            LogHelper.d(TAG, "mCurrentCombat = null || mNeededMonster < 0");
            return false;
        } else {
            LogHelper.d(TAG, "Combat=" + mCurrentCombat.toString() + "\nmonsterNeeded=" + mNeededMonster);
            return true;
        }
    }

    private void handleHelpClick() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        SimpleDialogFragment
                .createBuilder(mContext, ((BaseAppCompatActivity) mContext).getSupportFragmentManager())
                .setMessage(R.string.teamSelection_msg_help)
                .show();
    }

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
        Player user = session.getPlayer();
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
        Monster selectedMonserInfo = selectedMonster.getMonster();

        //Display some log
        LogHelper.d(TAG, "Monster selected: " + selectedMonster.toString());

        //Show the selected monster
        if (selectedMonserInfo != null) {
            Glide.with(mContext)
                    .load(selectedMonserInfo.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .error(R.drawable.default_monster)
                    .into(selectedMonsterView.get(mSelectedMonster.size()));
        }

        //Add the monster in the list (of selected monster)
        mSelectedMonster.add(selectedMonster);

        //Update 'start_fight' button with the current state
        button_go_fight.setText(String.format(getResources().getString(R.string.teamSelection_msg_progress), mSelectedMonster.size(), mNeededMonster));

        //If we've selected enough monster, we enable the button
        if (mSelectedMonster.size() == mNeededMonster) {
            onEnoughMonsterSelected();
        }
    }

    private void onEnoughMonsterSelected() {
        //Change text on the button
        button_go_fight.setText(getResources().getString(R.string.teamSelection_msg_startFight));

        //Set a listener for starting the fight
        button_go_fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAcceptRequest();
            }
        });

        //Disable monster selection
        button_select_monster.setOnClickListener(null);
    }

    private void sendAcceptRequest() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        progressView.start();

        ServiceHelper.bindToSocketService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NestedWorldSocketAPI nestedWorldSocketAPI = ((SocketService.LocalBinder) service).getService().getApiInstance();
                if (nestedWorldSocketAPI != null) {
                    ValueFactory.MapBuilder map = ValueFactory.newMapBuilder();

                    List<Value> selectedMonsterIdList = new ArrayList<>();
                    for (UserMonster userMonster : mSelectedMonster) {
                        selectedMonsterIdList.add(ValueFactory.newInteger(userMonster.userMonsterId));
                    }

                    map.put(ValueFactory.newString("accept"), ValueFactory.newBoolean(true));
                    map.put(ValueFactory.newString("monsters"), ValueFactory.newArray(selectedMonsterIdList));

                    ResultRequest resultRequest = new ResultRequest(map.build().map(), true);
                    nestedWorldSocketAPI.sendRequest(resultRequest, SocketMessageType.MessageKind.TYPE_RESULT, mCurrentCombat.combatId);

                } else {
                    onServiceDisconnected(null);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (mContext != null) {
                    //Stop loading animation
                    progressView.stop();

                    //Display error message
                    Toast.makeText(mContext, R.string.error_socket_disconnected, Toast.LENGTH_LONG).show();

                    //Finish the current activity
                    ((BaseAppCompatActivity) mContext).finish();
                }
            }
        });
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
            UserMonster userMonster = mUserMonsters.get(position);

            if (userMonster == null) {
                return null;
            }

            Monster monster = userMonster.getMonster();
            if (monster == null) {
                return null;
            }

            //Populate monster information
            Resources res = mContext.getResources();
            ((TextView) view.findViewById(R.id.textview_monster_name)).setText(monster.name);
            ((TextView) view.findViewById(R.id.textview_monster_lvl)).setText(String.format(res.getString(
                    R.string.integer),
                    userMonster.level));
            ((TextView) view.findViewById(R.id.textview_monster_hp)).setText(String.format(res.getString(
                    R.string.teamSelection_msg_monsterHp),
                    (int) monster.hp));
            ((TextView) view.findViewById(R.id.textview_monster_attack)).setText(String.format(res.getString(
                    R.string.teamSelection_msg_monsterAttack),
                    (int) monster.attack));
            ((TextView) view.findViewById(R.id.textview_monster_defense)).setText(String.format(res.getString(
                    R.string.teamSelection_msg_monsterDefence),
                    (int) monster.defense));

            //Display monster picture
            CircleImageView imageViewMonster = (CircleImageView) view.findViewById(R.id.imageView_monster);
            Glide.with(mContext)
                    .load(monster.baseSprite)
                    .placeholder(R.drawable.default_monster)
                    .centerCrop()
                    .into(imageViewMonster);

            //Add color shape around monster picture
            imageViewMonster.setBorderColor(ContextCompat.getColor(mContext, monster.getElementColorResource()));

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
            return mUserMonsters.get(position).getMonster().name;
        }
    }
}
