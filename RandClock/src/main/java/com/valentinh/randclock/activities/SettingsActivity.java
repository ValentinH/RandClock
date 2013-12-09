package com.valentinh.randclock.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.valentinh.randclock.R;


public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
