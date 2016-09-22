package com.nestedworld.nestedworld.fragments.base;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.nestedworld.nestedworld.helpers.service.ServiceHelper;
import com.nestedworld.nestedworld.service.SocketService;

public abstract class SocketServiceFragment extends BaseFragment {

    private SocketService mService = null;
    protected boolean mBound = false;

    /*
    ** Method every child will have to implement
     */
    protected void onSocketServiceReady(@NonNull final SocketService mService) {
        //service ready, do what you want
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        ServiceHelper.bindToSocketService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SocketService.LocalBinder localBinder = (SocketService.LocalBinder) service;
                mService = localBinder.getService();
                mBound = true;

                onSocketServiceReady(mService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                mService = null;
            }
        });
    }

    /*
    ** Utils
     */
    @Nullable
    protected SocketService getSocketService() {
        return mService;
    }
}
