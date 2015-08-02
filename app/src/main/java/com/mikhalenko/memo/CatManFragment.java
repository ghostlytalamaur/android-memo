package com.mikhalenko.memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.mikhalenko.memo.NotesDatabaseHelper.CategoryCursor;

import java.util.Observable;
import java.util.Observer;

import static android.widget.AdapterView.AdapterContextMenuInfo;

public class CatManFragment extends ListFragment implements Observer {

    private static final int LOADER_CATEGORIES = 1;
    private OnFragmentInteractionListener mListener;
    private CategoryListLoaderCallbacks mCategoryListLoaderCallbacks;


    public static CatManFragment newInstance() {
        CatManFragment fragment = new CatManFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CatManFragment() {
    }

    @Override
    public void onDestroyView() {
        NotesList.get(getActivity()).deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_manager, container, false);

        getLoaderManager().initLoader(LOADER_CATEGORIES, null, mCategoryListLoaderCallbacks);
        NotesList.get(getActivity()).addObserver(this);
        return view;
    }

    private void editCategory(final long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText edt = new EditText(getActivity());

        if (id != -1) {
            Category category = NotesList.get(getActivity()).getCategory(id);
            edt.setText(category.getName());
        }

        builder.setView(edt);
        builder.setTitle(R.string.title_dialog_add_new_category);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edt.getText().toString();
                if (!name.isEmpty()) {
                    Category category = new Category(id, name);
                    NotesList.get(getActivity()).addOrUpdateCategory(category);
                    Log.d(Consts.LOGCAT_TAG, "in editCategory -> positive button clicked");
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
        Log.d(Consts.LOGCAT_TAG, "in editCategory -> dialog shows");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryListLoaderCallbacks = new CategoryListLoaderCallbacks();

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        if (mListener != null)
            mListener.onFragmentInteraction(getResources().getString(R.string.title_category_manager));
        refreshList();
        super.onResume();
    }

    private void refreshList() {
        Log.d(Consts.LOGCAT_TAG, "In CatManFragment.refreshList()");
        getLoaderManager().restartLoader(LOADER_CATEGORIES, null, mCategoryListLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.catman_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_category:
                editCategory(-1);
                return true;
//            case R.id.action_delete_all_categories:
//                return actDeleteAll();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean actDeleteAll() {
/*
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
*/
        return false;
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
        MenuItem item = menu.findItem(R.id.menu_delete);
        if (item != null)
            item.setEnabled(((AdapterContextMenuInfo) menuInfo).id != 1);
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

    private boolean actDelete(final long categoryId) {
        int count = NotesList.get(getActivity()).getNotesInCategory(categoryId);

        if (count > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String msg = getString(R.string.alert_msg_delete_all_in_category);

            Category category =  NotesList.get(getActivity()).getCategory(categoryId);
            builder.setMessage(String.format(msg, category.getName(), count));
            builder.setPositiveButton(R.string.alert_btn_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NotesList.get(getActivity()).deleteCategory(categoryId);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();

        } else
            return NotesList.get(getActivity()).deleteCategory(categoryId);
        return false;
    }

    private void actEdit(long id) {
        editCategory(id);
    }

    @Override
    public void update(Observable observable, Object data) {
        refreshList();
    }

    private class CategoryListLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CategoriesListCursorLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            CategoryCursorAdapter adapter = (CategoryCursorAdapter) getListAdapter();
            if (adapter == null) {
                adapter = new CategoryCursorAdapter(getActivity(), (CategoryCursor) data);
                setListAdapter(adapter);
            }
            else
                adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            setListAdapter(null);
        }
    }

    private class CategoryCursorAdapter extends CursorAdapter {

        public CategoryCursorAdapter(Context context, CategoryCursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.list_item_catman, null);

            CategoryViewHolder holder = new CategoryViewHolder();
            holder.mTvTitle =  (TextView) view.findViewById(R.id.catman_list_title);
            holder.mTvCount = (TextView) view.findViewById(R.id.catman_list_notes_count);

            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            CategoryCursor categoryCursor = (CategoryCursor) cursor;
            if (categoryCursor.isClosed())
                return;
            Category category = categoryCursor.getCategory();

            final CategoryViewHolder holder = (CategoryViewHolder) view.getTag();
            holder.mId = category.getId();
            holder.mTvTitle.setText(category.getName());
            holder.mTvCount.setText(String.valueOf(
                    NotesList.get(context).getNotesInCategory(category.getId()))
            );
        }
    }
    private class CategoryViewHolder {
        public long mId;
        public TextView mTvTitle;
        public TextView mTvCount;

        CategoryViewHolder() {
            mId = -1;
        }
    }

}
