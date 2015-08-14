package com.mikhalenko.memo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Vector;

import static com.mikhalenko.memo.NotesDatabaseHelper.CategoryCursor;
import static com.mikhalenko.memo.NotesDatabaseHelper.NoteCursor;


public class NotesList extends Observable implements Prefs.OnSortTypeChangedListener{
    private static NotesList sNotesList;
    private final NotesDatabaseHelper mDbHelper;
    private HandlerExtension mHandler;
    private Prefs mPrefs;

    private CategoryList mCategories;
    private NotesList(Context appContext) {
        mDbHelper = new NotesDatabaseHelper(appContext);
        mCategories = new CategoryList();
        mHandler = new HandlerExtension(this);
        mPrefs = new Prefs(appContext);
        mPrefs.setOnSortTypeChangedListener(this);
        refreshList();
    }

    public static NotesList get(Context appContext) {
        if (sNotesList == null)
            sNotesList = new NotesList(appContext.getApplicationContext());
        return sNotesList;
    }

    public void dataChanged() {
        notifyUIListeners();
    }
    private void refreshList() {
        CategoryCursor categoryCursor = mDbHelper.queryCategories();
        if (categoryCursor.isBeforeFirst() && categoryCursor.isAfterLast())
            return;
        Vector<Long> validCategories = new Vector<>();
        categoryCursor.moveToFirst();
        do {
            Category NewCategory = categoryCursor.getCategory();
            validCategories.add(NewCategory.getID());

            Category wCategory = mCategories.getByID(NewCategory.getID());
            if (wCategory == null) {
                mCategories.add(NewCategory);
                wCategory = NewCategory;
            }
            else
                wCategory.copyFrom(NewCategory);

            NoteCursor noteCursor = mDbHelper.queryNotesFromCategory(wCategory.getID());
            if (noteCursor.isBeforeFirst() && noteCursor.isAfterLast()) {
                wCategory.getNotes().clear();
                continue;
            }

            Vector<Long> validIDs = new Vector<>();
            noteCursor.moveToFirst();
            do {
                SingleNote note = noteCursor.getNote();

                SingleNote wNote = wCategory.getNotes().getByID(note.getID());
                if (wNote == null)
                    wCategory.getNotes().add(note);
                else
                    wNote.copyFrom(note);
                validIDs.add(note.getID());
            } while (noteCursor.moveToNext());
            wCategory.getNotes().deleteInvalid(validIDs);
            wCategory.getNotes().sortList(mPrefs.getSortType(), mPrefs.isCompletedAtTheEnd());
            noteCursor.close();
        } while (categoryCursor.moveToNext());
        mCategories.deleteNotValid(validCategories);
        categoryCursor.close();
    }

    public CategoryList getCategoriesList() {
        return mCategories;
    }
    public SingleNote getNote(long id) {
        return mDbHelper.queryNote(id);
    }

    public boolean insertOrUpdate(SingleNote note) {
        if (note.isEmpty())
            return false;
        boolean res = mDbHelper.insertOrUpdateNote(note);
        notifyUIListeners();
        return res;

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
            notifyUIListeners();
        return res;
    }

    public Category getCategory(long id) {
        return mDbHelper.queryCategory(id);
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
            notifyUIListeners();
        return res;
    }

    public boolean deleteNote(long id) {
        boolean res = mDbHelper.deleteNote(id);
        notifyUIListeners();
        return res;
    }

    private void notifyUIListeners() {
        refreshList();
        setChanged();
        mHandler.sendMessage(new Message());
    }

    public void deleteAllFromCategory(long categoryId) {
        mDbHelper.deleteAllFromCategory(categoryId);
        notifyUIListeners();
    }

    public int getCategoriesCount() {
        return mCategories.size();
    }

    @Override
    public void sortTypeChanged(SortType newSortType, boolean completedAtTheEnd) {
        sortList(newSortType, completedAtTheEnd);
    }

    private void sortList(SortType aSortType, boolean complitedAtTheEnd) {
        for (Category c : mCategories) {
            c.getNotes().sortList(aSortType, complitedAtTheEnd);
        }
    }

    private static class HandlerExtension extends Handler {
        private final WeakReference<NotesList> mReference;

        HandlerExtension(NotesList list) {
            mReference = new WeakReference<>(list);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mReference.get() != null)
                mReference.get().notifyObservers();
        }
    }
}
