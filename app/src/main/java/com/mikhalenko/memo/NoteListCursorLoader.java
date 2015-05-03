package com.mikhalenko.memo;

import android.content.Context;
import android.database.Cursor;


public class NoteListCursorLoader extends SQLiteCursorLoader {


    private Prefs mPrefs;

    public NoteListCursorLoader(Context context) {
        super(context);
        mPrefs = new Prefs(getContext());
    }

    @Override
    protected Cursor loadCursor() {
        return NotesList.get(getContext()).getNotesFromCategory(mPrefs.getLastCategoryId());
    }
}