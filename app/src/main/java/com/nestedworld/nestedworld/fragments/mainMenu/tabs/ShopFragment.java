package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.models.request.chat.JoinChannelRequest;
import com.nestedworld.nestedworld.network.socket.models.request.chat.PartChannelRequest;
import com.nestedworld.nestedworld.network.socket.models.request.chat.SendMessageRequest;
import com.nestedworld.nestedworld.network.socket.models.request.combat.FleeRequest;
import com.nestedworld.nestedworld.network.socket.models.request.combat.MonsterKoCaptureRequest;
import com.nestedworld.nestedworld.network.socket.models.request.combat.MonsterKoReplaceRequest;
import com.nestedworld.nestedworld.network.socket.models.request.combat.SendAttackRequest;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShopFragment extends BaseFragment {

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
        return R.layout.fragment_tab_shop;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

    }
}