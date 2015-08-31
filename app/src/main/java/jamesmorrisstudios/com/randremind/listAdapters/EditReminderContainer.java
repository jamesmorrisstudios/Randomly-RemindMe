package jamesmorrisstudios.com.randremind.listAdapters;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleContainer;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;

import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderContainer extends BaseRecycleContainer {
    private EditReminderItem item;

    public EditReminderContainer(EditReminderItem item) {
        super(false);
        this.item = item;
    }

    @Override
    public BaseRecycleItem getHeader() {
        return null;
    }

    @Override
    public BaseRecycleItem getItem() {
        return item;
    }

}
