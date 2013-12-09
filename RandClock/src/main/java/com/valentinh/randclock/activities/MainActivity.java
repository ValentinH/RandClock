package com.valentinh.randclock.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.valentinh.randclock.R;
import com.valentinh.randclock.databases.Alarm_AdapterDB;
import com.valentinh.randclock.model.Alarm;
import com.valentinh.randclock.services.AlarmService;

import java.util.ArrayList;
import java.util.Calendar;

import com.android.datetimepicker.time.TimePickerDialog;


public class MainActivity extends Activity
{
    protected ImageButton addButton, settingsButton;
    protected ListView listView;
    protected AlarmAdapter adapter;
    protected Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Widgets
        addButton = (ImageButton) findViewById(R.id.addButton);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        listView = (ListView) findViewById(R.id.list);

        //set Listener
        addButton.setOnClickListener(new AddListener());
        settingsButton.setOnClickListener(new SettingsListener());

    }

    private void refreshAlarmsList()
    {
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        ArrayList<Alarm> alarms = adapt.getAll();
        adapter = new AlarmAdapter(alarms);
        listView.setAdapter(adapter);
        adapt.close();
    }

    protected void addAlarm(int hour, int minute)
    {
        Alarm al = new Alarm(hour, minute);
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        al.setId(adapt.create(al));
        adapt.close();
        activateAlarm(al, true);
        adapter.add(al);
        adapter.notifyDataSetChanged();
    }

    private void deleteAlarm(Alarm al)
    {
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        adapt.delete(al.getId());
        adapt.close();
        activateAlarm(al, false);
        adapter.remove(al);
        adapter.notifyDataSetChanged();
    }


    protected void activateAlarm(Alarm a, boolean activated)
    {
        if (activated)
        {
            Intent service = new Intent(this, AlarmService.class);
            service.setAction(AlarmService.CREATE);
            service.putExtra(AlarmService.ALARM_ID, a.getId());
            this.startService(service);
            String s = "single alarm set for " + a.toString();
            if (a.isRepeat())
                s = "repeating alarm set for " + a.toString();
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        } else
        {
            Intent service = new Intent(this, AlarmService.class);
            service.setAction(AlarmService.CANCEL);
            service.putExtra(AlarmService.ALARM_ID, a.getId());
            this.startService(service);
        }
        a.setEnabled(activated);
        updateAlarmModel(a);
    }

    protected void updateAlarmModel(Alarm a)
    {
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        adapt.update(a.getId(), a);
        adapt.close();
        adapter.notifyDataSetChanged();
    }

    private class AddListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            calendar = Calendar.getInstance();

            TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
                {
                    addAlarm(hourOfDay, minute);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
           tpd.show(MainActivity.this.getFragmentManager(), null);

        }
    }

    private class SettingsListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

        }
    }

    public class AlarmAdapter extends BaseAdapter
    {
        private ArrayList<Alarm> listAlarms;
        private LayoutInflater inflater = null;
        private boolean switchEnabled;

        public AlarmAdapter(ArrayList<Alarm> listAlarms)
        {
            this.listAlarms = listAlarms;
            inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switchEnabled = false;
        }

        @Override
        public int getCount()
        {
            return listAlarms.size();
        }

        @Override
        public Alarm getItem(int position)
        {
            return listAlarms.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return listAlarms.get(position).getId();
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            Alarm al = listAlarms.get(position);
            switchEnabled = false;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.alarm_item, parent, false);
                holder = new ViewHolder();
                holder.back = (RelativeLayout) convertView.findViewById(R.id.back);
                holder.time = (TextView) convertView.findViewById(R.id.timeTxt);
                holder.time.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final Alarm a = listAlarms.get((Integer) v.getTag());
                        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
                            {
                                a.setHour(hourOfDay);
                                a.setMinute(minute);
                                activateAlarm(a, a.isEnabled());
                            }
                        }, a.getHour(), a.getMinute(), true);
                        tpd.show(MainActivity.this.getFragmentManager(), null);
                    }
                });
                holder.repeat = (CheckBox) convertView.findViewById(R.id.repeatCheckBox);
                holder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        if (switchEnabled)
                        {
                            Alarm a = listAlarms.get((Integer) buttonView.getTag());
                            a.setRepeat(buttonView.isChecked());
                            activateAlarm(a, a.isEnabled());
                        }
                    }
                });
                holder.message = (Button) convertView.findViewById(R.id.messageButton);
                holder.message.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final Alarm a = listAlarms.get((Integer) v.getTag());
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final EditText input = new EditText(MainActivity.this);
                        input.setText(a.getTitle());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                String value = input.getText().toString();
                                a.setTitle(value);
                                updateAlarmModel(a);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                // Canceled.
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Message");
                        dialog.setView(input);
                        dialog.show();
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                });
                holder.switchView = (Switch) convertView.findViewById(R.id.enableSwitch);
                holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton v, boolean isChecked)
                    {
                        if (switchEnabled)
                        {
                            Alarm a = listAlarms.get((Integer) v.getTag());
                            if (a != null)
                            {
                                activateAlarm(a, isChecked);
                                View parent = (View) v.getParent();
                                setHolderColors((ViewHolder) parent.getTag(), isChecked);
                            }
                        }
                    }
                });
                holder.deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
                holder.deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Alarm a = listAlarms.get((Integer) v.getTag());
                        if (a != null)
                        {
                            deleteAlarm(a);
                        }
                    }
                });
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.time.setText(al.toString());
            holder.message.setText(al.getTitle());
            holder.repeat.setChecked(al.isRepeat());
            holder.switchView.setChecked(al.isEnabled());
            holder.switchView.setTag(position);
            holder.deleteButton.setTag(position);
            holder.repeat.setTag(position);
            holder.message.setTag(position);
            holder.time.setTag(position);

            setHolderColors(holder, al.isEnabled());

            switchEnabled = true;
            return convertView;
        }

        protected void setHolderColors(ViewHolder holder, boolean enabled)
        {
            if (enabled)
            {
                holder.time.setTextColor(getResources().getColor(R.color.list_time));
                holder.message.setTextColor(getResources().getColor(R.color.list_message));
                holder.repeat.setTextColor(getResources().getColor(R.color.list_repeat));
                holder.back.setBackgroundColor(getResources().getColor(R.color.list_background));
            } else
            {
                holder.time.setTextColor(getResources().getColor(R.color.list_time_disable));
                holder.message.setTextColor(getResources().getColor(R.color.list_message_disable));
                holder.repeat.setTextColor(getResources().getColor(R.color.list_repeat_disable));
                holder.back.setBackgroundColor(getResources().getColor(R.color.list_background_disable));
            }
        }

        class ViewHolder
        {
            TextView time;
            CheckBox repeat;
            Button message;
            Switch switchView;
            ImageButton deleteButton;
            RelativeLayout back;
        }

        public void add(Alarm a)
        {
            listAlarms.add(a);
        }

        public void remove(Alarm to_remove)
        {
            listAlarms.remove(to_remove);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refreshAlarmsList();
    }
}
