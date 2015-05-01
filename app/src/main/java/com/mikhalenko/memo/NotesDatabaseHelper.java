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

    private static final String NOTES_TANBLE = "notes_table";
    private static final String NOTE_ID = "_id";
    private static final String NOTE_TITLE = "title";
    private static final String NOTE_DESCRIPTION = "description";
    private static final String NOTE_DATE = "timestamp";
    private static final String NOTE_STATUS = "complited";

    NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createDB = "CREATE TABLE " + NOTES_TANBLE + " ("
                + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOTE_TITLE + " TEXT, "
                + NOTE_DESCRIPTION + " TEXT, "
                + NOTE_DATE + " LONG NOT NULL, "
                + NOTE_STATUS + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean updateExistingNote(SingleNote note) {
        ContentValues cv = new ContentValues();
        cv.put(NOTE_TITLE, note.getTitle());
        cv.put(NOTE_DESCRIPTION, note.getDescription());
        cv.put(NOTE_DATE, note.getDate());
        cv.put(NOTE_STATUS, note.isCompleted());
        long id = getWritableDatabase().update(NOTES_TANBLE, cv, NOTE_ID + "=?",
                new String[] { note.getId() + "" });
        return id >= 0;
    }

    public long insertNewNote(SingleNote note) {
        ContentValues cv = new ContentValues();
        cv.put(NOTE_TITLE, note.getTitle());
        cv.put(NOTE_DESCRIPTION, note.getDescription());
        cv.put(NOTE_DATE, note.getDate());
        cv.put(NOTE_STATUS, note.isCompleted());

        long id = getWritableDatabase().insert(NOTES_TANBLE, null, cv);
        return id;
    }

    public boolean deleteNote(long id) {
        boolean isDeleted = (getWritableDatabase().delete(NOTES_TANBLE, NOTE_ID + "=?",
                new String[] { String.valueOf(id) })) > 0;
        return isDeleted;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(NOTES_TANBLE, null, null);
    }

    public NoteCursor queryNote(long id) {
        Cursor wrapped = getReadableDatabase().query(NOTES_TANBLE,
                null,
                NOTE_ID + " = ?",
                new String[]{ String.valueOf(id) },
                null,
                null,
                null,
                "1");
        return new NoteCursor(wrapped);
    }

    public NoteCursor queryNotes() {
        Cursor cursor = getReadableDatabase().query(NOTES_TANBLE,
                null, null, null, null, null, NOTE_ID + " asc");
        return new NoteCursor(cursor);
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

            note.setId(id);
            note.setTitle(title);
            note.setDescription(description);
            note.setDate(timestamp);
            note.setCompleted(isComplited);
            return note;
        }
    }
}
