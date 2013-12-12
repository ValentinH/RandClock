package com.valentinh.randclock.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.valentinh.randclock.services.AlarmService;

public class AlarmSetter extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmService.class);
        service.setAction(AlarmService.RESET_ALL);
        context.startService(service);
    }

}