package jamesmorrisstudios.com.randremind.reminder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationContent;
import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
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
    //Depreciated
    @SerializedName("numberPerDay") //moves to triggerCount
    public int numberPerDay;
    @SerializedName("rangeTiming")  //if true enable randomEnable. if false enable specificEnable
    public boolean rangeTiming;
    @SerializedName("daysToRun") //Moves to daysOfWeek
    public boolean[] daysToRun;
    @SerializedName("weeksToRun") //Moves to weeksOfMonth
    public boolean[] weeksToRun;

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
    @SerializedName("messageList")
    public ArrayList<String> messageList;
    @SerializedName("messageInOrder")
    public boolean messageInOrder;
    //Timing
    @SerializedName("startDate")
    public DateItem startDate;
    @SerializedName("endEnable")
    public boolean endEnable;
    @SerializedName("endDate")
    public DateItem endDate;
    //Criteria
    @SerializedName("startTime")
    public TimeItem startTime;
    @SerializedName("endTime")
    public TimeItem endTime;
    @SerializedName("filterType")
    public FilterType filterType;
    @SerializedName("daysOfWeek")
    public boolean[] daysOfWeek;
    @SerializedName("daysOfMonth")
    public boolean[] daysOfMonth;
    @SerializedName("weeksOfMonth")
    public boolean[] weeksOfMonth;
    @SerializedName("monthsOfYear")
    public boolean[] monthsOfYear;
    @SerializedName("repeatCount")
    public int repeatCount;
    @SerializedName("repeatType")
    public RepeatType repeatType;
    @SerializedName("daysOfYear1")
    public ArrayList<DateItem> daysOfYear;
    //Triggers
    @SerializedName("triggerMode")
    public TriggerMode triggerMode;
    @SerializedName("triggerCount")
    public int triggerCount;
    @SerializedName("triggerPeriod")
    public TimePeriod triggerPeriod;
    @SerializedName("specificTimeList")
    public ArrayList<TimeItem> specificTimeList;
    @SerializedName("intervalPeriod")
    public RepeatTypeShort intervalPeriod;
    @SerializedName("intervalCount")
    public int intervalCount;
    //Notifications
    @SerializedName("notificationToneString")
    public String notificationTone;
    @SerializedName("notificationToneName")
    public String notificationToneName;
    @SerializedName("notificationVibratePattern")
    public NotificationContent.NotificationVibrate notificationVibratePattern;
    @SerializedName("notificationLED")
    public boolean notificationLED;
    @SerializedName("notificationLEDColorInt")
    public int notificationLEDColor;
    @SerializedName("notificationPriority")
    public NotificationContent.NotificationPriority notificationPriority;
    @SerializedName("notificationIconIndex")
    public int notificationIcon;
    @SerializedName("notificationAccentColor")
    public int notificationAccentColor;
    //Snooze
    @SerializedName("snooze")
    public SnoozeOptions snooze;
    @SerializedName("autoSnooze")
    public SnoozeOptions autoSnooze;
    //State management
    @SerializedName("curMessage")
    public int curMessage = 0;
    @SerializedName("alertTimes")
    public ArrayList<TimeItem> alertTimes;
    //Options specific to a reminder
    @SerializedName("showAdvanced")
    public boolean showAdvanced;
    //Side counter data
    @SerializedName("notifCounter")
    public int notifCounter;
    //Serialized on its own usually but included when exported (and its selected to be included)
    //Must null this before a save if not exporting it
    @SerializedName("reminderLog")
    public ReminderLog reminderLog = null;

    /**
     * Creates a new reminder reminder with all the default values set
     */
    public ReminderItemData(@NonNull String uniqueName) {
        setDefaultValues(uniqueName);
    }

    private void setDefaultValues(@NonNull String uniqueName) {
        //Depreciated
        this.numberPerDay = -1;
        this.rangeTiming = true;
        this.daysToRun = null;
        this.weeksToRun = null;

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
        this.startDate = UtilsTime.getDateNow();
        this.endEnable = false;
        this.endDate = UtilsTime.getDateNow();
        //Criteria
        this.startTime = new TimeItem(9, 0);
        this.endTime = new TimeItem(20, 0);
        this.filterType = FilterType.NORMAL;
        this.daysOfWeek = Utils.getFilledBoolArray(true, 7);
        this.daysOfMonth = Utils.getFilledBoolArray(true, 31);
        this.weeksOfMonth = new boolean[WeekOptions.values().length];
        this.weeksOfMonth[0] = true;
        this.monthsOfYear = Utils.getFilledBoolArray(true, 12);
        this.repeatCount = 1;
        this.repeatType = RepeatType.DAYS;
        this.daysOfYear = new ArrayList<>();
        //Triggers
        this.triggerMode = TriggerMode.RANDOM;
        this.triggerCount = 10;
        this.triggerPeriod = TimePeriod.DAY;
        this.specificTimeList = new ArrayList<>();
        this.intervalPeriod = RepeatTypeShort.MINUTES;
        this.intervalCount = 30;
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
        //State management
        this.curMessage = 0;
        this.alertTimes = new ArrayList<>();
        //Options specific to a reminder
        this.showAdvanced = false;
        //Side counter data
        this.notifCounter = 0;
    }

    /**
     * Creates a new reminder item that is a deep copy of the given one
     * @param reminderItemData
     */
    public ReminderItemData(@NonNull ReminderItemData reminderItemData) {
        setData(reminderItemData, reminderItemData.uniqueName, false);
    }

    /**
     * Creates a new reminder item that is a deep copy of the given one but with the new unique name as given
     * @param reminderItemData
     */
    public ReminderItemData(@NonNull ReminderItemData reminderItemData, @NonNull String uniqueName, boolean fromDefault) {
        setData(reminderItemData, uniqueName, fromDefault);
    }

    private void setData(@NonNull ReminderItemData reminderItemData, @NonNull String uniqueName, boolean fromDefault) {
        if(fromDefault) {
            setDefaultValues(uniqueName);
        }

        //Depreciated
        this.numberPerDay = reminderItemData.numberPerDay;
        this.rangeTiming = reminderItemData.rangeTiming;
        if(reminderItemData.daysToRun != null) {
            this.daysToRun = reminderItemData.daysToRun.clone();
        }
        if(reminderItemData.weeksToRun != null) {
            this.weeksToRun = reminderItemData.weeksToRun.clone();
        }

        //Name and Version
        this.uniqueName = uniqueName;
        this.version = reminderItemData.version;
        if(!fromDefault) {
            //General
            this.title = reminderItemData.title;
            this.enabled = reminderItemData.enabled;
            //Messages
            this.messageList = new ArrayList<>(reminderItemData.messageList);
            this.messageInOrder = reminderItemData.messageInOrder;
            //Timing
            this.startDate = new DateItem(reminderItemData.startDate);
            this.endEnable = reminderItemData.endEnable;
            this.endDate = new DateItem(reminderItemData.endDate);
        }
        //Criteria
        this.startTime = reminderItemData.startTime.copy();
        this.endTime = reminderItemData.endTime.copy();
        this.filterType = reminderItemData.filterType;
        this.daysOfWeek = reminderItemData.daysOfWeek.clone();
        this.daysOfMonth = reminderItemData.daysOfMonth.clone();
        this.weeksOfMonth = reminderItemData.weeksOfMonth.clone();
        this.monthsOfYear = reminderItemData.monthsOfYear.clone();
        this.repeatCount = reminderItemData.repeatCount;
        this.repeatType = reminderItemData.repeatType;
        this.daysOfYear = UtilsTime.cloneArrayListDate(reminderItemData.daysOfYear);
        //Triggers
        this.triggerMode = reminderItemData.triggerMode;
        this.triggerCount = reminderItemData.triggerCount;
        this.triggerPeriod = reminderItemData.triggerPeriod;
        this.specificTimeList = UtilsTime.cloneArrayListTime(reminderItemData.specificTimeList);
        this.intervalPeriod = reminderItemData.intervalPeriod;
        this.intervalCount = reminderItemData.intervalCount;
        //Notifications
        this.notificationTone = reminderItemData.notificationTone;
        this.notificationToneName = reminderItemData.notificationToneName;
        this.notificationVibratePattern = reminderItemData.notificationVibratePattern;
        this.notificationLED = reminderItemData.notificationLED;
        this.notificationLEDColor = reminderItemData.notificationLEDColor;
        this.notificationPriority = reminderItemData.notificationPriority;
        this.notificationIcon = reminderItemData.notificationIcon;
        this.notificationAccentColor = reminderItemData.notificationAccentColor;
        //Snooze
        this.snooze = reminderItemData.snooze;
        this.autoSnooze = reminderItemData.autoSnooze;
        if(!fromDefault) {
            //State management
            this.curMessage = reminderItemData.curMessage;
            this.alertTimes = new ArrayList<>(reminderItemData.alertTimes);
            //Options specific to a reminder
            this.showAdvanced = reminderItemData.showAdvanced;
            //Side counter data
            this.notifCounter = reminderItemData.notifCounter;
        }
    }

    public enum DayOfMonth {
        _1, _2, _3, _4, _5, _6, _7, _8 ,_9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20,
        _21, _22, _23, _24, _25, _26, _27, _28, _29, _30, _31;

        public String getName() {
            switch(this) {
                case _1:
                    return AppBase.getContext().getString(R.string.num_1);
                case _2:
                    return AppBase.getContext().getString(R.string.num_2);
                case _3:
                    return AppBase.getContext().getString(R.string.num_3);
                case _4:
                    return AppBase.getContext().getString(R.string.num_4);
                case _5:
                    return AppBase.getContext().getString(R.string.num_5);
                case _6:
                    return AppBase.getContext().getString(R.string.num_6);
                case _7:
                    return AppBase.getContext().getString(R.string.num_7);
                case _8:
                    return AppBase.getContext().getString(R.string.num_8);
                case _9:
                    return AppBase.getContext().getString(R.string.num_9);
                case _10:
                    return AppBase.getContext().getString(R.string.num_10);
                case _11:
                    return AppBase.getContext().getString(R.string.num_11);
                case _12:
                    return AppBase.getContext().getString(R.string.num_12);
                case _13:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_3);
                case _14:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_4);
                case _15:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_5);
                case _16:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_6);
                case _17:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_7);
                case _18:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_8);
                case _19:
                    return AppBase.getContext().getString(R.string.num_1)+AppBase.getContext().getString(R.string.num_9);
                case _20:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_0);
                case _21:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_1);
                case _22:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_2);
                case _23:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_3);
                case _24:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_4);
                case _25:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_5);
                case _26:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_6);
                case _27:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_7);
                case _28:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_8);
                case _29:
                    return AppBase.getContext().getString(R.string.num_2)+AppBase.getContext().getString(R.string.num_9);
                case _30:
                    return AppBase.getContext().getString(R.string.num_3)+AppBase.getContext().getString(R.string.num_0);
                case _31:
                    return AppBase.getContext().getString(R.string.num_3)+AppBase.getContext().getString(R.string.num_1);
                default:
                    return AppBase.getContext().getString(R.string.num_1);
            }
        }


    }

    public enum FilterType {
        NORMAL, MANUAL;

        public String getName() {
            switch(this) {
                case NORMAL:
                    return AppBase.getContext().getString(R.string.normal);
                case MANUAL:
                    return AppBase.getContext().getString(R.string.manual);
                default:
                    return AppBase.getContext().getString(R.string.normal);
            }
        }
    }

    public enum TriggerMode {
        RANDOM, LESS_RANDOM, EVEN, SPECIFIC, INTERVAL;

        public String getName() {
            switch(this) {
                case RANDOM:
                    return AppBase.getContext().getString(R.string.random);
                case LESS_RANDOM:
                    return AppBase.getContext().getString(R.string.less_random);
                case EVEN:
                    return AppBase.getContext().getString(R.string.even);
                case SPECIFIC:
                    return AppBase.getContext().getString(R.string.specific);
                case INTERVAL:
                    return AppBase.getContext().getString(R.string.interval);
                default:
                    return AppBase.getContext().getString(R.string.randomly);
            }
        }
    }

    public enum TimePeriod {
        DAY, WEEK, MONTH, YEAR;

        public String getName() {
            switch(this) {
                case DAY:
                    return AppBase.getContext().getString(R.string.day);
                case WEEK:
                    return AppBase.getContext().getString(R.string.week);
                case MONTH:
                    return AppBase.getContext().getString(R.string.month);
                case YEAR:
                    return AppBase.getContext().getString(R.string.year);
                default:
                    return AppBase.getContext().getString(R.string.day);
            }
        }
    }

    public enum RepeatTypeShort {
        MINUTES, HOURS;

        public String getName() {
            switch(this) {
                case MINUTES:
                    return AppBase.getContext().getString(R.string.minute_plural);
                case HOURS:
                    return AppBase.getContext().getString(R.string.hour_plural);
                default:
                    return AppBase.getContext().getString(R.string.minute_plural);
            }
        }
    }

    public enum RepeatType {
            DAYS, WEEKS, MONTHS, YEARS;

        public String getName() {
            switch(this) {
                case DAYS:
                    return AppBase.getContext().getString(R.string.days);
                case WEEKS:
                    return AppBase.getContext().getString(R.string.weeks);
                case MONTHS:
                    return AppBase.getContext().getString(R.string.months);
                case YEARS:
                    return AppBase.getContext().getString(R.string.years);
                default:
                    return AppBase.getContext().getString(R.string.days);
            }
        }
    }

    public enum SnoozeOptions {
        DISABLED(0),
        MIN_1(1),
        MIN_2(2),
        MIN_3(3),
        MIN_4(4),
        MIN_5(5),
        MIN_10(10),
        MIN_15(15),
        MIN_20(20),
        MIN_25(25),
        MIN_30(30),
        MIN_60(60);

        public final int minutes;

        SnoozeOptions(int minutes) {
            this.minutes = minutes;
        }

        public String getName() {
            switch(this) {
                case DISABLED:
                    return AppBase.getContext().getString(R.string.disabled);
                case MIN_1:
                    return "1 "+AppBase.getContext().getString(R.string.minute_singular);
                case MIN_2:
                    return "2 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_3:
                    return "3 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_4:
                    return "4 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_5:
                    return "5 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_10:
                    return "10 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_15:
                    return "15 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_20:
                    return "20 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_25:
                    return "25 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_30:
                    return "30 "+AppBase.getContext().getString(R.string.minute_plural);
                case MIN_60:
                    return "60 "+AppBase.getContext().getString(R.string.minute_plural);
                default:
                    return AppBase.getContext().getString(R.string.disabled);
            }
        }

    }

    public enum WeekOptions {
        Every,
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH,
        LAST;

        public String getName() {
            switch(this) {
                case Every:
                    return AppBase.getContext().getString(R.string.all);
                case FIRST:
                    return AppBase.getContext().getString(R.string.first);
                case SECOND:
                    return AppBase.getContext().getString(R.string.second);
                case THIRD:
                    return AppBase.getContext().getString(R.string.third);
                case FOURTH:
                    return AppBase.getContext().getString(R.string.fourth);
                case FIFTH:
                    return AppBase.getContext().getString(R.string.fifth);
                case LAST:
                    return AppBase.getContext().getString(R.string.last);
                default:
                    return AppBase.getContext().getString(R.string.every_week);
            }
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
