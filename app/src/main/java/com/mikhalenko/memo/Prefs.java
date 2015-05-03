package com.mikhalenko.memo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;


public class Prefs implements SharedPreferences.OnSharedPreferenceChangeListener {


    private Context mContext;
    private boolean mAutoSaveNotEmpty;
    private long mLastCategoryId;

    private final String mAutoSaveNotEmptyKey;
    private final String mLastCategoryIdKey;

    public Prefs(Context context) {
        mContext = context;
        Resources r = mContext.getResources();

        mAutoSaveNotEmptyKey = r.getString(R.string.pref_autosave_not_empty_notes_key);
        mLastCategoryIdKey = r.getString(R.string.pref_last_category_id_key);

        PreferenceManager.getDefaultSharedPreferences(mContext).
                registerOnSharedPreferenceChangeListener(this);
        readPrefs();
    }

    private void readPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        readPref(mAutoSaveNotEmptyKey, sp);
        readPref(mLastCategoryIdKey, sp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        readPref(key, sp);
    }

    private boolean readPref(String key, SharedPreferences sp) {
        if (key.equals(mAutoSaveNotEmptyKey))
            mAutoSaveNotEmpty = sp.getBoolean(key, true);
        else if (key.equals(mLastCategoryIdKey))
            mLastCategoryId = sp.getLong(key, 1);
        else
            return false;
        return true;
    }



    public boolean isAutoSaveNotEmpty() {
        return mAutoSaveNotEmpty;
    }

    public long getLastCategoryId() {
        return mLastCategoryId;
    }

    public boolean setLastCategoryId(long lastCategoryId) {
        mLastCategoryId = lastCategoryId;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return writePref(mLastCategoryIdKey, sp);
    }

    private boolean writePref(String key, SharedPreferences sp) {
        if (key.equals(mLastCategoryIdKey))
            sp.edit().putLong(key, mLastCategoryId).apply();
        else
            return false;
        return true;
    }
}