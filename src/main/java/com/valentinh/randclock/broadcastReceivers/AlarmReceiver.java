package com.valentinh.randclock.broadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.valentinh.randclock.activities.RingActivity;
import com.valentinh.randclock.services.AlarmService;

public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "RandClock");
        //Acquire the lock
        wl.acquire();

        //start RingActivity
        long id = intent.getLongExtra(AlarmService.ALARM_ID, -1);
        Intent newIntent = new Intent(context, RingActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(AlarmService.ALARM_ID, id);
        context.startActivity(newIntent);

        //Release the lock
        wl.release();
    }
}