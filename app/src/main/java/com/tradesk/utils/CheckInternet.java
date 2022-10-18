package com.tradesk.utils;

import android.content.Context;

import com.tradesk.di.ApplicationContext;
import com.tradesk.utils.service.ConnectivityReceiver;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class CheckInternet {

    private final Context mContext;

    @Inject
    public CheckInternet(@ApplicationContext Context context) {
        this.mContext = context;
    }

    public boolean checkConnectionActivity() {
        //        String message = "No Internet Connection";
        return ConnectivityReceiver.isConnected(mContext);
    }

}
