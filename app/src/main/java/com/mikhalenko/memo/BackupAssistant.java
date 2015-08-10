package com.mikhalenko.memo;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by mihal on 09.08.2015.
 */
public class BackupAssistant {

    private static final String BACKUP_FOLDER_NAME = "MemoBackups";

    private boolean doTransferFile(String aSourcePath, String aDestPath) {
        try{
            File sourceFile = new File(aSourcePath);
            File destFile = new File(aDestPath);

            FileChannel src = new FileInputStream(sourceFile).getChannel();
            FileChannel dst = new FileOutputStream(destFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getDBPath() {
        File data = Environment.getDataDirectory();
        return data.getPath() + "/data/com.mikhalenko.memo/" + "databases/" +
                NotesDatabaseHelper.DATABASE_NAME;
    }

    private String getBackupFolderPath() {
        File sd = Environment.getExternalStorageDirectory();
        if (!sd.canWrite() || !sd.canRead())
            return null;

        File destFile = new File(sd.getPath() + "/" + BACKUP_FOLDER_NAME);
        destFile.mkdirs();
        return destFile.getPath();
    }

    private boolean backupRestore(boolean isBackup) {
        String backupPath = getBackupFolderPath();
        if (backupPath == null)
            return false;

        if (isBackup)
            return doTransferFile(getDBPath(), backupPath + "/" + NotesDatabaseHelper.DATABASE_NAME);
        else
            return doTransferFile(backupPath + "/" + NotesDatabaseHelper.DATABASE_NAME, getDBPath());
    }

    public boolean BackupDB() {
        return backupRestore(true);
    }

    public boolean RestoreDB() {
        return backupRestore(false);
    }
}
