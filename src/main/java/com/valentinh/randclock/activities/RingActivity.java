package com.valentinh.randclock.activities;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.valentinh.randclock.R;

import java.io.IOException;
import java.util.Random;

public class RingActivity extends Activity
{

    Button stopBtn;
    TextView infoTxt;

    MediaPlayer player;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);


        stopBtn = (Button) findViewById(R.id.stopButton);
        stopBtn.setOnClickListener(new StopListener());
        infoTxt = (TextView) findViewById(R.id.infoTxt);


        if(savedInstanceState == null)
        {
            SongInfo song = getSong();
            if(song != null)
            {
                infoTxt.setText(song.title+" - "+song.artist);

                player = new MediaPlayer();
                player.setLooping(true);
                try
                {
                    player.setDataSource(song.path);
                    player.prepare();
                    player.start();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                long[] pattern = {0, 500, 500};
                vibrator.vibrate(pattern, 0);
            }
        }
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
        if(cursor.isNull(3))
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
            if(player!=null && player.isPlaying())
            {
                player.stop();
                player.release();
            }
            if(vibrator != null)
                vibrator.cancel();
            finish();
        }
    }
}