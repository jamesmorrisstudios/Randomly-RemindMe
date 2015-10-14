package jamesmorrisstudios.com.randremind.reminder;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.jamesmorrisstudios.appbaselibrary.Serializer;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.filewriting.FileWriter;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import java.util.ArrayList;

/**
 * Created by James on 10/6/2015.
 */
public class BackupAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final String path;
    private final FileWriter.FileLocation location;
    private final boolean includeLog;
    private final BackupListener listener;
    private final BackupCompleteListener completeListener;

    public BackupAsyncTask(@NonNull String path, @NonNull FileWriter.FileLocation location, boolean includeLog, @NonNull BackupListener listener, @NonNull BackupCompleteListener completeListener) {
        this.path = path;
        this.location = location;
        this.includeLog = includeLog;
        this.listener = listener;
        this.completeListener = completeListener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ReminderListData backupList = getBackupList(includeLog);
        byte[] bytes = Serializer.serializeClass(backupList);
        return bytes != null && FileWriter.writeFile(path, bytes, location);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        listener.backupComplete(result, FileWriter.getFileUri(path, location));
        completeListener.backupComplete();
    }

    public interface BackupListener {
        void backupComplete(boolean success, Uri filePath);
    }

    public interface BackupCompleteListener {
        void backupComplete();
    }

    private ReminderListData getBackupList(boolean includeLog) {
        ReminderListData backupList = new ReminderListData();
        backupList.lastWake = UtilsTime.getDateTimeNow();
        backupList.version = Utils.getVersionName();
        backupList.reminderItemList = new ArrayList<>();

        ReminderItem reminderItem = new ReminderItem();

        for(ReminderItemBackupRestore item : ReminderList.getInstance().getBackupData()) {
            if(!item.selected) {
                continue;
            }
            ReminderItemData data = ReminderList.getInstance().getReminderData(item.uniqueName);
            if(data == null) {
                continue;
            }
            data = new ReminderItemData(data); //Create a deep copy
            if(includeLog) {
                reminderItem.setReminderItemData(data);
                reminderItem.loadReminderLogDataSync();
                reminderItem.clearReminderItemData();
            } else {
                data.reminderLog = null;
            }
            backupList.reminderItemList.add(data);
        }
        return backupList;
    }


}
