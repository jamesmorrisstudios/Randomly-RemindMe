package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;

import java.util.ArrayList;

/**
 * Created by James on 4/30/2015.
 */
public class ReminderLogDay extends BaseRecycleItem {
    @SerializedName("timesShown") //Depreciated
    public ArrayList<TimeItem> timesShownOLD = null;
    @SerializedName("timesClicked") //Depreciated
    public ArrayList<TimeItem> timesClickedOLD = null;

    @SerializedName("date")
    public DateItem date;
    @SerializedName("dateTimes")
    private ArrayList<ReminderLogItem> dateTimes = new ArrayList<>();


    //Not serialized. We use these as temp use only
    public transient boolean lifetime = false;
    public transient long timesShownLifetime = 0, timesClickedLifetime = 0, timesShownAgainLifetime = 0, timesSnoozedLifetime = 0;

    public ReminderLogDay(@NonNull DateItem dateItem) {
        this.date = dateItem;
    }

    public final void addItem(ReminderLogItem.LogType type, DateTimeItem dateTime) {
        dateTimes.add(new ReminderLogItem(type, dateTime));
    }

    public final void updateLog() {
        if(dateTimes == null) {
            dateTimes = new ArrayList<>();
        }
        if(timesShownOLD != null && timesClickedOLD != null) {
            //First ensure the oldest entry is a shown not a clicked (bug fix)
            if(timesShownOLD.size() > 0 && timesClickedOLD.size() > 0) {
                while (timesShownOLD.get(timesShownOLD.size() - 1).toMinutes() > timesClickedOLD.get(timesClickedOLD.size() - 1).toMinutes()) {
                    timesClickedOLD.remove(timesClickedOLD.size() - 1);
                }
            }

            //Get the index of the oldest of each
            int indexShown = timesShownOLD.size() -1;
            int indexClicked = timesClickedOLD.size() -1;
            TimeItem shown = null, clicked = null, shownNext = null;

            while(indexShown >= 0) {
                //Get the current shown item
                shown = timesShownOLD.get(indexShown);
                //Add the shown item
                addItem(ReminderLogItem.LogType.SHOWN, new DateTimeItem(date, shown));
                timesShownOLD.remove(indexShown);
                //Get the clicked value if it exists
                if(indexClicked >= 0) {
                    clicked = timesClickedOLD.get(indexClicked);
                }
                //Move to the next shown index
                indexShown = timesShownOLD.size() -1;
                //Get the next shown time if it exists
                if(indexShown >= 0) {
                    shownNext = timesShownOLD.get(indexShown);
                }

                //Check if we have a clicked value in this range
                if(clicked != null) {
                    if(shownNext != null) {
                        if(clicked.toMinutes() >= shown.toMinutes() && clicked.toMinutes() < shownNext.toMinutes()) {
                            //Add the clicked item
                            addItem(ReminderLogItem.LogType.CLICKED, new DateTimeItem(date, clicked));
                            timesClickedOLD.remove(indexClicked);
                            indexClicked = timesClickedOLD.size() -1;
                        }
                    } else {
                        if(clicked.toMinutes() >= shown.toMinutes()) {
                            //Add the clicked item
                            addItem(ReminderLogItem.LogType.CLICKED, new DateTimeItem(date, clicked));
                            timesClickedOLD.remove(indexClicked);
                            indexClicked = timesClickedOLD.size() -1;
                        }
                    }
                }
                shown = null;
                clicked = null;
                shownNext = null;
            }
            timesShownOLD = null;
            timesClickedOLD = null;
        }
    }

    public int getTimesShown() {
        int count = 0;
        for(ReminderLogItem item : dateTimes) {
            if(item.type == ReminderLogItem.LogType.SHOWN) {
                count++;
            }
        }
        return count;
    }

    public int getTimesShownAgain() {
        int count = 0;
        for(ReminderLogItem item : dateTimes) {
            if(item.type == ReminderLogItem.LogType.SHOWN_AGAIN) {
                count++;
            }
        }
        return count;
    }

    public int getTimesClicked() {
        int count = 0;
        for(ReminderLogItem item : dateTimes) {
            if(item.type == ReminderLogItem.LogType.CLICKED) {
                count++;
            }
        }
        return count;
    }

    public int getTimesSnoozed() {
        int count = 0;
        for(ReminderLogItem item : dateTimes) {
            if(item.type == ReminderLogItem.LogType.SNOOZED) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<ReminderLogItem> getItemList() {
        return dateTimes;
    }

}
