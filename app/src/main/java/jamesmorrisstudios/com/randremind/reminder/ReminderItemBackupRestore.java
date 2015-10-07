package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;

/**
 * Created by James on 10/5/2015.
 */
public class ReminderItemBackupRestore {
    public final String uniqueName;
    public final String title;
    public boolean selected = true;

    public ReminderItemBackupRestore(@NonNull ReminderItemData data) {
        this.uniqueName = data.uniqueName;
        this.title = data.title;
    }
}
