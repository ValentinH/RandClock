package com.valentinh.randclock.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Cette classe contient la définition du schéma de la base de données
 */
public class BaseOpenHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "RandClock.db";

    public BaseOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /*####### DONNEES ####### */
        //TABLES
        db.execSQL("CREATE TABLE alarms( id INTEGER PRIMARY KEY, title TEXT, hour INTEGER, minute INTEGER, repeat INTEGER);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS alarms;");

        // Create tables again
        onCreate(db);

    }

}
