package com.mikhalenko.memo;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private CharSequence mTitle;
    private NavigationView mNavigationDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager.OnBackStackChangedListener mBackStackChangedListener;
    private String mCurMenuItemTitle;

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawer = (NavigationView) findViewById(R.id.navigation);

        setupNavigationDrawer();
        mBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                syncArrowState();
            }
        };
        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
        setupActionBar();
        restoreActionBar();
        onNavigationItemSelected(mNavigationDrawer.getMenu().getItem(0));
        displayView(getString(R.string.menu_home));
    }

    private void setupNavigationDrawer() {
        mNavigationDrawer.setNavigationItemSelectedListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        )
        {
            @Override
            public void onDrawerClosed(View drawerView) {
                syncArrowState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer))
            mDrawerLayout.closeDrawer(mNavigationDrawer);

        if (displayView(menuItem.getTitle().toString()))
            menuItem.setChecked(true);
        return true;
    }

    private boolean displayView(String title) {
        Fragment curFragment;

        if (title.equals(mCurMenuItemTitle))
            return false;
        mCurMenuItemTitle = title;
        if (title.equals(getString(R.string.menu_home)))
            curFragment = new SlidingTabsHomeFragment();
        else if (title.equals(getString(R.string.menu_catman)))
            curFragment = CatManFragment.newInstance();
        else if (title.equals(getString(R.string.menu_about)))
            curFragment = AboutFragment.newInstance();
        else if (title.equals(getString(R.string.menu_settings))) {
//            startActivity(new Intent(this, PrefsActivity.class));
            curFragment = new PrefsFragment();
//            return false;
        } else
            curFragment = null;

        if (curFragment == null)
            return false;

        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        replaceFragment(curFragment, false);

        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                return mDrawerToggle.isDrawerIndicatorEnabled() &&
                        mDrawerToggle.onOptionsItemSelected(item) ||
                        getSupportFragmentManager().popBackStackImmediate() ||
                        super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String s){
        mTitle = s;
        restoreActionBar();
        hideIME();
    }

    @Override
    public void replaceFragment(Fragment aFragment, boolean aNeedAddToBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (aNeedAddToBackStack)
            transaction.addToBackStack(null);
        transaction.replace(R.id.container, aFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private void syncArrowState() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        mDrawerToggle.setDrawerIndicatorEnabled(backStackCount == 0 || mDrawerLayout.isDrawerOpen(mNavigationDrawer));
    }

    private void hideIME() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
