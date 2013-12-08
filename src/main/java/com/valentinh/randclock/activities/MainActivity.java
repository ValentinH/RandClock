package com.valentinh.randclock.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.valentinh.randclock.broadcastReceivers.AlarmReceiver;
import com.valentinh.randclock.R;
import com.valentinh.randclock.databases.Alarm_AdapterDB;
import com.valentinh.randclock.model.Alarm;
import com.valentinh.randclock.services.AlarmService;

import java.util.Calendar;

public class MainActivity extends Activity
{

    private AlarmReceiver alarm;

    protected Button setBt, activateBt;

    protected Calendar calendar;

    protected int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Widgets
        setBt = (Button) findViewById(R.id.btSet);
        activateBt = (Button) findViewById(R.id.btActivate);

        //set Listener
        setBt.setOnClickListener(new SetListener());
        activateBt.setOnClickListener(new ActivateListener());

        calendar  = Calendar.getInstance();


    }

    protected void setTime(int h, int m)
    {
        this.hour = h;
        this.minute = m;
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
    }

    private class SetListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                   setTime(hourOfDay, minute);
                }
            }, hour, minute, true);
            tpd.show();
        }
    }

    private class ActivateListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Context context = getApplicationContext();
            if (context != null)
            {
                Alarm al = new Alarm("Test", hour, minute, false);
                Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
                adapt.open();
                al.setId(adapt.create(al));
                adapt.close();
                Intent service = new Intent(context, AlarmService.class);
                service.setAction(AlarmService.CREATE);
                service.putExtra(AlarmService.ALARM_ID, al.getId());
                context.startService(service);
            }
        }
    }

}
