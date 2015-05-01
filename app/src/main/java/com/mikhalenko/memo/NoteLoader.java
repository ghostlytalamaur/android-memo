package com.mikhalenko.memo;

import android.content.Context;

public class NoteLoader extends DataLoader<SingleNote> {
    private long mNoteId;

    public NoteLoader(Context context, long noteId) {
        super(context);
        mNoteId = noteId;
    }


    @Override
    public SingleNote loadInBackground() {
        return NotesList.get(getContext()).getNote(mNoteId);
    }
}