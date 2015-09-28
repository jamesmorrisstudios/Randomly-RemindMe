package jamesmorrisstudios.com.randremind.dialogHelper;

import android.support.annotation.NonNull;

import jamesmorrisstudios.com.randremind.reminder.ReminderLogDay;

/**
 * Created by James on 9/15/2015.
 */
public class ReminderLogRequest {
    public final ReminderLogDay reminderLogDay;

    public ReminderLogRequest(@NonNull ReminderLogDay reminderLogDay) {
        this.reminderLogDay = reminderLogDay;
    }

}
