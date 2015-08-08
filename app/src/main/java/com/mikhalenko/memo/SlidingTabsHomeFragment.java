package com.mikhalenko.memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Observer;


public class SlidingTabsHomeFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private OnFragmentInteractionListener mListener;
    private static final String cstStateCurIndex = "currentTabIndex";

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(getResources().getString(R.string.title_home));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && mViewPager != null)
            outState.putInt(cstStateCurIndex, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_fragment_menu, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_home, container, false);
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

    @Override
    public void onPause() {
        Prefs prefs = new Prefs(getActivity());
        prefs.setLastCategoryIndex(mViewPager.getCurrentItem());
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new CategoryPageAdapter());

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        if (savedInstanceState != null)
            mViewPager.setCurrentItem(savedInstanceState.getInt(cstStateCurIndex, 0));
        else {
            Prefs prefs = new Prefs(getActivity());
            if (prefs.isNeedSaveLastCategory())
                mViewPager.setCurrentItem(prefs.getLastCategoryIndex());
        }
    }

    private long getNoteID(int position) {
        Category category = NotesList.get(getActivity()).getCategoriesList().
                get(mViewPager.getCurrentItem());
        return category.getNotes().get(position).getID();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_delete: {
                return actDelete(getNoteID(menuInfo.position));
            }
        }

        return super.onContextItemSelected(item);
    }

    private boolean actDelete(long noteId) {
        return NotesList.get(getActivity()).deleteNote(noteId);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.home_list_view_context_menu, menu);
    }

    private boolean actDeleteAll() {
        final Category category = NotesList.get(getActivity()).getCategoryByPos(mViewPager.getCurrentItem());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_msg_delete_all);
        builder.setPositiveButton(R.string.alert_btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotesList.get(getActivity()).deleteAllFromCategory(category.getID());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
        return true;
    }


    private void actEdit(long id) {
        FragmentManager manager = getFragmentManager();

        Category category = NotesList.get(getActivity()).getCategoriesList().get(mViewPager.getCurrentItem());
        Fragment fragment = EditItemFragment.newInstance(id, category.getID());

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(fragment.toString());
        transaction.replace(R.id.container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private void actAdd() {
        actEdit(-1);
    }

    private class CategoryPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return NotesList.get(getActivity()).getCategoriesList().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Category category = NotesList.get(getActivity()).getCategoriesList().get(position);
            if (category != null)
                return category.getName();
            else
                return "Empty";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Category category = NotesList.get(getActivity()).getCategoriesList().get(position);
            NotesListView view = new NotesListView(getActivity(), category);

            NotesList.get(getActivity()).addObserver(view);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    actEdit(getNoteID(position));
                }
            });

            registerForContextMenu(view);
            container.addView(view);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            NotesList.get(getActivity()).deleteObserver((Observer) object);
            container.removeView((View) object);
        }
    }

}
