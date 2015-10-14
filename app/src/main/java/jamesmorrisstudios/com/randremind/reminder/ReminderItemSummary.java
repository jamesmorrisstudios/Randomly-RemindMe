package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;

import java.util.ArrayList;

/**
 * Contains a subset of the reminder item data in order to display on the main list of reminders
 * It is not serializable
 *
 * Created by James on 9/29/2015.
 */
public class ReminderItemSummary extends BaseRecycleItem {
    public final String uniqueName;
    public final String title;
    public final boolean rangeTiming;
    public final boolean[] daysToRun;
    public final ArrayList<TimeItem> specificTimeList;
    public final TimeItem startTime;
    public final TimeItem endTime;

    public boolean enabled;

    public ReminderItemSummary(@NonNull ReminderItemData data) {
        this.uniqueName = data.uniqueName;
        this.title = data.title;
        this.rangeTiming = data.rangeTiming;
        this.daysToRun = data.daysToRun;
        this.specificTimeList = data.specificTimeList;
        this.startTime = data.startTime;
        this.endTime = data.endTime;
        this.enabled = data.enabled;
    }


}
