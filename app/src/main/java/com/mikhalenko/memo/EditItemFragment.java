package com.mikhalenko.memo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemFragment extends Fragment {
    private static final String EXTRA_NOTE_ID = "com.mikhalenko.test.extra_note_id";
    private static final String EXTRA_CATEGORY_ID = "com.mikhalenko.test.extra_category_id";
    private static final String SAVED_NOTE =  "com.mikhalenko.test.saved_note";
    private static final String DIALOG_DATE = "date_dialog";

    private static final int REQUEST_DATE = 0;
    private static final int LOADER_NOTE_LOAD = 10;
    private static final int LOADER_NOTE_SAVE = 11;
    private SingleNote mNote;
    private EditText mEdtTitle;
    private EditText mEdtDesc;
    private Button mBtnDate;
    private Menu mMenu;
    private OnFragmentInteractionListener mListener;
    private Prefs mPrefs;

    public EditItemFragment() {
    }

    public static EditItemFragment newInstance(long id, long categoryId) {
        EditItemFragment f = new EditItemFragment();

        Bundle args = new Bundle();
        args.putLong(EXTRA_NOTE_ID, id);
        args.putLong(EXTRA_CATEGORY_ID , categoryId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_NOTE, mNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long noteId = getArguments().getLong(EXTRA_NOTE_ID, -1);

        if ((noteId != -1) && (savedInstanceState == null)) {
            Bundle args = new Bundle();
            args.putLong(EXTRA_NOTE_ID, noteId);
            LoaderManager lm = getLoaderManager();
            lm.initLoader(LOADER_NOTE_LOAD + (int) noteId, args, new NoteLoaderCallbacks());
        } else if ((noteId == -1) && (savedInstanceState == null)) {
            mNote = new SingleNote();
            mNote.setCategoryID(getArguments().getLong(EXTRA_CATEGORY_ID, 1));
        }
        else
            mNote = (SingleNote) savedInstanceState.getSerializable(SAVED_NOTE);
        setHasOptionsMenu(true);

        mPrefs = new Prefs(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);
        mEdtTitle = (EditText) view.findViewById(R.id.edit_title);
        mEdtDesc = (EditText) view.findViewById(R.id.edit_description);

        mBtnDate = (Button) view.findViewById(R.id.btnDate);

        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dlg = DatePickerFragment.newInstance(mNote.getDate());
                dlg.setTargetFragment(EditItemFragment.this, REQUEST_DATE);
                dlg.show(fm, DIALOG_DATE);
            }
        });

        updateUI();

        return view;
    }

    private void fillNote() {
        mNote.setTitle(mEdtTitle.getText().toString());
        mNote.setDescription(mEdtDesc.getText().toString());
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
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        if (mPrefs.isAutoSaveNotEmpty())
            actSave();
        super.onPause();
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    private void updateUI() {
        if (mNote != null) {
            mEdtTitle.setText(mNote.getTitle());
            mEdtDesc.setText(mNote.getDescription());
            mBtnDate.setText(mNote.getStringDate());
        }
        if (mListener != null) {
            String title;
            if ((mNote == null) || (mNote.getID() == -1))
                title = getResources().getString(R.string.title_note_new);
            else
                title = getResources().getString(R.string.title_note_edit);
            mListener.onFragmentInteraction(title);
        }
        enableMenuItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AppCompatActivity.RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_DATE:
                mNote.setDate(data.getLongExtra(DatePickerFragment.EXTRA_DATE, 0));
                fillNote();
                updateUI();
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mMenu = menu;
        enableMenuItems();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_item_fragment_menu, menu);
    }

    private void enableMenuItems() {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_delete);
            if (item != null) item.setVisible((mNote != null) && (mNote.getID() != -1));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                actSave();
                break;
            case R.id.action_delete:
                actDelete();
                break;
        }
        return true;
    }


    private void actDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_msg_delete);
        builder.setPositiveButton(R.string.alert_btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (NotesList.get(getActivity()).deleteNote(mNote.getID()))
                    getFragmentManager().popBackStackImmediate();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void actSave() {
        if (mNote == null)
            return;
        fillNote();

        Bundle args = new Bundle();
        args.putSerializable(SAVED_NOTE, mNote);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_NOTE_SAVE, args, new NoteSaverCallbacks());
    }

    private void notifySaved(boolean isSaved) {
        String msg;
        if (isSaved)
            msg = "Note saved!";
        else
            msg = "Error saving note!";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private class NoteSaverCallbacks implements LoaderManager.LoaderCallbacks<SingleNote> {

        @Override
        public Loader<SingleNote> onCreateLoader(int id, Bundle args) {
            return new NoteSaver(getActivity(), (SingleNote) args.getSerializable(SAVED_NOTE));
        }

        @Override
        public void onLoadFinished(Loader<SingleNote> loader, SingleNote data) {
            mNote = data;
            notifySaved(data != null);
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<SingleNote> loader) {

        }
    }

    private class NoteLoaderCallbacks implements LoaderManager.LoaderCallbacks<SingleNote> {

        @Override
        public Loader<SingleNote> onCreateLoader(int id, Bundle args) {
            return new NoteLoader(getActivity(), args.getLong(EXTRA_NOTE_ID));
        }

        @Override
        public void onLoadFinished(Loader<SingleNote> loader, SingleNote note) {
            mNote = note;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<SingleNote> loader) {
        }
    }


}
