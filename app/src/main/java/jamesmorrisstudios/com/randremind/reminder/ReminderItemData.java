package jamesmorrisstudios.com.randremind.reminder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationContent;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.util.IconUtil;

/**
 * Simple data storage. NO LOGIC
 * Only the ReminderItem should touch this
 *
 * Created by James on 9/29/2015.
 */
public class ReminderItemData {
    public static final int CURRENT_VERSION = 1;
    //Unique data
    @SerializedName("uniqueName")
    public String uniqueName;
    @SerializedName("version")
    public int version = 0;
    //Title
    @SerializedName("title")
    public String title;
    @SerializedName("enabled")
    public boolean enabled;
    //messageList (replaces content)
    @SerializedName("messageList")
    public ArrayList<String> messageList = new ArrayList<>();
    @SerializedName("messageInOrder")
    public boolean messageInOrder = false;
    //Timing
    @SerializedName("startTime")
    public TimeItem startTime;
    @SerializedName("endTime")
    public TimeItem endTime;
    @SerializedName("specificTimeList")
    public ArrayList<TimeItem> specificTimeList;
    @SerializedName("numberPerDay")
    public int numberPerDay;
    @SerializedName("rangeTiming")
    public boolean rangeTiming = true;
    //Repeat
    @SerializedName("daysToRun")
    public boolean[] daysToRun;
    @SerializedName("weeksToRun")
    public boolean[] weeksToRun;
    //Notifications
    @SerializedName("notificationToneString")
    public String notificationTone;
    @SerializedName("notificationToneName")
    public String notificationToneName;
    @SerializedName("notificationVibratePattern")
    public NotificationContent.NotificationVibrate notificationVibratePattern = NotificationContent.NotificationVibrate.SHORT;
    @SerializedName("notificationLED")
    public boolean notificationLED = true;
    @SerializedName("notificationLEDColorInt")
    public int notificationLEDColor = Color.BLUE;
    @SerializedName("notificationPriority")
    public NotificationContent.NotificationPriority notificationPriority = NotificationContent.NotificationPriority.DEFAULT;
    @SerializedName("notificationIconIndex")
    public int notificationIcon = IconUtil.getIndex(R.drawable.notif_1);
    @SerializedName("notificationAccentColor")
    public int notificationAccentColor = AppBase.getContext().getResources().getColor(R.color.accent);
    //Snooze
    @SerializedName("snooze")
    public SnoozeOptions snooze;
    @SerializedName("autoSnooze")
    public SnoozeOptions autoSnooze;

    //Serialized on its own usually but included when exported (and its selected to be included)
    //Must null this before a save if not exporting it
    @SerializedName("reminderLog")
    public ReminderLog reminderLog = null;

    /**
     * Creates a new reminder reminder with all the default values set
     */
    public ReminderItemData(@NonNull String uniqueName) {
        //Unique name
        this.uniqueName = uniqueName;
        this.version = 0;
        //Title
        this.title = "";
        this.enabled = true;
        //Messages
        this.messageList = new ArrayList<>();
        this.messageInOrder = false;
        //Timing
        this.startTime = new TimeItem(9, 0);
        this.endTime = new TimeItem(20, 0);
        this.numberPerDay = 6;
        this.rangeTiming = true;
        this.specificTimeList = new ArrayList<>();
        this.specificTimeList.add(new TimeItem(9, 0));
        //Repeat
        this.daysToRun = new boolean[]{true, true, true, true, true, true, true};
        this.weeksToRun = new boolean[WeekOptions.values().length];
        this.weeksToRun[0] = true;
        //Notifications
        this.notificationTone = null;
        this.notificationToneName = AppBase.getContext().getString(R.string.none);
        this.notificationVibratePattern = NotificationContent.NotificationVibrate.SHORT;
        this.notificationLED = true;
        this.notificationLEDColor = Color.BLUE;
        this.notificationPriority = NotificationContent.NotificationPriority.DEFAULT;
        this.notificationIcon = IconUtil.getIndex(R.drawable.notif_1);
        this.notificationAccentColor = AppBase.getContext().getResources().getColor(R.color.accent);
        //Snooze
        this.snooze = SnoozeOptions.DISABLED;
        this.autoSnooze = SnoozeOptions.DISABLED;
    }

    /**
     * Creates a new reminder item that is a deep copy of the given one
     * @param reminderItemData
     */
    public ReminderItemData(@NonNull ReminderItemData reminderItemData) {
        this.uniqueName = reminderItemData.uniqueName;
        this.version = reminderItemData.version;
        this.title = reminderItemData.title;
        this.messageList = new ArrayList<>(reminderItemData.messageList);
        this.messageInOrder = reminderItemData.messageInOrder;
        this.enabled = reminderItemData.enabled;
        this.startTime = reminderItemData.startTime.copy();
        this.endTime = reminderItemData.endTime.copy();
        this.specificTimeList = UtilsTime.cloneArrayListTime(reminderItemData.specificTimeList);
        this.numberPerDay = reminderItemData.numberPerDay;
        this.rangeTiming = reminderItemData.rangeTiming;
        this.daysToRun = reminderItemData.daysToRun.clone();
        this.weeksToRun = reminderItemData.weeksToRun.clone();
        this.notificationTone = reminderItemData.notificationTone;
        this.notificationToneName = reminderItemData.notificationToneName;
        this.notificationVibratePattern = reminderItemData.notificationVibratePattern;
        this.notificationLED = reminderItemData.notificationLED;
        this.notificationLEDColor = reminderItemData.notificationLEDColor;
        this.notificationPriority = reminderItemData.notificationPriority;
        this.notificationIcon = reminderItemData.notificationIcon;
        this.notificationAccentColor = reminderItemData.notificationAccentColor;
        this.snooze = reminderItemData.snooze;
        this.autoSnooze = reminderItemData.autoSnooze;
    }

    /**
     * Creates a new reminder item that is a deep copy of the given one but with the new unique name as given
     * @param reminderItemData
     */
    public ReminderItemData(@NonNull ReminderItemData reminderItemData, @NonNull String uniqueName) {
        this.uniqueName = uniqueName;
        this.version = reminderItemData.version;
        this.title = reminderItemData.title;
        this.messageList = new ArrayList<>(reminderItemData.messageList);
        this.messageInOrder = reminderItemData.messageInOrder;
        this.enabled = reminderItemData.enabled;
        this.startTime = reminderItemData.startTime.copy();
        this.endTime = reminderItemData.endTime.copy();
        this.specificTimeList = UtilsTime.cloneArrayListTime(reminderItemData.specificTimeList);
        this.numberPerDay = reminderItemData.numberPerDay;
        this.rangeTiming = reminderItemData.rangeTiming;
        this.daysToRun = reminderItemData.daysToRun.clone();
        this.weeksToRun = reminderItemData.weeksToRun.clone();
        this.notificationTone = reminderItemData.notificationTone;
        this.notificationToneName = reminderItemData.notificationToneName;
        this.notificationVibratePattern = reminderItemData.notificationVibratePattern;
        this.notificationLED = reminderItemData.notificationLED;
        this.notificationLEDColor = reminderItemData.notificationLEDColor;
        this.notificationPriority = reminderItemData.notificationPriority;
        this.notificationIcon = reminderItemData.notificationIcon;
        this.notificationAccentColor = reminderItemData.notificationAccentColor;
        this.snooze = reminderItemData.snooze;
        this.autoSnooze = reminderItemData.autoSnooze;
    }

    /**
     */
    public ReminderItemData(@NonNull String uniqueName,
                            int version,
                            @NonNull String title,
                            @NonNull ArrayList<String> messageList,
                            boolean messageInOrder,
                            boolean enabled,
                            @NonNull TimeItem startTime,
                            @NonNull TimeItem endTime,
                            @NonNull ArrayList<TimeItem> specificTimeList,
                            int numberPerDay,
                            boolean rangeTiming,
                            @NonNull boolean[] daysToRun,
                            @NonNull boolean[] weeksToRun,
                            String notificationTone,
                            String notificationToneName,
                            NotificationContent.NotificationVibrate notificationVibratePattern,
                            boolean notificationLED,
                            int notificationLEDColor,
                            @NonNull NotificationContent.NotificationPriority notificationPriority,
                            int notificationIcon,
                            int notificationAccentColor,
                            @NonNull SnoozeOptions snooze,
                            @NonNull SnoozeOptions autoSnooze) {
        this.uniqueName = uniqueName;
        this.version = version;
        this.title = title;
        this.messageList = new ArrayList<>(messageList);
        this.messageInOrder = messageInOrder;
        this.enabled = enabled;
        this.startTime = startTime.copy();
        this.endTime = endTime.copy();
        this.specificTimeList = UtilsTime.cloneArrayListTime(specificTimeList);
        this.numberPerDay = numberPerDay;
        this.rangeTiming = rangeTiming;
        this.daysToRun = daysToRun.clone();
        this.weeksToRun = weeksToRun.clone();
        this.notificationTone = notificationTone;
        this.notificationToneName = notificationToneName;
        this.notificationVibratePattern = notificationVibratePattern;
        this.notificationLED = notificationLED;
        this.notificationLEDColor = notificationLEDColor;
        this.notificationPriority = notificationPriority;
        this.notificationIcon = notificationIcon;
        this.notificationAccentColor = notificationAccentColor;
        this.snooze = snooze;
        this.autoSnooze = autoSnooze;
    }

    public final void updateVersion() {
        //Change how curMessage works
        if(messageList == null) {
            messageList = new ArrayList<>();
        }
        version = CURRENT_VERSION;
    }

    public enum SnoozeOptions {
        DISABLED(0, AppBase.getContext().getString(R.string.disabled)),
        MIN_1(1, "1 "+AppBase.getContext().getString(R.string.minute_singular)),
        MIN_2(2, "2 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_3(3, "3 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_4(4, "4 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_5(5, "5 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_10(10, "10 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_15(15, "15 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_20(20, "20 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_25(25, "25 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_30(30, "30 "+AppBase.getContext().getString(R.string.minute_plural)),
        MIN_60(60, "60 "+AppBase.getContext().getString(R.string.minute_plural));

        public final String name;
        public final int minutes;

        SnoozeOptions(int minutes, String name) {
            this.minutes = minutes;
            this.name = name;
        }
    }

    public enum WeekOptions {
        Every(AppBase.getContext().getString(R.string.every_week)),
        FIRST(AppBase.getContext().getString(R.string.first)),
        SECOND(AppBase.getContext().getString(R.string.second)),
        THIRD(AppBase.getContext().getString(R.string.third)),
        FOURTH(AppBase.getContext().getString(R.string.fourth)),
        FIFTH(AppBase.getContext().getString(R.string.fifth)),
        LAST(AppBase.getContext().getString(R.string.last));

        public final String name;

        WeekOptions(String name) {
            this.name = name;
        }
    }

    /**
     * @param obj Object to compare to
     * @return True if equal based on unique id.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null && obj instanceof ReminderItemData) {
            ReminderItemData item = (ReminderItemData) obj;
            return this.uniqueName.equals(item.uniqueName);
        } else {
            return false;
        }
    }
}
