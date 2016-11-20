package com.nestedworld.nestedworld.ui.mainMenu.tabs.monster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.adapter.RecyclerView.MonsterAdapter;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.updater.MonsterUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.http.OnMonstersUpdatedEvent;
import com.nestedworld.nestedworld.ui.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonstersFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public final static String FRAGMENT_NAME = MonstersFragment.class.getSimpleName();
    private final MonsterAdapter mAdapter = new MonsterAdapter();
    @BindView(R.id.recycler_monsters_list)
    RecyclerView recyclerViewMonsters;
    @BindView(R.id.swipeRefreshLayout_monster_list)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.textview_no_monster)
    TextView textViewNoMonster;

    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new MonstersFragment())
                .addToBackStack(FRAGMENT_NAME)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_monsters;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //init Adapter and ListView listener
        setupListView();

        //Populate adapter with monster in BDD
        populateMonsterList();

        //Init swipe listener
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    /*
    ** SwipeRefreshLayout.OnRefreshListener Implementation
     */
    @Override
    public void onRefresh() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Start loading animation
        swipeRefreshLayout.setRefreshing(true);

        //Retrieve monster
        new MonsterUpdater().start(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Do not update adapter (done by eventBus)
                //Stop loading animation
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                @StringRes int errorRes;
                switch (errorKind) {
                    case NETWORK:
                        errorRes = R.string.error_network;
                        break;
                    case SERVER:
                        errorRes = R.string.error_unexpected;
                        break;
                    default:
                        errorRes = R.string.error_unexpected;
                        break;
                }

                //Display error message
                Toast.makeText(mContext, errorRes, Toast.LENGTH_LONG).show();

                //Stop loading animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /*
    ** EventBus
     */
    @Subscribe
    public void onMonsterUpdated(OnMonstersUpdatedEvent onMonstersUpdatedEvent) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateMonsterList();
            }
        });
    }

    /*
    ** Internal method
     */
    private void setupListView() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        recyclerViewMonsters.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerViewMonsters.setHasFixedSize(true);
        recyclerViewMonsters.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewMonsters.setAdapter(mAdapter);
    }

    @UiThread
    private void populateMonsterList() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        //Retrieve monsters from ORM
        List<Monster> monsters = Select.from(Monster.class).list();

        mAdapter.clear();
        if (monsters == null || monsters.isEmpty()) {
            textViewNoMonster.setVisibility(View.VISIBLE);
            recyclerViewMonsters.setVisibility(View.GONE);
        } else {
            textViewNoMonster.setVisibility(View.GONE);
            recyclerViewMonsters.setVisibility(View.VISIBLE);
            mAdapter.addAll(monsters);
        }
    }
}
