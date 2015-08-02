package com.mikhalenko.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String NOTES_TABLE = "notes_table";
    private static final String NOTE_ID = "_id";
    private static final String NOTE_TITLE = "title";
    private static final String NOTE_DESCRIPTION = "description";
    private static final String NOTE_DATE = "timestamp";
    private static final String NOTE_STATUS = "complited";
    private static final String NOTE_CATEGORY = "category";

    private static final String CATEGORY_TABLE = "categories_table";
    private static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_NAME = "name";


    NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");

        String createCategoryTable = "CREATE TABLE " + CATEGORY_TABLE + " ("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_NAME + " TEXT NOT NULL"
                + ");";
        db.execSQL(createCategoryTable);

        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, "Unknown");
        db.insert(CATEGORY_TABLE, null, cv); // Add "Unknown" category with _id = 1

        final String createNoteTable = "CREATE TABLE " + NOTES_TABLE + " ("
                + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOTE_TITLE + " TEXT, "
                + NOTE_DESCRIPTION + " TEXT, "
                + NOTE_DATE + " LONG NOT NULL, "
                + NOTE_STATUS + " INTEGER NOT NULL, "
                + NOTE_CATEGORY + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + NOTE_CATEGORY + ") REFERENCES " +
                        CATEGORY_TABLE + " (" + CATEGORY_ID + ")"
                + " ON DELETE CASCADE ON UPDATE CASCADE);";
        db.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertOrUpdateNote(SingleNote note) {
        ContentValues cv = new ContentValues();
        cv.put(NOTE_TITLE, note.getTitle());
        cv.put(NOTE_DESCRIPTION, note.getDescription());
        cv.put(NOTE_DATE, note.getDate());
        cv.put(NOTE_STATUS, note.isCompleted());
        cv.put(NOTE_CATEGORY, note.getCategoryID());

        long id = note.getId();
        if (queryNote(id) == null)
            id = getWritableDatabase().insert(NOTES_TABLE, null, cv);
        else
            id = getWritableDatabase().update(NOTES_TABLE, cv, NOTE_ID + "=?",
                    new String[] { id + "" });
        note.setId(id);
        return id >= 0;
    }

    public boolean insertOrUpdateCategory(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, category.getName());

        long id = category.getId();

        if (queryCategory(id) == null) {
            id = getWritableDatabase().insert(CATEGORY_TABLE, null, cv);
            category.setId(id);
        }
        else
            id = getWritableDatabase().update(CATEGORY_TABLE, cv, CATEGORY_ID + "=?",
                    new String[]{category.getId() + ""});
        return id >= 0;
    }

    public boolean deleteCategory(long id) {
        return (getWritableDatabase().delete(CATEGORY_TABLE, CATEGORY_ID + "=?",
                new String[] { String.valueOf(id) })) > 0;
    }


    public boolean deleteNote(long id) {
        return (getWritableDatabase().delete(NOTES_TABLE, NOTE_ID + "=?",
                new String[] { String.valueOf(id) })) > 0;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(NOTES_TABLE, null, null);
    }

    public SingleNote queryNote(long id) {
        Cursor wrapped = getReadableDatabase().query(NOTES_TABLE,
                null,
                NOTE_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        NoteCursor c = new NoteCursor(wrapped);
        c.moveToFirst();
        SingleNote note = c.getNote();

        c.close();
        return note;
    }

    public Category queryCategory(long id) {
        Cursor wrapped = getReadableDatabase().query(CATEGORY_TABLE,
                null,
                CATEGORY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null, "1");
        CategoryCursor c = new CategoryCursor(wrapped);
        c.moveToFirst();
        Category category = c.getCategory();
        c.close();
        return category;
    }

    public NoteCursor queryNotes() {
        Cursor cursor = getReadableDatabase().query(NOTES_TABLE,
                null, null, null, null, null, NOTE_ID + " asc");
        return new NoteCursor(cursor);
    }

    public NoteCursor queryNotesFromCategory(long categoryId) {
        Cursor cursor = getReadableDatabase().query(NOTES_TABLE,
                null,
                NOTE_CATEGORY +" = ?",
                new String[]{String.valueOf(categoryId)},
                null, null, NOTE_ID + " asc");
        return new NoteCursor(cursor);
    }

    public CategoryCursor queryCategories() {
        Cursor cursor = getReadableDatabase().query(CATEGORY_TABLE,
                null, null, null, null, null, CATEGORY_ID + " asc");
        return new CategoryCursor(cursor);
    }

    public Category queryCategoryByPos(int pos) {
        if (pos < 0)
            return null;
        CategoryCursor cursor = queryCategories();
        if (pos >= cursor.getCount())
            return null;
        cursor.moveToPosition(pos);
        Category category = cursor.getCategory();
        cursor.close();
        return category;
    }

    public int deleteAllFromCategory(long categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(NOTES_TABLE, NOTE_CATEGORY + " = ?", new String[] {String.valueOf(categoryId)});
    }

    public static class CategoryCursor extends CursorWrapper {

        public CategoryCursor(Cursor cursor) {
            super(cursor);
        }

        public Category getCategory() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            long id = getLong(getColumnIndex(CATEGORY_ID));
            String name = getString(getColumnIndex(CATEGORY_NAME));

            return new Category(id, name);
        }
    }

    public static class NoteCursor extends CursorWrapper {

        public NoteCursor(Cursor cursor) {
            super(cursor);
        }

        public SingleNote getNote() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            SingleNote note = new SingleNote();

            long id = getLong(getColumnIndex(NOTE_ID));
            String title = getString(getColumnIndex(NOTE_TITLE));
            String description = getString(getColumnIndex(NOTE_DESCRIPTION));
            long timestamp = getLong(getColumnIndex(NOTE_DATE));
            boolean isComplited = getInt(getColumnIndex(NOTE_STATUS)) == 1;
            long categoryId = getLong(getColumnIndex(NOTE_CATEGORY));

            note.setId(id);
            note.setTitle(title);
            note.setDescription(description);
            note.setDate(timestamp);
            note.setCompleted(isComplited);
            note.setCategoryID(categoryId);
            return note;
        }
    }
}
