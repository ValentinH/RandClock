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
    private boolean enabled;

    public Alarm()
    {
    }

    public Alarm(int hour, int minute)
    {
        this.title = "";
        this.hour = hour;
        this.minute = minute;
        this.repeat = false;
        this.enabled = true;
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
    public int getRepeat()
    {
       return repeat ? 1 : 0;
    }

    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }
    public void setRepeat(int r)
    {
        this.repeat = (r == 1);
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    public int getEnabled()
    {
        return enabled ? 1 : 0;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    public void setEnabled(int e)
    {
        this.enabled = (e == 1);
    }

    @Override
    public String toString()
    {
        String sHour = hour + "";
        if(hour<10) sHour = "0"+hour;
        String sMinute = minute + "";
        if(minute<10) sMinute = "0"+minute;
        return  sHour +":" + sMinute;
    }
}
