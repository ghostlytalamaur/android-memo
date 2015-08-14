package com.mikhalenko.memo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.machinarius.preferencefragment.PreferenceFragment;


public class PrefsFragment extends PreferenceFragment {
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        addPreferencesFromResource(R.xml.preferences);
        return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    }
}
