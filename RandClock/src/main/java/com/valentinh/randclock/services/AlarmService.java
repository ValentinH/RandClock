package com.valentinh.randclock.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.valentinh.randclock.RandClock;
import com.valentinh.randclock.broadcastReceivers.AlarmReceiver;
import com.valentinh.randclock.databases.Alarm_AdapterDB;
import com.valentinh.randclock.model.Alarm;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmService extends IntentService
{

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final String RESET_ALL = "RESET_ALL";

    public static final String ALARM_ID = "alarmId";

    private IntentFilter matcher;

    public AlarmService()
    {
        super(RandClock.TAG);
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
        matcher.addAction(RESET_ALL);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String action = intent.getAction();
        long alarmId = intent.getLongExtra(ALARM_ID, -1);

        if (matcher.matchAction(action))
        {
            execute(action, alarmId);
        }
    }

    private void execute(String action, long alarmId)
    {
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        if (RESET_ALL.equals(action))
        {
            ArrayList<Alarm> alarms = adapt.getAll();
            for (Alarm a : alarms)
            {
                setAlarm(a);
            }
        } else if (CREATE.equals(action))
        {
            Alarm a = adapt.getOne(alarmId);
            if (a != null)
                setAlarm(a);
        }
        else
        {
            cancelAlarm((int)alarmId);
        }

        adapt.close();
    }

    private void setAlarm(Alarm a)
    {
        if(!a.isEnabled())
            return;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra(ALARM_ID, a.getId());

        PendingIntent pi = PendingIntent.getBroadcast(this, (int) a.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (a.isRepeat())
            am.setRepeating(AlarmManager.RTC_WAKEUP, getMillis(a), 24 * 3600 * 1000, pi);
        else
            am.set(AlarmManager.RTC_WAKEUP, getMillis(a), pi);
    }

    private void cancelAlarm(int id)
    {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    private long getMillis(Alarm a)
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long current = c.getTimeInMillis();

        c.set(Calendar.HOUR_OF_DAY, a.getHour());
        c.set(Calendar.MINUTE, a.getMinute());

        if (current >= c.getTimeInMillis())
            c.add(Calendar.DAY_OF_YEAR, 1);

        return c.getTimeInMillis();
    }

}
