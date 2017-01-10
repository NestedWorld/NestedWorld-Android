package com.nestedworld.nestedworld.ui.view.main.tabs.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.ShopItem;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.database.updater.ShopItemsUpdater;
import com.nestedworld.nestedworld.data.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.http.OnShopItemsUpdated;
import com.nestedworld.nestedworld.ui.adapter.array.ShopItemAdapter;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;
import com.nestedworld.nestedworld.ui.view.base.BaseFragment;
import com.nestedworld.nestedworld.ui.view.main.tabs.home.HomeFragment;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShopFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ShopItemAdapter mAdapter;

    /*
     * #############################################################################################
     * # Butterknife widget binding
     * #############################################################################################
     */
    @BindView(R.id.textview_inventory_empty)
    TextView textViewShopEmpty;
    @BindView(R.id.swipeRefreshLayout_shop)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listView_shop)
    ListView listViewShop;
    @BindView(R.id.progressView)
    ProgressView progressView;

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
    public void onShopItemUpdated(OnShopItemsUpdated onShopItemsUpdated) {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ((BaseAppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateAdapter();
            }
        });
    }

    /*
     * #############################################################################################
     * # Life cycle
     * #############################################################################################
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_shop;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //start loading animation
        progressView.start();

        //Setup listview and adapter
        setupListView();

        //Setup swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);

        //Retrieve inventory and populate ListView
        populateAdapter();
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
     * # SwipeRefreshLayout.OnRefreshListener implementation
     * #############################################################################################
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new ShopItemsUpdater().start(new OnEntityUpdated() {
            @Override
            public void onSuccess() {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Do not update adapter here (use eventBus)

                //Stop loading animation
                progressView.stop();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(@NonNull KIND errorKind) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Stop loading animation
                progressView.stop();
                swipeRefreshLayout.setRefreshing(false);

                //Display error message
                Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void setupListView() {
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        mAdapter = new ShopItemAdapter(mContext);
        listViewShop.setAdapter(mAdapter);
    }

    private void populateAdapter() {
        final List<ShopItem> shopItemList = NestedWorldDatabase.getInstance()
                .getDataBase()
                .getShopItemDao()
                .loadAll();

        if (shopItemList == null || shopItemList.isEmpty()) {
            mAdapter.clear();
            textViewShopEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewShopEmpty.setVisibility(View.GONE);
            mAdapter.clear();
            mAdapter.addAll(shopItemList);
        }

        //Stop loading animation
        swipeRefreshLayout.setRefreshing(false);
        progressView.stop();
    }
}