package jamesmorrisstudios.com.randremind.editReminder;

import android.widget.RelativeLayout;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderAlarm {


    public EditReminderAlarm(RelativeLayout parent) {

    }

    public final void bindItem(EditReminderItem item) {
        final ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
        if(reminderItem == null) {
            return;
        }

    }

}
