package com.example.administrator.cyut_molosport.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.administrator.cyut_molosport.Alarm.service.AlarmServiceBroadcastReciever;
import com.example.administrator.cyut_molosport.Member.MainActivity;
import com.example.administrator.cyut_molosport.R;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent mathAlarmServiceIntent = new Intent(
                context,
                AlarmServiceBroadcastReciever.class);
        context.sendBroadcast(mathAlarmServiceIntent, null);

        StaticWakeLock.lockOn(context);

        Log.d(this.getClass().getSimpleName(), "AlarmReceiver");

        //Bundle bundle = intent.getExtras();
        String message = "該運動囉!!!!!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent call = new Intent(Intent.ACTION_MAIN);
        call.addCategory(Intent.CATEGORY_LAUNCHER);
        call.setClass(context, MainActivity.class);
        call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


        PendingIntent pi = PendingIntent.getActivity(context, 0, call, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_alarm_add_black_36dp)
                .setContentTitle(message)
                .setContentText("MoloSport運動計畫");

        Notification notification = builder.build();


        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager ni = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        ni.notify(1,notification);
    }
}
