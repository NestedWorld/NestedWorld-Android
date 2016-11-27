package com.nestedworld.nestedworld.ui.fight.teamSelection;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.RecyclerView.UserMonsterAdapter;
import com.nestedworld.nestedworld.adapter.pagerAdapter.UserMonsterPagerAdapter;
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
import butterknife.OnClick;
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
    ImageView buttonSelectMonster;
    @BindView(R.id.view_go_fight)
    View buttonGoFight;
    @BindView(R.id.imageView_user_background)
    ImageView imageViewUserPicture;
    @BindView(R.id.imageview_opponent_picture)
    ImageView imageViewOpponentPicture;
    @BindView(R.id.progressView)
    ProgressView progressView;
    @BindView(R.id.textView_state)
    TextView textViewState;

    private List<UserMonster> mUserMonsters;
    private final UserMonsterPagerAdapter mAdapter = new UserMonsterPagerAdapter();
    private int mMonsterCountRecommended;
    private int mMonsterRequire;
    private Combat mCombat = null;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager,
                            @NonNull final Combat combat,
                            final int monsterCountRecommended,
                            final int monsterCountRequire) {
        //Instantiate new fragment
        Fragment newFragment = new TeamSelectionFragment();

        //Add fragment param
        Bundle args = new Bundle();
        args.putInt("MONSTER_RECOMMENDED_KEY", monsterCountRecommended);
        args.putInt("MONSTER_REQUIRE_KEY", monsterCountRequire);
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

            if (mUserMonsters.size() < mMonsterCountRecommended) {
                Toast.makeText(mContext, "You don't have enough monster (" + mMonsterCountRecommended + "required)", Toast.LENGTH_LONG).show();
                ((BaseAppCompatActivity) mContext).finish();
            }

            //Init the viewPager (it will display player's monster)
            setUpViewPager();

            //init header block (with players information)
            setupHeader();

            //Init button textViewState (it display the number of selected monster)
            textViewState.setPaintFlags(textViewState.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textViewState.setText(String.format(getResources().getString(
                    R.string.teamSelection_msg_progress),
                    mMonsterCountRecommended,
                    mMonsterRequire));
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
    ** EventBus
     */
    @Subscribe
    public void onNewCombatStart(OnCombatStartMessageEvent event) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        LogHelper.d(TAG, "onNewCombatStart");

        StartMessage startMessage = event.getMessage();

        if (startMessage.id.equals(mCombat.combatId)) {
            LogHelper.d(TAG, "onNewCombatStart > accept");

            //Delete the combat from Orm
            NestedWorldDatabase
                    .getInstance()
                    .getDataBase()
                    .delete(mCombat);

            //Start fight fragment
            BattleFragment.load(getFragmentManager(), startMessage, mSelectedMonster);
        } else {
            LogHelper.d(TAG, "onNewCombatStart > refuse");
        }
    }

    /*
    ** Butterknife
     */
    @OnClick(R.id.button_select_monster)
    public void onSelectMonsterClick() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Get the selected monster
        UserMonster selectedUserMonster = mAdapter.getItemAtPosition(viewPager.getCurrentItem());
        if (selectedUserMonster != null) {
            Monster selectedMonster = selectedUserMonster.getMonster();
            if (selectedMonster != null) {
                LogHelper.d(TAG, "UserMonster selected: " + selectedUserMonster.toString());
                //Add the monster in the list of selected monster
                mSelectedMonster.add(selectedUserMonster);

                //Update button 'selectMonster' color (can't select the same monster 2 time)
                updateArrowState();

                //Display monster sprite
                Glide.with(mContext)
                        .load(selectedMonster.baseSprite)
                        .placeholder(R.drawable.default_monster)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .error(R.drawable.default_monster)
                        .into(selectedMonsterView.get(mSelectedMonster.size() - 1));
            }
        }
    }

    @OnClick(R.id.view_go_fight)
    public void onStartFightClick() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        if (mSelectedMonster.size() >= 1) {
            sendAcceptRequest();
        } else {
            Toast.makeText(mContext, "You should select at least 1 monster", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.imageview_help)
    public void onHelpClick() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Display a dialog with instruction
        SimpleDialogFragment
                .createBuilder(mContext, ((BaseAppCompatActivity) mContext).getSupportFragmentManager())
                .setTitle("Hint")
                .setMessage(R.string.teamSelection_msg_help)
                .show();
    }

    @OnClick(R.id.view_reset)
    public void onResetClick() {
        //Reset monster selector
        mAdapter.clear();
        mAdapter.addAll(mUserMonsters);

        //Clear select monster
        mSelectedMonster.clear();
        for (ImageView imageView : selectedMonsterView) {
            imageView.setImageResource(R.drawable.ic_clear_24dp);
        }
    }

    /*
    ** Private method
     */
    private boolean parseArgs() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return false;
        }

        //Retrieve recommended monster
        if (!getArguments().containsKey("MONSTER_RECOMMENDED_KEY")) {
            throw new IllegalArgumentException("Must provide needed monster count");
        }
        mMonsterCountRecommended = getArguments().getInt("MONSTER_RECOMMENDED_KEY", -1);

        //Retrieve require monster
        if (!getArguments().containsKey("MONSTER_REQUIRE_KEY")) {
            throw new IllegalArgumentException("Must provide needed monster count");
        }
        mMonsterRequire = getArguments().getInt("MONSTER_REQUIRE_KEY", -1);

        //Retrieve combat
        if (!getArguments().containsKey("combatId")) {
            throw new IllegalArgumentException("Must provide wanted combat");
        }
        String combatId = getArguments().getString("combatId", "");
        mCombat = NestedWorldDatabase
                .getInstance()
                .getDataBase()
                .getCombatDao()
                .queryBuilder()
                .where(CombatDao.Properties.CombatId.eq(combatId))
                .unique();

        if ((mMonsterCountRecommended < 0) || (mMonsterRequire < 0) || mCombat == null) {
            LogHelper.d(TAG, "mCombat = null || mMonsterCountRecommended < 0");
            return false;
        } else {
            LogHelper.d(TAG, "Combat=" + mCombat.toString() + "\nmonsterNeeded=" + mMonsterCountRecommended);
            return true;
        }
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
            onFatalError();//Should logout if we didn't have a session
            return;
        }

        //Retrieve the player
        Player user = session.getPlayer();
        if (user == null) {
            LogHelper.d(TAG, "No User");
            ((BaseAppCompatActivity) mContext).finish();
            return;
        }

        //Display player avatar in header
        if (user.avatar != null) {
            Glide.with(mContext)
                    .load(user.avatar)
                    .placeholder(R.drawable.default_avatar_rounded)
                    .error(R.drawable.default_avatar_rounded)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(imageViewUserPicture);
        }

        //TODO display opponent avatar
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

    private void setUpViewPager() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mAdapter.addAll(mUserMonsters);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateArrowState();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(mAdapter);
        viewPagerArrowIndicator.setViewPager(viewPager);
    }

    private void updateArrowState() {
        if (mSelectedMonster.contains(mUserMonsters.get(viewPager.getCurrentItem()))) {
            buttonSelectMonster.setImageResource(R.drawable.ic_expand_more_red_24dp);
            buttonSelectMonster.setClickable(false);
        } else {
            buttonSelectMonster.setImageResource(R.drawable.ic_expand_more_accent_24dp);
            buttonSelectMonster.setClickable(true);
        }
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
                    nestedWorldSocketAPI.sendRequest(resultRequest, SocketMessageType.MessageKind.TYPE_RESULT, mCombat.combatId);

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
}
