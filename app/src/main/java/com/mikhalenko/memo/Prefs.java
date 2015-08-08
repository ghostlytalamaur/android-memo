package com.mikhalenko.memo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;


public class Prefs implements SharedPreferences.OnSharedPreferenceChangeListener {


    private Context mContext;
    private boolean mAutoSaveNotEmpty;
    private int mLastCategoryIndex;

    private final String mAutoSaveNotEmptyKey;
    private final String mLastCategoryIdKey;
    private final String mComplitedAtTheEndKey;
    private final String mSortTypeKey;
    private final String mSaveLastAccesedCategoryKey;

    private boolean mComplitedAtTheEnd;
    private boolean mNeedSaveLastCategory;
    private SortType mSortType;

    public Prefs(Context context) {
        mContext = context;
        Resources r = mContext.getResources();

        mAutoSaveNotEmptyKey = r.getString(R.string.pref_autosave_not_empty_notes_key);
        mLastCategoryIdKey = r.getString(R.string.pref_last_category_id_key);
        mComplitedAtTheEndKey = r.getString(R.string.pref_complited_at_the_end_key);
        mSortTypeKey = r.getString(R.string.pref_sorttype_key);
        mSaveLastAccesedCategoryKey = r.getString(R.string.pref_save_last_category_key);

        PreferenceManager.getDefaultSharedPreferences(mContext).
                registerOnSharedPreferenceChangeListener(this);
        readPrefs();
    }

    private void readPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        readPref(mAutoSaveNotEmptyKey, sp);
        readPref(mLastCategoryIdKey, sp);
        readPref(mComplitedAtTheEndKey, sp);
        readPref(mSortTypeKey, sp);
        readPref(mSaveLastAccesedCategoryKey, sp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        readPref(key, sp);
    }

    private boolean readPref(String key, SharedPreferences sp) {
        if (key.equals(mAutoSaveNotEmptyKey))
            mAutoSaveNotEmpty = sp.getBoolean(key, true);
        else if (key.equals(mLastCategoryIdKey))
            mLastCategoryIndex = sp.getInt(key, 0);
        else if (key.equals(mComplitedAtTheEndKey))
            mComplitedAtTheEnd = sp.getBoolean(key, true);
        else if (key.equals(mSortTypeKey))
            mSortType = SortType.values()[Integer.parseInt(sp.getString(key, "0"))];
        else if (key.equals(mSaveLastAccesedCategoryKey))
            mNeedSaveLastCategory = sp.getBoolean(key, true);
        else
            return false;
        return true;
    }

    public boolean isNeedSaveLastCategory() {
        return mNeedSaveLastCategory;
    }

    public boolean isAutoSaveNotEmpty() {
        return mAutoSaveNotEmpty;
    }

    public int getLastCategoryIndex() {
        return mLastCategoryIndex;
    }

    public boolean isComplitedAtTheEnd() {
        return mComplitedAtTheEnd;
    }

    public boolean setLastCategoryIndex(int lastCategoryIndex) {
        mLastCategoryIndex = lastCategoryIndex;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return writePref(mLastCategoryIdKey, sp);
    }

    private boolean writePref(String key, SharedPreferences sp) {
        if (key.equals(mLastCategoryIdKey))
            sp.edit().putInt(key, mLastCategoryIndex).apply();
        else
            return false;
        return true;
    }

    public SortType getSortType() {
        return mSortType;
    }
}