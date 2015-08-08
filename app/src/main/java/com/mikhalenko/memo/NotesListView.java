package com.mikhalenko.memo;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;


public class NotesListView extends ListView implements Observer {


    public NotesListView(Context context) {
        super(context);
    }

    public NotesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotesListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotesListView(Context aContext, Category aCategory) {
        super(aContext);
        if (aCategory != null)
            setAdapter(new NotesAdapter(getContext(), aCategory));
    }

    private void refreshList() {
        Log.d(Consts.LOGCAT_TAG, "In NotesListView.refreshList()");
        if (getAdapter() == null)
            return;
        ((NotesAdapter) getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        refreshList();
    }

    public class NotesAdapter extends ArrayAdapter<SingleNote> {
        private final Category mCategory;

        public NotesAdapter(Context aContext, Category aCategory) {
            super(aContext, 0, aCategory.getNotes());
            mCategory = aCategory;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mCategory == null)
                return null;

            View view;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.list_item_note, parent, false);
            }
            else
                view = convertView;

            final NoteViewHolder holder = new NoteViewHolder();

            holder.mTvTitle =  (TextView) view.findViewById(R.id.note_list_item_title);
            holder.mTvDesc = (TextView) view.findViewById(R.id.note_list_item_description);
            holder.mTvDate = (TextView) view.findViewById(R.id.note_list_item_date);
            holder.mCbComplited = (CheckBox) view.findViewById(R.id.note_list_item_cbComplited);

            view.setTag(holder);

            holder.mCbComplited.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleNote note = NotesList.get(getContext()).getNote(holder.mId);
                    if (note == null)
                        return;

                    note.setCompleted(!note.isCompleted());
                    NotesList.get(getContext()).insertOrUpdate(note);
                    updateComplited(holder, note.isCompleted());
                }
            });


            SingleNote note = mCategory.getNotes().get(position);
            holder.mId = note.getID();
            String title = note.getSingleLineTitle();
            holder.mTvTitle.setText(title);
            holder.mTvDesc.setText(note.getSingleLineDescription());

            holder.mTvDate.setText(note.getStringDate());

            updateComplited(holder, note.isCompleted());

            return view;
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

}