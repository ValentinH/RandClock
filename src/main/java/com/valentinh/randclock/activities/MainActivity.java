package com.valentinh.randclock.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.valentinh.randclock.R;
import com.valentinh.randclock.databases.Alarm_AdapterDB;
import com.valentinh.randclock.model.Alarm;
import com.valentinh.randclock.services.AlarmService;

import java.util.ArrayList;


public class MainActivity extends Activity
{
    protected ImageButton addButton, settingsButton;
    protected ListView listView;
    protected AlarmAdapter adapter;

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

    protected void addAlarm(int hour, int minute, String message, boolean repeat)
    {
        Alarm al = new Alarm(message, hour, minute, false);
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
            Toast.makeText(this, "Alarm set for " + a.toString(), Toast.LENGTH_SHORT).show();
        } else
        {
            Intent service = new Intent(this, AlarmService.class);
            service.setAction(AlarmService.CANCEL);
            service.putExtra(AlarmService.ALARM_ID, a.getId());
            this.startService(service);
        }
        a.setEnabled(activated);
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        adapt.update(a.getId(), a);
        adapt.close();
    }

    private class AddListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog dialog;
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            builder.setTitle(getString(R.string.addAlarm));
            View contentView = inflater.inflate(R.layout.add_alarm, null);

            final TimePicker tp = (TimePicker) contentView.findViewById(R.id.timePicker);
            tp.setIs24HourView(true);
            final CheckBox repeatCheckbox = (CheckBox) contentView.findViewById(R.id.repeatCheckbox);
            final EditText messageText = (EditText) contentView.findViewById(R.id.message);

            builder.setView(contentView);
            builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    boolean repeat = repeatCheckbox.isChecked();
                    addAlarm(tp.getCurrentHour(), tp.getCurrentMinute(), messageText.getText().toString(), repeat);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            dialog = builder.create();
            dialog.show();
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
                holder.repeat = (TextView) convertView.findViewById(R.id.repeatTxt);
                holder.message = (TextView) convertView.findViewById(R.id.messageView);
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
                                setHolderColors((ViewHolder) parent.getTag(),isChecked);
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
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.message.setText(al.getTitle());
            holder.time.setText(al.toString());
            if (al.isRepeat())
                holder.repeat.setText("repeat");
            else
                holder.repeat.setText("");

            holder.switchView.setChecked(al.isEnabled());
            holder.switchView.setTag(position);
            holder.deleteButton.setTag(position);

            setHolderColors(holder, al.isEnabled());

            switchEnabled = true;
            return convertView;
        }

        protected void setHolderColors(ViewHolder holder, boolean enabled)
        {
            if(enabled)
            {
                holder.time.setTextColor(Color.parseColor("#000000"));
                holder.message.setTextColor(Color.parseColor("#333333"));
                holder.repeat.setTextColor(Color.parseColor("#666666"));
                holder.back.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            else
            {
                holder.time.setTextColor(Color.parseColor("#333333"));
                holder.message.setTextColor(Color.parseColor("#666666"));
                holder.repeat.setTextColor(Color.parseColor("#999999"));
                holder.back.setBackgroundColor(Color.parseColor("#cccccc"));
            }
        }

        class ViewHolder
        {
            TextView time;
            TextView repeat;
            TextView message;
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
