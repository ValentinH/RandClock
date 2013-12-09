package com.valentinh.randclock.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Debug;
import android.util.Log;

import com.valentinh.randclock.model.Alarm;

import java.util.ArrayList;

/**
 * Created by Valentin on 08/12/13.
 */
public class Alarm_AdapterDB extends AbstractAdapterDB
{

    private static final String DATABASE_TABLE = "alarms";

    public static final String KEY_ROWID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_REPEAT = "repeat";
    public static final String KEY_ENABLED = "enabled";

    public Alarm_AdapterDB(Context ctx)
    {
        super(ctx);
    }

    public long create(Alarm ac)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, ac.getTitle());
        args.put(KEY_HOUR, ac.getHour());
        args.put(KEY_MINUTE, ac.getMinute());
        args.put(KEY_REPEAT, ac.getRepeat());
        args.put(KEY_ENABLED, ac.getEnabled());

        return db.insert(DATABASE_TABLE, null, args);
    }

    public long update(long id, Alarm ac)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, ac.getTitle());
        args.put(KEY_HOUR, ac.getHour());
        args.put(KEY_MINUTE, ac.getMinute());
        args.put(KEY_REPEAT, ac.getRepeat());
        args.put(KEY_ENABLED, ac.getEnabled());

        return db.update(DATABASE_TABLE, args, KEY_ROWID + "= " + id, null);
    }

    public boolean delete(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Alarm getOne(long rowId)
    {
        Cursor c = db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_HOUR, KEY_MINUTE, KEY_REPEAT, KEY_ENABLED}, KEY_ROWID + "=" + rowId, null, null, null, null);

        if( c.getCount() == 0)
        {
            c.close();
            return null;
        }

        c.moveToFirst();

        Alarm ac = new Alarm();
        ac.setId(c.getLong(c.getColumnIndex(KEY_ROWID)));
        ac.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
        ac.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)));
        ac.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)));
        ac.setRepeat(c.getInt(c.getColumnIndex(KEY_REPEAT)));
        ac.setEnabled(c.getInt(c.getColumnIndex(KEY_ENABLED)));
        c.close();
        return ac;
    }

    public ArrayList<Alarm> getAll()
    {
        Cursor c = db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_HOUR, KEY_MINUTE, KEY_REPEAT, KEY_ENABLED}, null, null, null, null, null);

        if( c.getCount() == 0)
        {
            c.close();
            return new ArrayList<Alarm>(0);
        }

        ArrayList<Alarm> acs = new ArrayList<Alarm>(c.getCount());
        c.moveToFirst();
        do{
            Alarm ac = new Alarm();
            ac.setId(c.getLong(c.getColumnIndex(KEY_ROWID)));
            ac.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            ac.setHour(c.getInt(c.getColumnIndex(KEY_HOUR)));
            ac.setMinute(c.getInt(c.getColumnIndex(KEY_MINUTE)));
            ac.setRepeat(c.getInt(c.getColumnIndex(KEY_REPEAT)));
            ac.setEnabled(c.getInt(c.getColumnIndex(KEY_ENABLED)));
            acs.add(ac);
        }while(c.moveToNext());

        c.close();
        return acs;
    }
}
