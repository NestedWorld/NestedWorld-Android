package com.nestedworld.nestedworld.fragment.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;

public class ChatFragment extends BaseFragment {

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ChatFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_chat;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

    }
}
