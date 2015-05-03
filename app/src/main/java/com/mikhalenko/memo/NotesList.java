package com.mikhalenko.memo;

import android.content.Context;
import java.util.ArrayList;

import static com.mikhalenko.memo.NotesDatabaseHelper.*;


public class NotesList {
    private static NotesList sNotesList;
    private final NotesDatabaseHelper mDbHelper;
    private ArrayList<INotesListener> mListeners;

    private NotesList(Context appContext) {
        mDbHelper = new NotesDatabaseHelper(appContext);
    }

    public static NotesList get(Context appContext) {
        if (sNotesList == null)
            sNotesList = new NotesList(appContext.getApplicationContext());
        return sNotesList;
    }

    public SingleNote getNote(long id) {
        return mDbHelper.queryNote(id);
    }

    public boolean insertOrUpdate(SingleNote note) {
        if (note.isEmpty())
            return false;
        boolean res = mDbHelper.insertOrUpdateNote(note);
        if (res)
            notifyListeners(ListenerEvents.dataChanged);
        return res;

    }

    public NoteCursor getNotes() {
        return mDbHelper.queryNotes();
    }

    public NoteCursor getNotesFromCategory(long categoryId) {
        return mDbHelper.queryNotesFromCategory(categoryId);
    }

    public CategoryCursor getCategories() {
        return  mDbHelper.queryCategories();
    }

    public boolean addOrUpdateCategory(Category category) {
        return mDbHelper.insertOrUpdateCategory(category);
    }

    public boolean deleteCategory(long id) {
        return mDbHelper.deleteCategory(id);
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
            mListeners = new ArrayList<>();
        try {
            return listener != null && mListeners.add(listener);
        }
        catch (ClassCastException e) {
            throw new ClassCastException(listener.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    public boolean unregisterListener(INotesListener listener) {
        return listener != null && mListeners.remove(listener);
    }
}
