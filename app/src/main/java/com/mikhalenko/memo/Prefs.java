package com.mikhalenko.memo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;


public class Prefs implements SharedPreferences.OnSharedPreferenceChangeListener {


    private Context mContext;
    private boolean mAutoSaveNotEmpty;

    public Prefs(Context context) {
        mContext = context;
        readPrefs();
    }

    private void readPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        Resources r = mContext.getResources();
        readPref(r.getString(R.string.pref_autosave_not_empty_notes_key), sp);
    }

    public boolean isAutoSaveNotEmpty() {
        return mAutoSaveNotEmpty;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        readPref(key, sp);
    }

    private boolean readPref(String key, SharedPreferences sp) {
        Resources r = mContext.getResources();
        if (key.equals(r.getString(R.string.pref_autosave_not_empty_notes_key)))
            mAutoSaveNotEmpty = sp.getBoolean(key, true);
        else
            return false;
        return true;
    }
}
