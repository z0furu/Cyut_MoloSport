package com.example.administrator.cyut_molosport.Alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2015/10/14.
 */
public class AlarmServiceBroadcastReciever  extends BroadcastReceiver {

    private static final String TAG = AlarmServiceBroadcastReciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.e(TAG,"onReceive()");
        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);
    }

}