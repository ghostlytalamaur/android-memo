package com.mikhalenko.memo;

import android.content.Context;
import android.database.Cursor;


public class NoteListCursorLoader extends SQLiteCursorLoader {


    private long mCategoryID;

    public NoteListCursorLoader(Context context, long aCategoryID) {
        super(context);
        mCategoryID = aCategoryID;
    }

    @Override
    protected Cursor loadCursor() {
        return NotesList.get(getContext()).getNotesFromCategory(mCategoryID);
//        return NotesList.get(getContext()).getNotes();
    }
}