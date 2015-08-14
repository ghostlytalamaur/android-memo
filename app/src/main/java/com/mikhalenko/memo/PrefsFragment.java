package com.mikhalenko.memo;

import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.machinarius.preferencefragment.PreferenceFragment;


public class PrefsFragment extends PreferenceFragment {
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        addPreferencesFromResource(R.xml.preferences);

        Preference backup = findPreference(getString(R.string.pref_backup_key));
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                doBackup();
                return true;
            }
        });
        Preference restore = findPreference(getString(R.string.pref_restore_key));
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                doRestore();
                return true;
            }
        });

        return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    }

    private void doRestore() {
        BackupAssistant assistant = new BackupAssistant();
        assistant.RestoreDB();
        NotesList.get(getActivity()).dataChanged();
    }

    private void doBackup() {
        BackupAssistant assistant = new BackupAssistant();
        assistant.BackupDB();
    }
}
