package jamesmorrisstudios.com.randremind.editReminder;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;

import jamesmorrisstudios.com.randremind.listAdapters.EditReminderViewHolder;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderItem extends BaseRecycleItem {
    public boolean visible = true;
    public String text = "";
    public EditReminderViewHolder.EditReminderPage page;

    public EditReminderItem(String text, EditReminderViewHolder.EditReminderPage page) {
        this.text = text;
        this.page = page;
    }
}
