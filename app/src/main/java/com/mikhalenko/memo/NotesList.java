package com.mikhalenko.memo;

import android.content.Context;
import java.util.ArrayList;

import static com.mikhalenko.memo.NotesDatabaseHelper.*;


public class NotesList {
    private static NotesList sNotesList;
    private final NotesDatabaseHelper mDbHelper;
    private ArrayList<INotesListener> mListeners;

    private NotesList(Context appContext) {
        Context context = appContext;
        mDbHelper = new NotesDatabaseHelper(context);
    }

    public static NotesList get(Context appContext) {
        if (sNotesList == null)
            sNotesList = new NotesList(appContext.getApplicationContext());
        return sNotesList;
    }

    public SingleNote getNote(long id) {
        SingleNote note = null;
        NoteCursor cursor = mDbHelper.queryNote(id);
        cursor.moveToFirst();
        if (!cursor.isAfterLast())
            note = cursor.getNote();
        cursor.close();
        return note;
    }

    public boolean insertOrUpdate(SingleNote note) {
        if (note.isEmpty())
            return false;
        boolean res;
        if ((note.getId() == -1)) {
            res = insertNote(note);
            if (res)
                notifyListeners(ListenerEvents.dataAdded);
        }
        else {
            res = updateNote(note);
            if (res)
                notifyListeners(ListenerEvents.dataChanged);
        }
        return res;

    }

    public NoteCursor getNotes() {
        return mDbHelper.queryNotes();
    }

    private boolean insertNote(SingleNote newNote) {
        long id = mDbHelper.insertNewNote(newNote);
        newNote.setId(id);
        boolean res = id != -1;
        if (res)
            notifyListeners(ListenerEvents.dataAdded);
        return res;
    }

    private boolean updateNote(SingleNote existingNote) {
        boolean res = mDbHelper.updateExistingNote(existingNote);
        notifyListeners(ListenerEvents.dataChanged);
        return res;
    }

    public boolean deleteNote(long id) {
        boolean res = mDbHelper.deleteNote(id);
        notifyListeners(ListenerEvents.dataDeleted);
        return res;
    }

    public void deleteAll() {
        mDbHelper.deleteAll();
        notifyListeners(ListenerEvents.dataDeleted);
    }


    private void notifyListeners(ListenerEvents event) {
        // TODO: make copy of mListeners before iterating
        for (INotesListener l : mListeners) {
            if (l != null)
                l.onNotesListEvent(event);
        }
    }

    public enum ListenerEvents {
        dataChanged,
        dataAdded,
        dataDeleted
    }

    public interface INotesListener {
        void onNotesListEvent(ListenerEvents event);
    }

    public boolean registerListener(INotesListener listener) {
        if (mListeners == null)
            mListeners = new ArrayList<INotesListener>();
        try {
            if (listener != null)
                return mListeners.add(listener);
            else
                return false;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(listener.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    public boolean unregisterListener(INotesListener listener) {
        if (listener == null)
            return false;
        return mListeners.remove(listener);
    }
}
