package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.utilitieslibrary.time.DateTimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import java.util.ArrayList;

/**
 * Created by James on 4/30/2015.
 */
public class ReminderLog {
    @SerializedName("days")
    public ArrayList<ReminderLogDay> days = new ArrayList<>();

    public final void logClicked(@NonNull DateTimeItem dateTime) {
        Log.v("ReminderLog", "Log Clicked: "+dateTime.dateItem.year+" "+dateTime.dateItem.month+" "+dateTime.dateItem.dayOfMonth+
        ", "+dateTime.timeItem.getHourInTimeFormatString()+":"+dateTime.timeItem.getMinuteString());
        ReminderLogDay day = getDay(dateTime);
        day.timesClicked.add(0, dateTime.timeItem);
    }

    public final void logShown(@NonNull DateTimeItem dateTime) {
        Log.v("ReminderLog", "Log Shown: "+dateTime.dateItem.year+" "+dateTime.dateItem.month+" "+dateTime.dateItem.dayOfMonth+
                ", "+dateTime.timeItem.getHourInTimeFormatString()+":"+dateTime.timeItem.getMinuteString());
        ReminderLogDay day = getDay(dateTime);
        day.timesShown.add(0, dateTime.timeItem);
    }

    private ReminderLogDay getDay(@NonNull DateTimeItem dateTimeItem) {
        //If no days yet add and return one
        if(days.isEmpty()) {
            Log.v("ReminderLog", "No days yet, creating a new one");
            days.add(new ReminderLogDay(dateTimeItem.dateItem));
            return days.get(0);
        }
        //If the first day is today now return it
        ReminderLogDay day = days.get(0);
        if(day.date.equals(dateTimeItem.dateItem)) {
            Log.v("ReminderLog", "Same day as current");
            return day;
        }
        //If we have a new day then create a new entry and return it
        days.add(0, new ReminderLogDay(dateTimeItem.dateItem));
        Log.v("ReminderLog", "New day so creating another entry");
        return days.get(0);
    }

}
