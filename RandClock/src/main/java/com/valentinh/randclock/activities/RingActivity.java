package com.valentinh.randclock.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.valentinh.randclock.R;
import com.valentinh.randclock.databases.Alarm_AdapterDB;
import com.valentinh.randclock.model.Alarm;
import com.valentinh.randclock.services.AlarmService;

import java.io.IOException;
import java.util.Random;

public class RingActivity extends Activity
{

    Button stopBtn;
    TextView infoTxt;
    TextView messageTxt;

    MediaPlayer player;
    Vibrator vibrator;
    boolean vibrate;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        setVolumeControlStream(AudioManager.STREAM_ALARM);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vibrate = prefs.getBoolean("vibrate_pref", false);

        long alarm_id = getIntent().getLongExtra(AlarmService.ALARM_ID, -1);
        stopBtn = (Button) findViewById(R.id.stopButton);
        stopBtn.setOnClickListener(new StopListener());
        infoTxt = (TextView) findViewById(R.id.infoTxt);
        messageTxt = (TextView) findViewById(R.id.message);


        if (savedInstanceState == null)
        {
            Alarm al = getAlarm(alarm_id);
            if (al != null)
            {
                messageTxt.setText(al.getTitle());
            }
            SongInfo song = getSong();
            if (song != null)
            {
                infoTxt.setText(song.title + " - " + song.artist);
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                player.setLooping(true);
                try
                {
                    player.setDataSource(song.path);
                    player.prepare();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(vibrate)
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (player != null && !player.isPlaying())
        {
            player.start();
        }
        if (vibrate && vibrator != null)
        {
            //Vibrate for 500 milliseconds
            long[] pattern = {0, 500, 500};
            vibrator.vibrate(pattern, 0);
        }
    }

    protected Alarm getAlarm(long id)
    {
        Alarm_AdapterDB adapt = new Alarm_AdapterDB(getApplicationContext());
        adapt.open();
        Alarm al = adapt.getOne(id);
        if (al != null && !al.isRepeat())
        {
            al.setEnabled(false);
            adapt.update(al.getId(), al);
        }
        adapt.close();

        return al;
    }

    protected SongInfo getSong()
    {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM
        };

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = managedQuery(allsongsuri, projection, selection, null, null);

        int total = cursor.getCount();
        Random r = new Random();
        int pos = r.nextInt(total);
        cursor.moveToPosition(pos);
        if (cursor.isNull(3))
            return null;

        return new SongInfo(cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

    protected class SongInfo
    {
        protected String title;
        protected String artist;
        protected String path;

        public SongInfo(String title, String artist, String path)
        {
            this.title = title;
            this.artist = artist;
            this.path = path;
        }
    }

    private class StopListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            finish();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (player != null && player.isPlaying())
        {
            player.pause();
        }
        if (vibrator != null)
            vibrator.cancel();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (player != null && player.isPlaying())
        {
            player.release();
            player = null;
        }
        if (vibrate && vibrator != null)
            vibrator.cancel();
    }
}
