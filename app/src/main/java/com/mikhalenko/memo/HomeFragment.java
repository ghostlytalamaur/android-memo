package com.mikhalenko.memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


import static android.widget.AdapterView.AdapterContextMenuInfo;
import static com.mikhalenko.memo.NotesDatabaseHelper.NoteCursor;

public class HomeFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, NotesList.INotesListener {

    private OnFragmentInteractionListener mListener;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public void onDestroy() {
        NotesList.get(getActivity()).unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
        NotesList.get(getActivity()).registerListener(this);
    }

    @Override
    public void onResume() {
        if (mListener != null)
            mListener.onFragmentInteraction(getResources().getString(R.string.title_home));
        refreshList();
        super.onResume();
    }

    private void refreshList() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                actAdd();
                return true;
            case R.id.action_delete_all:
                return actDeleteAll();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean actDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_msg_delete_all);
        builder.setPositiveButton(R.string.alert_btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotesList.get(getActivity()).deleteAll();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        registerForContextMenu(getListView());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        actEdit(id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.home_list_view_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                return actDelete(menuInfo.id);
        }

        return super.onContextItemSelected(item);

    }

    private boolean actDelete(long noteId) {
        return NotesList.get(getActivity()).deleteNote(noteId);
    }

    private void actEdit(long id) {
        FragmentManager manager = getFragmentManager();
        Fragment fragment = EditItemFragment.newInstance(id);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void actAdd() {
        actEdit(-1);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new NoteListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        NoteCursorAdapter adapter = new NoteCursorAdapter(getActivity(), (NoteCursor) data);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    @Override
    public void onNotesListEvent(NotesList.ListenerEvents event) {
        refreshList();
    }


    private static class NoteCursorAdapter extends CursorAdapter {

        private NoteCursor mNoteCursor;

        public NoteCursorAdapter(Context context, NoteCursor cursor) {
            super(context, cursor, 0);
            mNoteCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.list_item_note, null);

            ViewHolder holder = new ViewHolder();
            holder.mTvTitle =  (TextView) view.findViewById(R.id.note_list_item_title);
            holder.mTvDesc = (TextView) view.findViewById(R.id.note_list_item_description);
            holder.mTvDate = (TextView) view.findViewById(R.id.note_list_item_date);
            holder.mCbComplited = (CheckBox) view.findViewById(R.id.note_list_item_cbComplited);

            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            SingleNote note = mNoteCursor.getNote();

            final ViewHolder holder = (ViewHolder) view.getTag();

            holder.mCbComplited.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleNote note = NotesList.get(context).getNote(holder.mId);
                    if (note == null)
                        return;
                    boolean isChecked = !note.isCompleted();
                    note.setCompleted(isChecked);
                    NotesList.get(context).insertOrUpdate(note);
                }
            });


            holder.mId = note.getId();
            holder.mTvTitle.setText(note.getSingleLineTitle());
            holder.mTvDesc.setText(note.getSingleLineDescription());

            holder.mTvDate.setText(note.getStringDate());
            holder.mCbComplited.setChecked(note.isCompleted());
            int flags = holder.mTvTitle.getPaintFlags();
            if (note.isCompleted())
                flags = flags | Paint.STRIKE_THRU_TEXT_FLAG;
            else
                flags = flags & (~Paint.STRIKE_THRU_TEXT_FLAG);

            holder.mTvTitle.setPaintFlags(flags);
            holder.mTvDesc.setPaintFlags(flags);

        }
    }

    private static class ViewHolder {
        public long mId;
        public TextView mTvTitle;
        public TextView mTvDesc;
        public TextView mTvDate;
        public CheckBox mCbComplited;

        ViewHolder() {
            mId = -1;
        }
    }
}
