package jamesmorrisstudios.com.randremind.editReminder;

import android.view.View;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 11/3/2015.
 */
public class EditReminderTrigger {

    public EditReminderTrigger(View parent) {

    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }

    }


}
