package jamesmorrisstudios.com.randremind.listAdapters;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderContainer;

import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderContainer extends BaseRecycleNoHeaderContainer {
    private EditReminderItem item;

    public EditReminderContainer(EditReminderItem item) {
        this.item = item;
    }

    @Override
    public BaseRecycleItem getItem() {
        return item;
    }

}
