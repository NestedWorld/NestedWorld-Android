package com.nestedworld.nestedworld.ui.view.connected.fight.teamSelection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.database.entities.CombatDao;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.database.entities.UserMonster;
import com.nestedworld.nestedworld.data.database.entities.session.Session;
import com.nestedworld.nestedworld.data.database.entities.session.SessionData;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.data.network.socket.models.request.result.ResultRequest;
import com.nestedworld.nestedworld.data.network.socket.service.SocketService;
import com.nestedworld.nestedworld.events.socket.combat.OnCombatStartMessageEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.ui.adapter.pager.UserMonsterPagerAdapter;
import com.nestedworld.nestedworld.ui.customView.viewpager.ViewPagerWithIndicator;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;
import com.nestedworld.nestedworld.ui.view.connected.fight.battle.BattleFragment;
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
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeamSelectionFragment extends BaseFragment {

    private final List<UserMonster> mSelectedMonster = new ArrayList<>();
    private final UserMonsterPagerAdapter mAdapter = new UserMonsterPagerAdapter();
    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
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
    private int mMonsterCountRecommended;
    private int mMonsterRequire;
    private Combat mCombat = null;

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void load(@NonNull final FragmentManager fragmentManager,
                            @NonNull final Combat combat,
                            final int monsterCountRecommended,
                            final int monsterCountRequire) {
        //Instantiate new fragment
        final Fragment newFragment = new TeamSelectionFragment();

        //Add fragment param
        final Bundle args = new Bundle();
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
     * #############################################################################################
     * # EventBus
     * #############################################################################################
     */
    @Subscribe
    public void onNewCombatStart(OnCombatStartMessageEvent event) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        LogHelper.d(TAG, "onNewCombatStart");

        final StartMessage startMessage = event.getMessage();

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
     * #############################################################################################
     * # Widget callback binding
     * #############################################################################################
     */
    @OnClick(R.id.button_select_monster)
    public void onSelectMonsterClick() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Get the selected monster
        final UserMonster selectedUserMonster = mAdapter.getItemAtPosition(viewPager.getCurrentItem());
        if (selectedUserMonster != null) {
            final Monster selectedMonster = selectedUserMonster.getMonster();
            if (selectedMonster != null) {
                LogHelper.d(TAG, "UserMonster selected: " + selectedUserMonster.toString());
                //Add the monster in the list of selected monster
                mSelectedMonster.add(selectedUserMonster);

                //Update button 'selectMonster' color (can't select the same monster 2 time)
                updateArrowState();

                //Display monster sprite
                Glide.with(mContext)
                        .load(selectedMonster.baseSprite)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .placeholder(R.drawable.default_monster_rounded)
                        .error(R.drawable.default_monster_rounded)
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

        //Clear arrow state
        updateArrowState();
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
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

            if (mUserMonsters.size() < mMonsterRequire) {
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
     * #############################################################################################
     * # Private method
     * #############################################################################################
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
        final String combatId = getArguments().getString("combatId", "");
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
        final Session session = SessionHelper.getSession();
        if (session == null) {
            LogHelper.d(TAG, "No Session");
            onFatalError();//Should logout if we didn't have a session
            return;
        }

        //Retrieve the player
        final SessionData sessionData = session.getSessionData();
        if (sessionData == null) {
            LogHelper.d(TAG, "No User");
            ((BaseAppCompatActivity) mContext).finish();
            return;
        }

        //Display player avatar in header
        if (sessionData.avatar != null) {
            Glide.with(mContext)
                    .load(sessionData.avatar)
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

        final ActionBar actionBar = ((BaseAppCompatActivity) mContext).getSupportActionBar();
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
                final NestedWorldSocketAPI nestedWorldSocketAPI = ((SocketService.LocalBinder) service).getService().getApiInstance();
                if (nestedWorldSocketAPI != null) {
                    final ValueFactory.MapBuilder map = ValueFactory.newMapBuilder();

                    final List<Value> selectedMonsterIdList = new ArrayList<>();
                    for (UserMonster userMonster : mSelectedMonster) {
                        selectedMonsterIdList.add(ValueFactory.newInteger(userMonster.userMonsterId));
                    }

                    map.put(ValueFactory.newString("accept"), ValueFactory.newBoolean(true));
                    map.put(ValueFactory.newString("monsters"), ValueFactory.newArray(selectedMonsterIdList));

                    final ResultRequest resultRequest = new ResultRequest(map.build().map(), true);
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
