package com.valentinh.randclock;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.valentinh.randclock.databases.BaseOpenHelper;

/**
 * Created by Valentin on 07/12/13.
 */
public class RandClock extends Application
{
    public static final String TAG = "RandClock";

    public static BaseOpenHelper dbHelper;
    public static SQLiteDatabase db;
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        dbHelper = new BaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();
    }

}