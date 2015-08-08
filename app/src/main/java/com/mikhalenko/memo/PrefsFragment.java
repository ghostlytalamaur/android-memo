package com.mikhalenko.memo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mihal on 08.08.2015.
 */
public class PrefsFragment extends PreferenceFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.preferences);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
