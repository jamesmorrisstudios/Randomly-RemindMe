package jamesmorrisstudios.com.randremind.listAdapters;

import android.support.annotation.NonNull;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;

/**
 * Container for achievement items in the achievemnt fragment
 *
 * Created by James on 3/31/2015.
 */
public final class ReminderContainer {
    //General
    public final boolean isHeader;

    //If Header
    public final String headerTitle;

    //Not header data
    public final ReminderItem item;

    /**
     * Constructor for header
     * @param headerTitle Title
     */
    public ReminderContainer(@NonNull String headerTitle) {
        this.isHeader = true;
        this.headerTitle = headerTitle;
        this.item = null;
    }

    /**
     * Constructor for normal item
     * @param item achievement item
     */
    public ReminderContainer(@NonNull ReminderItem item) {
        this.isHeader = false;
        this.item = item;
        this.headerTitle = null;
    }

}
