package com.mikhalenko.memo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import static com.mikhalenko.memo.NotesDatabaseHelper.NoteCursor;

public class NotesListView extends ListView implements Observer {

    private static final int cstFirstLoaderNotesId = 1000;
    private static final String cstArgsLoaderCatetoryId = "ArgsLoaderCatetoryId";

    private LoaderManager mLoaderManager;
    private long mCategoryID;
    private NoteListLoaderCallbacks mNoteListLoaderCallbacks;
//    private Handler mHandler;

    public NotesListView(Context context) {
        super(context);
    }

    public NotesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotesListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public NotesListView(Context context, int pos, LoaderManager loaderManager) {
        super(context);
//        mHandler = new HandlerExtension(this);
        assert (loaderManager != null);
        mLoaderManager = loaderManager;
        mNoteListLoaderCallbacks = new NoteListLoaderCallbacks();
        loadNotes(pos);
    }

    private boolean loadNotes(int pos) {
        Category category = NotesList.get(getContext()).getCategoryByPos(pos);
        if (category != null)
            return loadNotesById(category.getId());
        return false;
    }

    private void refreshList() {
        Log.d(Consts.LOGCAT_TAG, "In NotesListView.refreshList()");
        Bundle args = new Bundle();
        args.putLong(cstArgsLoaderCatetoryId, mCategoryID);
        mLoaderManager.restartLoader(getNotesLoaderId(), args, mNoteListLoaderCallbacks);
    }

    private int getNotesLoaderId() {
        return (int) (cstFirstLoaderNotesId + mCategoryID);
    }

    private boolean loadNotesById(long categoryID) {
        mCategoryID = categoryID;

        Bundle args = new Bundle();
        args.putLong(cstArgsLoaderCatetoryId, mCategoryID);

        mLoaderManager.initLoader(getNotesLoaderId(), args, mNoteListLoaderCallbacks);
        return true;
    }

    @Override
    public void update(Observable observable, Object data) {
        refreshList();
    }

    public class NoteCursorAdapter extends CursorAdapter {


        public NoteCursorAdapter(Context context, NoteCursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.list_item_note, null);

            NoteViewHolder holder = new NoteViewHolder();
            holder.mTvTitle =  (TextView) view.findViewById(R.id.note_list_item_title);
            holder.mTvDesc = (TextView) view.findViewById(R.id.note_list_item_description);
            holder.mTvDate = (TextView) view.findViewById(R.id.note_list_item_date);
            holder.mCbComplited = (CheckBox) view.findViewById(R.id.note_list_item_cbComplited);

            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            if (cursor.isClosed())
                return;
            SingleNote note = ((NoteCursor) cursor).getNote();

            final NoteViewHolder holder = (NoteViewHolder) view.getTag();

            holder.mCbComplited.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleNote note = NotesList.get(context).getNote(holder.mId);
                    if (note == null)
                        return;

                    note.setCompleted(!note.isCompleted());
                    NotesList.get(context).insertOrUpdate(note);
                    updateComplited(holder, note.isCompleted());
                }
            });


            holder.mId = note.getId();
            String title = note.getSingleLineTitle();
            holder.mTvTitle.setText(title);
            holder.mTvDesc.setText(note.getSingleLineDescription());

            holder.mTvDate.setText(note.getStringDate());

            updateComplited(holder, note.isCompleted());
        }

        private void updateComplited(NoteViewHolder holder, boolean aIsComplited) {
            int flags = holder.mTvTitle.getPaintFlags();
            if (aIsComplited)
                flags = flags | Paint.STRIKE_THRU_TEXT_FLAG;
            else
                flags = flags & (~Paint.STRIKE_THRU_TEXT_FLAG);

            holder.mTvTitle.setPaintFlags(flags);
            holder.mTvDesc.setPaintFlags(flags);
            holder.mCbComplited.setChecked(aIsComplited);
        }

        private class NoteViewHolder {
            public long mId;
            public TextView mTvTitle;
            public TextView mTvDesc;
            public TextView mTvDate;
            public CheckBox mCbComplited;

            NoteViewHolder() {
                mId = -1;
            }
        }

    }

//    private class HandlerExtension extends Handler {
//        private final WeakReference<NotesListView> mNotesListView;
//
//        HandlerExtension(NotesListView listView) {
//            mNotesListView = new WeakReference<>(listView);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            NotesListView currentListView = mNotesListView.get();
//            if (currentListView == null)
//                return;
//            currentListView.refreshList();
//        }
//    }

    private class NoteListLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long categoryId = 1;
            if (args != null)
                categoryId = args.getLong(cstArgsLoaderCatetoryId);
            return new NoteListCursorLoader(getContext(), categoryId);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            NoteCursorAdapter adapter = (NoteCursorAdapter) getAdapter();
            if (adapter == null) {
                adapter = new NoteCursorAdapter(getContext(), (NoteCursor) data);
                setAdapter(adapter);
            }
            else
                adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            NoteCursorAdapter adapter = (NoteCursorAdapter) getAdapter();
            if (adapter != null)
                adapter.swapCursor(null);
        }
    }


}