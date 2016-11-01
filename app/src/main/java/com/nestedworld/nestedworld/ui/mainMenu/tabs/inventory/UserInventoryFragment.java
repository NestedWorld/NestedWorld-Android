package com.nestedworld.nestedworld.ui.mainMenu.tabs.inventory;

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
import com.nestedworld.nestedworld.adapter.InventoryObjectAdapter;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.InventoryResponse;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.home.HomeFragment;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserInventoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.textview_inventory_empty)
    TextView textViewInventoryEmpty;
    @BindView(R.id.swipeRefreshLayout_inventory)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listView_inventory)
    ListView listViewInventory;
    @BindView(R.id.progressView)
    ProgressView progressView;

    private InventoryObjectAdapter mAdapter;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .addToBackStack(null)
                .commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_inventory;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        //Start loading animation
        progressView.start();

        //Setup listview and adapter
        setupListView();

        //Setup swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);

        //Retrieve inventory and populate ListView
        retrieveInventoryObject();
    }

    /*
    ** SwipeRefreshLayout.OnRefreshListener implementation
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        retrieveInventoryObject();
    }

    /*
    ** Internal method
     */
    private void retrieveInventoryObject() {
        NestedWorldHttpApi.getInstance().getUserInventory().enqueue(new NestedWorldHttpCallback<InventoryResponse>() {
            @Override
            public void onSuccess(@NonNull Response<InventoryResponse> response) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Stop loading animation
                progressView.stop();
                swipeRefreshLayout.setRefreshing(false);

                //Check response
                if (response.body() == null || response.body().objects == null) {
                    onError(KIND.SERVER, response);
                } else if (!response.body().objects.isEmpty()) {
                    textViewInventoryEmpty.setVisibility(View.GONE);
                    mAdapter.clear();
                    mAdapter.addAll(response.body().objects);
                } else {
                    textViewInventoryEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<InventoryResponse> response) {
                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }
                //Stop loading animation
                progressView.stop();
                swipeRefreshLayout.setRefreshing(false);

                //Display error message
                Toast.makeText(mContext, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                textViewInventoryEmpty.setVisibility(View.VISIBLE);
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

        mAdapter = new InventoryObjectAdapter(mContext);
        listViewInventory.setAdapter(mAdapter);
    }
}