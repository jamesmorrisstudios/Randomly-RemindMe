package jamesmorrisstudios.com.randremind.listAdapters;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;

/**
 * Created by James on 4/29/2015.
 */
public class ReminderListItem extends BaseRecycleItem {
    public ReminderItem reminder;

    public ReminderListItem(ReminderItem reminder) {
        this.reminder = reminder;
    }
}
