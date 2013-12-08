package com.valentinh.randclock.databases;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractAdapterDB
{
    protected Context context;
    protected BaseOpenHelper baseHelper;
    protected SQLiteDatabase db;

    public AbstractAdapterDB(Context ctx)
    {
        context = ctx;
    }

    public void open()
    {
        baseHelper = new BaseOpenHelper(context);
        db = baseHelper.getWritableDatabase();
    }

    public void close()
    {
        db.close();
    }

}
