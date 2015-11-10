package jamesmorrisstudios.com.randremind.editReminder;

import android.view.View;

/**
 * Created by James on 11/3/2015.
 */
public abstract class BaseEditReminder {
    protected final View parent;

    public BaseEditReminder(View parent) {
        this.parent = parent;
    }

    public abstract void bindItem(EditReminderItem item, boolean showAdvanced);

}
