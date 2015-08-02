package com.mikhalenko.memo;

import android.content.Context;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Observable;

import static com.mikhalenko.memo.NotesDatabaseHelper.*;


public class NotesList extends Observable {
    private static NotesList sNotesList;
    private final NotesDatabaseHelper mDbHelper;
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
        notifyListeners();
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
        boolean res = mDbHelper.insertOrUpdateCategory(category);
        if (res)
            notifyListeners();
        return res;
    }

    public Category getCategory(long id) {
        return mDbHelper.queryCategory(id);
    }

    public Category getCategoryByPos(int pos) {
        return mDbHelper.queryCategoryByPos(pos);
    }

    public int getNotesInCategory(long categoryID) {
        NoteCursor c = getNotesFromCategory(categoryID);
        int res = c.getCount();
        c.close();
        return res;
    }

    public boolean deleteCategory(long id) {
        boolean res = mDbHelper.deleteCategory(id);
        if (res)
            notifyListeners();
        return res;
    }

    public boolean deleteNote(long id) {
        boolean res = mDbHelper.deleteNote(id);
        notifyListeners();
        return res;
    }

    public void deleteAll() {
        mDbHelper.deleteAll();
        notifyListeners();
    }

    private void notifyListeners() {
        setChanged();
        notifyObservers();
    }

    public void deleteAllFromCategory(long categoryId) {
        mDbHelper.deleteAllFromCategory(categoryId);
        notifyListeners();
    }

    public int getCategoriesCount() {
        CategoryCursor cursor = getCategories();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
