package com.mikhalenko.memo;

import android.content.Context;
import android.database.Cursor;


public class NoteListCursorLoader extends SQLiteCursorLoader {

    public NoteListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        return NotesList.get(getContext()).getNotes();
    }
}