package com.valentinh.randclock.model;

/**
 * Created by Valentin on 08/12/13.
 */
public class Alarm
{
    private long id;

    private String title;
    private int hour;
    private int minute;
    private boolean repeat;

    public Alarm()
    {
    }

    public Alarm(String title, int hour, int minute, boolean repeat)
    {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public boolean isRepeat()
    {
        return repeat;
    }

    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }
    public void setRepeat(int repeat)
    {
        this.repeat = (repeat == 1);
    }
}
