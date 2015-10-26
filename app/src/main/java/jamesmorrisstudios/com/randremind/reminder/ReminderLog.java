package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;

import java.util.ArrayList;

/**
 * Created by James on 4/30/2015.
 */
public class ReminderLog {
    @SerializedName("days")
    public ArrayList<ReminderLogDay> days = new ArrayList<>();
    @SerializedName("lifetimeShown")
    public long lifetimeShown = 0;
    @SerializedName("lifetimeClicked")
    public long lifetimeClicked = 0;
    @SerializedName("lifetimeSnoozed")
    public long lifetimeSnoozed = 0;
    @SerializedName("lifetimeShownAgain")
    public long lifetimeShownAgain = 0;
    @SerializedName("lifetimeDismissed")
    public long lifetimeDismissed = 0;

    public final void updateLog() {
        for(ReminderLogDay day : days) {
            day.updateLog();
        }
    }

    public final void logDismissed(@NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime) {
        Log.v("ReminderLog", "Log Clicked: " + dateTime.dateItem.year + " " + dateTime.dateItem.month + " " + dateTime.dateItem.dayOfMonth +
                ", " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        ReminderLogDay day = getDay(firstDateTime);
        day.addItem(ReminderLogItem.LogType.DISMISSED, dateTime);
        lifetimeDismissed++;
    }

    public final void logClicked(@NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime, boolean snoozed) {
        Log.v("ReminderLog", "Log Clicked: " + dateTime.dateItem.year + " " + dateTime.dateItem.month + " " + dateTime.dateItem.dayOfMonth +
                ", " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        ReminderLogDay day = getDay(firstDateTime);
        if(snoozed) {
            //If snoozed it is manual (clicked) snooze
            //day.dateTimesSnoozed.add(0, dateTime);
            day.addItem(ReminderLogItem.LogType.SNOOZED, dateTime);
            lifetimeSnoozed++;
        } else {
            //day.dateTimesClicked.add(0, dateTime);
            day.addItem(ReminderLogItem.LogType.CLICKED, dateTime);
            lifetimeClicked++;
        }
    }

    public final void logShown(@NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime, boolean snoozed) {
        Log.v("ReminderLog", "Log Shown: " + dateTime.dateItem.year + " " + dateTime.dateItem.month + " " + dateTime.dateItem.dayOfMonth +
                ", " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        ReminderLogDay day = getDay(firstDateTime);
        if(snoozed) {
            //If snoozed it is being shown again after either an auto snooze or manual snooze
            //day.dateTimesShownAgain.add(0, dateTime);
            day.addItem(ReminderLogItem.LogType.SHOWN_AGAIN, dateTime);
            lifetimeShownAgain++;
        } else {
            //day.dateTimesShown.add(0, dateTime);
            day.addItem(ReminderLogItem.LogType.SHOWN, dateTime);
            lifetimeShown++;
        }
    }

    private ReminderLogDay getDay(@NonNull DateTimeItem dateTimeItem) {
        //If no days yet add and return one
        if (days.isEmpty()) {
            Log.v("ReminderLog", "No days yet, creating a new one");
            days.add(new ReminderLogDay(dateTimeItem.dateItem));
            return days.get(0);
        }
        //If the first day is today now return it
        ReminderLogDay day = days.get(0);
        if (day.date.equals(dateTimeItem.dateItem)) {
            Log.v("ReminderLog", "Same day as current");
            return day;
        }
        //If we have a new day then create a new entry and return it
        days.add(0, new ReminderLogDay(dateTimeItem.dateItem));
        while (days.size() > 360) {
            days.remove(days.size() - 1);
        }
        Log.v("ReminderLog", "New day so creating another entry");
        return days.get(0);
    }

}
