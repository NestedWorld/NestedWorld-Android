package com.nestedworld.nestedworld.ui.mainMenu.tabs.inventory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.InventoryResponse;
import com.nestedworld.nestedworld.ui.base.BaseFragment;
import com.nestedworld.nestedworld.ui.mainMenu.tabs.home.HomeFragment;

import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserInventoryFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = HomeFragment.class.getSimpleName();

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
        return R.layout.fragment_tab_inventory;
    }

    @Override
    protected void init(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        NestedWorldHttpApi.getInstance().getUserInventory().enqueue(new NestedWorldHttpCallback<InventoryResponse>() {
            @Override
            public void onSuccess(@NonNull Response<InventoryResponse> response) {

            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<InventoryResponse> response) {

            }
        });
    }
}