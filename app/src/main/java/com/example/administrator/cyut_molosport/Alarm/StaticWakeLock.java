package com.example.administrator.cyut_molosport.Alarm;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Administrator on 2015/10/14.
 */
public class StaticWakeLock {

    private static PowerManager.WakeLock wl = null;

    public static void lockOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //Object flags;
        if (wl == null)
            wl = pm.newWakeLock( PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "MATH_ALARM");
        wl.acquire(); //喚醒螢幕
    }

    public static void lockOff(Context context) {
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            if (wl != null)
                wl.release();//釋放powerManager
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}

