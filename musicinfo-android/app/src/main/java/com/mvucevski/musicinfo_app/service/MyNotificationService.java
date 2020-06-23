package com.mvucevski.musicinfo_app.service;

import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.Semaphore;

public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "MyNotificationService";
    private static final String NOTIFICATIONS_KEY = "NOTIFICATIONS_KEY";
    static MyNotificationService _this;
    static Semaphore sem = new Semaphore(0);
    private static boolean connected = false;
    private boolean isBound = false;

    public static MyNotificationService get() {
        MyNotificationService ret = null;
        //sem.acquireUninterruptibly();
        if(connected){
            Log.i(TAG, "GET CONENCTED=TRUE");
            ret = _this;
        }

        //sem.release();
        Log.i(TAG, "GET in MyNotificationService");
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Connecting");
        //sem.acquireUninterruptibly();
        StatusBarNotification[] notifications = getActiveNotifications();
        Intent intent = new Intent(NOTIFICATIONS_KEY);
        // You can also include some extra data.
        intent.putExtra("notifications", notifications);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //connected = true;
        //_this = this;

        //sem.release();
        Log.i(TAG, "Connected");
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "Disconnected");
        //sem.acquireUninterruptibly();
        connected=false;
        _this = null;
        //sem.release();
    }


}