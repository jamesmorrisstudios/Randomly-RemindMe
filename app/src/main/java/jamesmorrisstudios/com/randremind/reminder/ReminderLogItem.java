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
        SHOWN(AppBase.getContext().getString(R.string.shown)),
        CLICKED(AppBase.getContext().getString(R.string.completed)),
        SHOWN_AGAIN(AppBase.getContext().getString(R.string.shown_again)),
        SNOOZED(AppBase.getContext().getString(R.string.snoozed)),
        DISMISSED(AppBase.getContext().getString(R.string.dismissed));

        public final String name;

        LogType(String name) {
            this.name = name;
        }
    }

}
