package jamesmorrisstudios.com.randremind.reminder;

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

    public final void logClicked() {
        DateTimeItem dateTimeItem = UtilsTime.getDateTimeNow();
        Log.v("ReminderLog", "Log Clicked: "+dateTimeItem.dateItem.year+" "+dateTimeItem.dateItem.month+" "+dateTimeItem.dateItem.dayOfMonth+
        ", "+dateTimeItem.timeItem.getHourInTimeFormatString()+":"+dateTimeItem.timeItem.getMinuteString());
        ReminderLogDay day = getDay(dateTimeItem);
        day.timesClicked.add(0, dateTimeItem.timeItem);
    }

    public final void logShown() {
        DateTimeItem dateTimeItem = UtilsTime.getDateTimeNow();
        Log.v("ReminderLog", "Log Shown: "+dateTimeItem.dateItem.year+" "+dateTimeItem.dateItem.month+" "+dateTimeItem.dateItem.dayOfMonth+
                ", "+dateTimeItem.timeItem.getHourInTimeFormatString()+":"+dateTimeItem.timeItem.getMinuteString());
        ReminderLogDay day = getDay(dateTimeItem);
        day.timesShown.add(0, dateTimeItem.timeItem);
    }

    private ReminderLogDay getDay(DateTimeItem dateTimeItem) {
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
        days.add(new ReminderLogDay(dateTimeItem.dateItem));
        Log.v("ReminderLog", "New day so creating another entry");
        return days.get(0);
    }

}
