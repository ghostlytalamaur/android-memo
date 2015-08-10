package com.mikhalenko.memo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PrefsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_prefs);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            addPreferencesFromResource(R.xml.preferences);

            View v = super.onCreateView(inflater, container, savedInstanceState);

            Button btnRestore = new Button(v.getContext());
            btnRestore.setId(R.id.btnRestore);
            btnRestore.setText(R.string.btn_restore);
            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackupAssistant assistant = new BackupAssistant();
                    String msg;
                    if (assistant.RestoreDB()) {
                        msg = "Restore complited";
                        NotesList.get(getActivity()).dataChanged();
                    }
                    else
                        msg = "Restore failed";
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            });

            Button btnBackup = new Button(v.getContext());
            btnBackup.setId(R.id.btnBackup);
            btnBackup.setText(R.string.btn_backup);
            btnBackup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackupAssistant assistant = new BackupAssistant();
                    String msg;
                    if (assistant.BackupDB())
                        msg = "Backup complited";
                    else
                        msg = "Backup failed";
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            });

            ((ListView) (v.findViewById(android.R.id.list))).addHeaderView(btnBackup);
            ((ListView) (v.findViewById(android.R.id.list))).addHeaderView(btnRestore);
            return v;
//            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

}
