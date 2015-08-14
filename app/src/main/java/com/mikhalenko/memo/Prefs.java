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
    private final String mCompletedAtTheEndKey;
    private final String mSortTypeKey;
    private final String mSaveLastAccessedCategoryKey;

    private boolean mCompletedAtTheEnd;
    private boolean mNeedSaveLastCategory;
    private SortType mSortType;
    private OnSortTypeChangedListener mSortTypeChangedListener;

    public interface OnSortTypeChangedListener {
        void sortTypeChanged(SortType newSortType, boolean completedAtTheEnd);
    }

    public Prefs(Context context) {
        mContext = context;
        Resources r = mContext.getResources();

        mAutoSaveNotEmptyKey = r.getString(R.string.pref_autosave_not_empty_notes_key);
        mLastCategoryIdKey = r.getString(R.string.pref_last_category_id_key);
        mCompletedAtTheEndKey = r.getString(R.string.pref_complited_at_the_end_key);
        mSortTypeKey = r.getString(R.string.pref_sorttype_key);
        mSaveLastAccessedCategoryKey = r.getString(R.string.pref_save_last_category_key);


        PreferenceManager.getDefaultSharedPreferences(mContext).
                registerOnSharedPreferenceChangeListener(this);
        readPrefs();
    }

    private void readPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        readPref(mAutoSaveNotEmptyKey, sp);
        readPref(mLastCategoryIdKey, sp);
        readPref(mCompletedAtTheEndKey, sp);
        readPref(mSortTypeKey, sp);
        readPref(mSaveLastAccessedCategoryKey, sp);
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
        else if (key.equals(mCompletedAtTheEndKey)) {
            mCompletedAtTheEnd = sp.getBoolean(key, true);
            onSortTypeChanged();
        }
        else if (key.equals(mSortTypeKey)) {
            mSortType  = SortType.values()[Integer.parseInt(sp.getString(key, "0"))];
            onSortTypeChanged();
        }
        else if (key.equals(mSaveLastAccessedCategoryKey))
            mNeedSaveLastCategory = sp.getBoolean(key, true);
        else
            return false;
        return true;
    }

    private void onSortTypeChanged() {
        if (mSortTypeChangedListener != null)
            mSortTypeChangedListener.sortTypeChanged(mSortType, mCompletedAtTheEnd);
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

    public boolean isCompletedAtTheEnd() {
        return mCompletedAtTheEnd;
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

    public void setOnSortTypeChangedListener(OnSortTypeChangedListener aListener) {
        mSortTypeChangedListener = aListener;
    }
}