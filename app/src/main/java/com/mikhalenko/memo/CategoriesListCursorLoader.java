package com.mikhalenko.memo;

import android.content.Context;
import android.database.Cursor;

public class CategoriesListCursorLoader extends SQLiteCursorLoader {

    public CategoriesListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        return NotesList.get(getContext()).getCategories();
    }
}
