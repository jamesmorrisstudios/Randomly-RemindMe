package jamesmorrisstudios.com.randremind.reminder;

import android.support.annotation.NonNull;

import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 9/16/2015.
 */
public class ReminderLogItem {
    public final LogType type;
    public final DateTimeItem dateTime;

    public ReminderLogItem(@NonNull LogType type, @NonNull DateTimeItem dateTime) {
        this.type = type;
        this.dateTime = dateTime;
    }

    public enum LogType {
        SHOWN,
        CLICKED,
        SHOWN_AGAIN,
        SNOOZED,
        DISMISSED;

        public String getName() {
            switch(this) {
                case SHOWN:
                    return AppBase.getContext().getString(R.string.shown);
                case CLICKED:
                    return AppBase.getContext().getString(R.string.completed);
                case SHOWN_AGAIN:
                    return AppBase.getContext().getString(R.string.shown_again);
                case SNOOZED:
                    return AppBase.getContext().getString(R.string.snoozed);
                case DISMISSED:
                    return AppBase.getContext().getString(R.string.dismissed);
                default:
                    return AppBase.getContext().getString(R.string.shown);
            }
        }
    }

}
