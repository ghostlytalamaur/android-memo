package com.mikhalenko.memo;

import android.content.Context;

public class NoteSaver extends DataLoader<SingleNote> {
    private SingleNote mNote;

    public NoteSaver(Context context, SingleNote note) {
        super(context);
        mNote = note;
    }


    @Override
    public SingleNote loadInBackground() {
        if (NotesList.get(getContext()).insertOrUpdate(mNote))
            return mNote;
        else
            return null;
    }
}