/*
 * Copyright (c) 2015.  James Morris Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jamesmorrisstudios.com.randremind.reminder;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.FileWriter;
import com.jamesmorrisstudios.appbaselibrary.Serializer;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.math.UtilsMath;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationAction;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationContent;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationContent.NotificationPriority;
import com.jamesmorrisstudios.appbaselibrary.notification.NotificationContent.NotificationVibrate;
import com.jamesmorrisstudios.appbaselibrary.preferences.Prefs;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.receiver.NotificationReceiver;
import jamesmorrisstudios.com.randremind.util.IconUtil;

/**
 * Individual reminder reminder that contains all needed items to be a reminder
 * <p/>
 * Created by James on 4/20/2015.
 */
public final class ReminderItem extends BaseRecycleItem {
    public static final int CURRENT_VERSION = 1;
    //Unique data
    @SerializedName("uniqueName")
    private String uniqueName;
    @SerializedName("version")
    private int version = 0;
    //Title
    @SerializedName("title")
    private String title;
    @SerializedName("enabled")
    private boolean enabled;
    //messageList (replaces content)
    @SerializedName("messageList")
    private ArrayList<String> messageList = new ArrayList<>();
    @SerializedName("messageInOrder")
    private boolean messageInOrder = false;
    //Timing
    @SerializedName("startTime")
    private TimeItem startTime;
    @SerializedName("endTime")
    private TimeItem endTime;
    @SerializedName("specificTimeList")
    private ArrayList<TimeItem> specificTimeList;
    @SerializedName("numberPerDay")
    private int numberPerDay;
    @SerializedName("rangeTiming")
    private boolean rangeTiming = true;
    //Repeat
    @SerializedName("daysToRun")
    private boolean[] daysToRun;
    @SerializedName("weeksToRun")
    private boolean[] weeksToRun;
    //Notifications
    @SerializedName("notificationToneString")
    private String notificationTone;
    @SerializedName("notificationToneName")
    private String notificationToneName;
    @SerializedName("notificationVibratePattern")
    private NotificationVibrate notificationVibratePattern = NotificationVibrate.SHORT;
    @SerializedName("notificationLED")
    private boolean notificationLED = true;
    @SerializedName("notificationLEDColorInt")
    private int notificationLEDColor = Color.BLUE;
    @SerializedName("notificationPriority")
    private NotificationPriority notificationPriority = NotificationPriority.DEFAULT;
    @SerializedName("notificationIconIndex")
    private int notificationIcon = IconUtil.getIndex(R.drawable.notif_1);
    @SerializedName("notificationAccentColor")
    private int notificationAccentColor = AppBase.getContext().getResources().getColor(R.color.accent);
    //Snooze
    @SerializedName("snooze")
    private SnoozeOptions snooze;
    @SerializedName("autoSnooze")
    private SnoozeOptions autoSnooze;

    //Do Not Serialize This
    private transient boolean dirty = false; //Set to true when changing something
    public transient ReminderLog reminderLog = null;
    private transient AsyncTask<Void, Void, Boolean> taskLoad = null;

    public void clearDirty() {
        dirty = false;
    }

    private void setDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(this.title == null || !this.title.equals(title)) {
            setDirty();
            this.title = title;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled != enabled) {
            setDirty();
            this.enabled = enabled;
        }
    }

    public SnoozeOptions getSnooze() {
        return snooze;
    }

    public void setSnooze(@NonNull SnoozeOptions snooze) {
        setDirty();
        this.snooze = snooze;
    }

    public SnoozeOptions getAutoSnooze() {
        return autoSnooze;
    }

    public void setAutoSnooze(@NonNull SnoozeOptions autoSnooze) {
        setDirty();
        this.autoSnooze = autoSnooze;
    }

    public ArrayList<String> getMessageList() {
        return messageList;
    }

    public ArrayList<String> updateMessageList() {
        setDirty();
        return messageList;
    }

    public void setMessageList(ArrayList<String> messageList) {
        setDirty();
        this.messageList = messageList;
    }

    public boolean isMessageInOrder() {
        return messageInOrder;
    }

    public void setMessageInOrder(boolean messageInOrder) {
        if(this.messageInOrder != messageInOrder) {
            setDirty();
            this.messageInOrder = messageInOrder;
        }
    }

    public TimeItem getStartTime() {
        return startTime;
    }

    public TimeItem updateStartTime() {
        setDirty();
        return startTime;
    }

    public void setStartTime(TimeItem startTime) {
        setDirty();
        this.startTime = startTime;
    }

    public TimeItem getEndTime() {
        return endTime;
    }

    public TimeItem updateEndTime() {
        setDirty();
        return endTime;
    }

    public void setEndTime(TimeItem endTime) {
        setDirty();
        this.endTime = endTime;
    }

    public ArrayList<TimeItem> getSpecificTimeList() {
        return specificTimeList;
    }

    public ArrayList<TimeItem> updateSpecificTimeList() {
        setDirty();
        return specificTimeList;
    }

    public void setSpecificTimeList(ArrayList<TimeItem> specificTimeList) {
        setDirty();
        this.specificTimeList = specificTimeList;
    }

    public int getNumberPerDay() {
        return numberPerDay;
    }

    public void setNumberPerDay(int numberPerDay) {
        if(this.numberPerDay != numberPerDay) {
            setDirty();
            this.numberPerDay = numberPerDay;
        }
    }

    public boolean isRangeTiming() {
        return rangeTiming;
    }

    public void setRangeTiming(boolean rangeTiming) {
        if(this.rangeTiming != rangeTiming) {
            setDirty();
            this.rangeTiming = rangeTiming;
        }
    }

    public boolean[] getDaysToRun() {
        return daysToRun;
    }

    public boolean[] updateDaysToRun() {
        setDirty();
        return daysToRun;
    }

    public void setDaysToRun(boolean[] daysToRun) {
        setDirty();
        this.daysToRun = daysToRun;
    }

    public boolean[] getWeeksToRun() {
        return weeksToRun;
    }

    public boolean[] updateWeeksToRun() {
        setDirty();
        return weeksToRun;
    }

    public void setWeeksToRun(boolean[] weeksToRun) {
        setDirty();
        this.weeksToRun = weeksToRun;
    }


    public void setNotificationTone(String notificationTone) {
        if(this.notificationTone == null || !this.notificationTone.equals(notificationTone)) {
            setDirty();
            this.notificationTone = notificationTone;
        }
    }

    public String getNotificationToneName() {
        return notificationToneName;
    }

    public void setNotificationToneName(String notificationToneName) {
        if(this.notificationToneName == null || !this.notificationToneName.equals(notificationToneName)) {
            setDirty();
            this.notificationToneName = notificationToneName;
        }
    }

    public NotificationVibrate getNotificationVibratePattern() {
        return notificationVibratePattern;
    }

    public void setNotificationVibratePattern(NotificationVibrate notificationVibratePattern) {
        if(this.notificationVibratePattern != notificationVibratePattern) {
            setDirty();
            this.notificationVibratePattern = notificationVibratePattern;
        }
    }

    public boolean isNotificationLED() {
        return notificationLED;
    }

    public void setNotificationLED(boolean notificationLED) {
        if(this.notificationLED != notificationLED) {
            setDirty();
            this.notificationLED = notificationLED;
        }
    }

    public int getNotificationLEDColor() {
        return notificationLEDColor;
    }

    public void setNotificationLEDColor(int notificationLEDColor) {
        if(this.notificationLEDColor != notificationLEDColor) {
            setDirty();
            this.notificationLEDColor = notificationLEDColor;
        }
    }

    public NotificationPriority getNotificationPriority() {
        return notificationPriority;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
        if(this.notificationPriority != notificationPriority) {
            setDirty();
            this.notificationPriority = notificationPriority;
        }
    }

    public int getNotificationIcon() {
        return notificationIcon;
    }

    public void setNotificationIcon(int notificationIcon) {
        if(this.notificationIcon != notificationIcon) {
            setDirty();
            this.notificationIcon = notificationIcon;
        }
    }

    public int getNotificationAccentColor() {
        return notificationAccentColor;
    }

    public void setNotificationAccentColor(int notificationAccentColor) {
        if(this.notificationAccentColor != notificationAccentColor) {
            setDirty();
            this.notificationAccentColor = notificationAccentColor;
        }
    }

    /**
     * Creates a new reminder reminder with all the default values set
     */
    public ReminderItem() {
        //Unique name
        this.uniqueName = generateUniqueName();
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
        this.notificationVibratePattern = NotificationVibrate.SHORT;
        this.notificationLED = true;
        this.notificationLEDColor = Color.BLUE;
        this.notificationPriority = NotificationPriority.DEFAULT;
        this.notificationIcon = IconUtil.getIndex(R.drawable.notif_1);
        this.notificationAccentColor = AppBase.getContext().getResources().getColor(R.color.accent);
        //Snooze
        this.snooze = SnoozeOptions.DISABLED;
        this.autoSnooze = SnoozeOptions.DISABLED;
    }

    /**
     */
    public ReminderItem(@NonNull String uniqueName, int version, @NonNull String title,
                        @NonNull ArrayList<String> messageList, boolean messageInOrder,
                        boolean enabled, @NonNull TimeItem startTime, @NonNull TimeItem endTime,
                        @NonNull ArrayList<TimeItem> specificTimeList,
                        int numberPerDay, boolean rangeTiming,
                        @NonNull boolean[] daysToRun, @NonNull boolean[] weeksToRun, String notificationTone, String notificationToneName,
                        NotificationVibrate notificationVibratePattern, boolean notificationLED,
                        int notificationLEDColor, @NonNull NotificationPriority notificationPriority,
                        int notificationIcon, int notificationAccentColor, @NonNull SnoozeOptions snooze, @NonNull SnoozeOptions autoSnooze) {
        this.uniqueName = uniqueName;
        this.version = version;
        this.title = title;
        this.messageList = new ArrayList<>(messageList);
        this.messageInOrder = messageInOrder;
        this.enabled = enabled;
        this.startTime = startTime.copy();
        this.endTime = endTime.copy();
        this.specificTimeList = cloneArrayListTime(specificTimeList);
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

    private ArrayList<TimeItem> cloneArrayListTime(ArrayList<TimeItem> items) {
        ArrayList<TimeItem> newItems = new ArrayList<>();
        for(TimeItem item : items) {
            newItems.add(item.copy());
        }
        return newItems;
    }

    /**
     * Generates a unique name for the reminder
     *
     * @return Unique name
     */
    @NonNull
    public static String generateUniqueName() {
        return UUID.randomUUID().toString();
    }

    public final void regenerateUniqueName() {
        uniqueName = UUID.randomUUID().toString();
    }

    public final void setCurMessage(int curMessage) {
        Prefs.putInt(AppBase.getContext().getString(R.string.pref_reminder_alerts), "CURR_MESSAGE" + uniqueName, curMessage);
    }

    public final int getCurMessage() {
        return Prefs.getInt(AppBase.getContext().getString(R.string.pref_reminder_alerts), "CURR_MESSAGE" + uniqueName, -1);
    }

    public static ArrayList<TimeItem> getAlertTimes(String uniqueName) {
        ArrayList<String> items = Prefs.getStringArrayList(AppBase.getContext().getString(R.string.pref_reminder_alerts), "ALERTS" + uniqueName);
        ArrayList<TimeItem> timeItems = new ArrayList<>();
        for (String item : items) {
            timeItems.add(TimeItem.decodeFromString(item));
        }
        return timeItems;
    }

    public static void setAlertTimes(ArrayList<TimeItem> alertTimes, String uniqueName) {
        ArrayList<String> items = new ArrayList<>();
        for (TimeItem timeItem : alertTimes) {
            items.add(TimeItem.encodeToString(timeItem));
        }
        Prefs.putStringArrayList(AppBase.getContext().getString(R.string.pref_reminder_alerts), "ALERTS" + uniqueName, items);
    }

    public static boolean logReminderShown(@NonNull String uniqueName, @NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime, boolean snoozed) {
        Log.v("REMINDER ITEM", "STATIC Log Show Reminder");
        ReminderLog reminderLog = loadFromFile(uniqueName);
        if (reminderLog == null) {
            Log.v("REMINDER ITEM", "No save, creating new one");
            reminderLog = new ReminderLog();
        }
        reminderLog.updateLog();
        reminderLog.logShown(dateTime, firstDateTime, snoozed);
        return saveToFile(reminderLog, uniqueName);
    }

    public static boolean logReminderClicked(@NonNull String uniqueName, @NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime, boolean snoozed) {
        Log.v("REMINDER ITEM", "Log Clicked Reminder");
        ReminderLog reminderLog = loadFromFile(uniqueName);
        if (reminderLog == null) {
            Log.v("REMINDER ITEM", "No save, creating new one");
            reminderLog = new ReminderLog();
        }
        reminderLog.updateLog();
        reminderLog.logClicked(dateTime, firstDateTime, snoozed);
        return saveToFile(reminderLog, uniqueName);
    }

    /**
     * Events to post
     *
     * @param event Enum to post
     */
    private static void postReminderItemEvent(@NonNull ReminderItemEvent event) {
        Bus.postEnum(event);
    }

    /**
     * Saves the reminder log to file
     *
     * @return True if successful
     */
    private static boolean saveToFile(ReminderLog reminderLog, String uniqueName) {
        byte[] bytes = Serializer.serializeClass(reminderLog);
        return bytes != null && FileWriter.writeFile("LOG" + uniqueName, bytes, FileWriter.FileLocation.INTERNAL);
    }

    /**
     * Loads the reminder log from file
     *
     * @return True if successful
     */
    private static ReminderLog loadFromFile(String uniqueName) {
        if (!FileWriter.doesFileExist("LOG" + uniqueName, FileWriter.FileLocation.INTERNAL)) {
            return new ReminderLog();
        }
        byte[] bytes = FileWriter.readFile("LOG" + uniqueName, FileWriter.FileLocation.INTERNAL);
        if (bytes == null) {
            return new ReminderLog();
        }
        return Serializer.deserializeClass(bytes, ReminderLog.class);
    }

    /**
     * Truncates the UUID for a unique id
     *
     * @return A unique notification id
     */
    public final int getNotificationId() {
        return uniqueName.hashCode();
    }

    /**
     * @return A deep copy of this reminder
     */
    @NonNull
    public final ReminderItem copy() {
        return new ReminderItem(uniqueName, version, title, messageList, messageInOrder,
                enabled, startTime, endTime, specificTimeList, numberPerDay,
                rangeTiming, daysToRun, weeksToRun, notificationTone, notificationToneName,
                notificationVibratePattern, notificationLED, notificationLEDColor,
                notificationPriority,
                notificationIcon, notificationAccentColor, snooze, autoSnooze);
    }

    /**
     * @return A deep copy of this reminder but with a new unique name
     */
    @NonNull
    public final ReminderItem duplicate() {
        return new ReminderItem(generateUniqueName(), version, title, messageList, messageInOrder,
                enabled, startTime, endTime, specificTimeList, numberPerDay,
                rangeTiming, daysToRun, weeksToRun, notificationTone, notificationToneName,
                notificationVibratePattern, notificationLED, notificationLEDColor,
                notificationPriority,
                notificationIcon, notificationAccentColor, snooze, autoSnooze);
    }

    /**
     * @return The notification tone as a Uri
     */
    public final Uri getNotificationTone() {
        if (notificationTone == null) {
            return null;
        }
        return Uri.parse(notificationTone);
    }

    /**
     * @param obj Object to compare to
     * @return True if equal based on unique id.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null && obj instanceof ReminderItem) {
            ReminderItem item = (ReminderItem) obj;
            return this.uniqueName.equals(item.uniqueName);
        } else {
            return false;
        }
    }

    /**
     * Generate new alert times given the current parameters
     */
    public final void updateAlertTimes() {
        if (!rangeTiming) {
            ArrayList<TimeItem> alertTimes = new ArrayList<>();
            for (TimeItem item : specificTimeList) {
                alertTimes.add(item.copy());
                Log.v(title, item.getHourInTimeFormatString() + ":" + item.getMinuteString());
            }
            ReminderItem.setAlertTimes(alertTimes, this.uniqueName);
            return;
        }
        int diff = getDiffMinutes();
        int startOffset = timeToMinutes(startTime);

        generateEvenishSplit(diff, startOffset, 0.5f, numberPerDay);
    }

    /**
     * Creates a distribution of values from offset to diff+offset
     *
     * @param diff        Total range of values
     * @param offset      Offset amount to shift the entire range
     * @param wiggle      Percent of space between items to allow randomization
     * @param numberItems Number of items to generate
     */
    private void generateEvenishSplit(int diff, int offset, float wiggle, int numberItems) {
        ArrayList<TimeItem> alertTimes = new ArrayList<>();
        Random rand = new Random();
        int itemSplit = Math.round((diff * 1.0f) / numberItems);
        int[] values = new int[numberItems];
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.round((i * 1.0f) / (numberItems) * diff) + itemSplit / 2 + Math.round((itemSplit * wiggle) * (rand.nextFloat() - 0.5f));
            if (i > 0) {
                values[i] = Math.min(Math.max(values[i], values[i - 1] + 5), diff);
            }
        }
        for (int value : values) {
            alertTimes.add(minutesToTimeItem(value + offset));
            Log.v(title, alertTimes.get(alertTimes.size() - 1).getHourInTimeFormatString() + ":" + alertTimes.get(alertTimes.size() - 1).getMinuteString());
        }
        ReminderItem.setAlertTimes(alertTimes, this.uniqueName);
    }

    /**
     * @return The difference in minutes
     */
    private int getDiffMinutes() {
        return timeToMinutes(endTime) - timeToMinutes(startTime);
    }

    /**
     * Converts a time reminder to minutes value
     *
     * @param time Time reminder
     * @return Value in minutes
     */
    private int timeToMinutes(@NonNull TimeItem time) {
        return time.hour * 60 + time.minute;
    }

    /**
     * Converts minutes to time reminder
     *
     * @param totalMinutes Minutes
     * @return Time reminder
     */
    private TimeItem minutesToTimeItem(int totalMinutes) {
        int hour = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return new TimeItem(hour, minutes);
    }

    private boolean isValidWeekToRun(DateTimeItem now) {
        if(weeksToRun[0]) {
            return true;
        }
        UtilsTime.WeekOfMonth week = UtilsTime.getWeekOfMonth(now.dateItem);
        Log.v("ReminderItem", "Week of month: "+week.name+" is last of month: "+UtilsTime.isLastWeekOfMonth(now.dateItem));
        if(weeksToRun[1] && week == UtilsTime.WeekOfMonth.FIRST) {
            return true;
        }
        if(weeksToRun[2] && week == UtilsTime.WeekOfMonth.SECOND) {
            return true;
        }
        if(weeksToRun[3] && week == UtilsTime.WeekOfMonth.THIRD) {
            return true;
        }
        if(weeksToRun[4] && week == UtilsTime.WeekOfMonth.FOURTH) {
            return true;
        }
        if(weeksToRun[5] && week == UtilsTime.WeekOfMonth.FIFTH) {
            return true;
        }
        if(weeksToRun[6] && UtilsTime.isLastWeekOfMonth(now.dateItem)) {
            return true;
        }
        return false;
    }

    public final void scheduleNextWake(DateTimeItem now) {
        if (!enabled) {
            return;
        }
        if (!daysToRun[UtilsTime.getCurrentDayOfWeek().getIndex()]) {
            return;
        }
        if(!isValidWeekToRun(now)) {
            return;
        }
        ArrayList<TimeItem> alertTimes = ReminderItem.getAlertTimes(uniqueName);
        if (alertTimes.isEmpty()) {
            return;
        }
        TimeItem time = null;
        for (TimeItem alertTime : alertTimes) {
            //alert time is after the current time
            if (!UtilsTime.timeBeforeOrEqual(alertTime, now.timeItem) || (now.timeItem.minute == 0 && now.timeItem.hour == 0)) {
                if (time == null || UtilsTime.timeBefore(alertTime, time)) {
                    time = alertTime;
                }
            }
        }
        if (time != null) {
            Scheduler.getInstance().scheduleWake(new DateTimeItem(now.dateItem, time), uniqueName);
        }
    }

    public final void deleteNextWake() {
        Scheduler.getInstance().cancelWake(uniqueName);
    }

    public final void rescheduleNextWake(DateTimeItem time) {
        deleteNextWake();
        scheduleNextWake(time);
    }

    public final NotificationContent getNotification(boolean preview, @NonNull DateTimeItem dateTime, boolean getCurrent, @Nullable DateTimeItem firstDateTime) {
        if(firstDateTime == null) {
            firstDateTime = dateTime;
        }

        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String title = this.title;
        if (title == null || title.isEmpty()) {
            title = AppBase.getContext().getString(R.string.title);
        }

        String content = "";

        if(messageList.size() >= 1) {
            if(getCurrent) {
                content = messageList.get(UtilsMath.inBoundsInt(0, messageList.size() - 1, getCurMessage()));
            } else {
                if (messageInOrder) {
                    int curMessage = getCurMessage() + 1;
                    if (curMessage >= messageList.size()) {
                        curMessage = 0;
                    }
                    content = messageList.get(curMessage);
                    setCurMessage(curMessage);
                } else {
                    Random rand = new Random();
                    int lastMessage = rand.nextInt(messageList.size());
                    setCurMessage(lastMessage);
                    content = messageList.get(lastMessage);
                }
            }
        }
        NotificationContent notif;
        String keySystem = AppBase.getContext().getString(R.string.pref_notification_custom);
        String keytheme = AppBase.getContext().getString(R.string.pref_notification_theme);
        NotificationContent.NotificationType type = NotificationContent.NotificationType.NORMAL;
        NotificationContent.NotificationTheme theme = NotificationContent.NotificationTheme.DARK;
        @DrawableRes int iconCancel, iconCheck, iconSnooze;

        if (Prefs.getBoolean(pref, keySystem, true)) {
            if(getSnooze() == SnoozeOptions.DISABLED) {
                type = NotificationContent.NotificationType.CUSTOM;
            } else {
                type = NotificationContent.NotificationType.CUSTOM_SNOOZE;
            }
        }
        if (Prefs.getBoolean(pref, keytheme, true)) {
            theme = NotificationContent.NotificationTheme.LIGHT;
            iconCancel = R.drawable.notif_cancel_light;
            iconCheck = R.drawable.notif_check_light;
            iconSnooze = R.drawable.notif_snooze_light;
        } else {
            iconCancel = R.drawable.notif_cancel_dark;
            iconCheck = R.drawable.notif_check_dark;
            iconSnooze = R.drawable.notif_snooze_dark;
        }

        notif = new NotificationContent(theme, type, title, content, this.getNotificationTone(), IconUtil.getIconRes(notificationIcon), notificationAccentColor, getNotificationId());

        String keyOnGoing = AppBase.getContext().getString(R.string.pref_notification_ongoing);
        notif.setOnGoing(Prefs.getBoolean(pref, keyOnGoing, false));

        notif.setVibrate(this.notificationVibratePattern);
        notif.setNotificationPriority(this.notificationPriority);

        if (this.notificationLED) {
            notif.enableLed(this.notificationLEDColor);
        }

        Intent intentClicked = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentClicked.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED");
        intentClicked.setType(this.uniqueName);
        intentClicked.putExtra("NAME", this.uniqueName);
        intentClicked.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentClicked.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentClicked.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentCancel = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentCancel.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_DELETED");
        intentCancel.setType(this.uniqueName);
        intentCancel.putExtra("NAME", this.uniqueName);
        intentCancel.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentCancel.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentCancel.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentDismiss = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentDismiss.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_DISMISS");
        intentDismiss.setType(this.uniqueName);
        intentDismiss.putExtra("NAME", this.uniqueName);
        intentDismiss.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentDismiss.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentDismiss.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentSnooze = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentSnooze.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_SNOOZE");
        intentSnooze.setType(this.uniqueName);
        intentSnooze.putExtra("NAME", this.uniqueName);
        intentSnooze.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentSnooze.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentSnooze.putExtra("NOTIFICATION_ID", getNotificationId());
        intentSnooze.putExtra("SNOOZE_LENGTH", snooze.minutes);

        Intent intentAck = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentAck.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_ACKNOWLEDGE");
        intentAck.setType(this.uniqueName);
        intentAck.putExtra("NAME", this.uniqueName);
        intentAck.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentAck.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentAck.putExtra("NOTIFICATION_ID", getNotificationId());

        if (preview) {
            intentClicked.putExtra("PREVIEW", true);
            intentCancel.putExtra("PREVIEW", true);
            intentDismiss.putExtra("PREVIEW", true);
            intentSnooze.putExtra("PREVIEW", true);
            intentAck.putExtra("PREVIEW", true);
        }

        String keySummary = AppBase.getContext().getString(R.string.pref_notification_click_ack);

        if (!Prefs.getBoolean(pref, keySummary, true)) {
            intentClicked.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED_SILENT");
        }

        PendingIntent pClicked = PendingIntent.getBroadcast(AppBase.getContext(), 0, intentClicked, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pCanceled = PendingIntent.getBroadcast(AppBase.getContext(), 0, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pDismiss = PendingIntent.getBroadcast(AppBase.getContext(), 0, intentDismiss, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pSnooze = PendingIntent.getBroadcast(AppBase.getContext(), 0, intentSnooze, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pAck = PendingIntent.getBroadcast(AppBase.getContext(), 0, intentAck, PendingIntent.FLAG_CANCEL_CURRENT);

        notif.addContentIntent(pClicked);
        notif.addDeleteIntent(pCanceled);
        notif.addAction(new NotificationAction(iconCancel, "", pDismiss));
        if(getSnooze() != SnoozeOptions.DISABLED) {
            notif.addAction(new NotificationAction(iconSnooze, "", pSnooze));
        }
        notif.addAction(new NotificationAction(iconCheck, "", pAck));

        return notif;
    }

    public final void deleteReminderLog() {
        FileWriter.deleteFile("LOG" + uniqueName, FileWriter.FileLocation.INTERNAL);
    }

    public final void deleteAlertTimes() {
        Prefs.deleteStringArrayList(AppBase.getContext().getString(R.string.pref_reminder_alerts), "ALERTS" + uniqueName);
    }

    public final boolean hasReminderLog() {
        return reminderLog != null && reminderLog.days != null;
    }

    @NonNull
    public final byte[] getReminderLogCsv() {
        String append = "\n";
        String log = "id,datestamp, datestampformat,timestamp, timestampformat,type"+append;
        for(ReminderLogDay day : reminderLog.days) {
            String prepend = uniqueName+","+UtilsTime.getTimeMillis(day.date)+","+UtilsTime.getShortDateFormatted(day.date)+",";
            for(ReminderLogItem item : day.getItemList()) {
                log += prepend;
                log += UtilsTime.getTimeMillis(item.dateTime)+",";
                log += UtilsTime.getShortDateTimeFormatted(item.dateTime)+",";
                log += item.type.name;
                log += append;
            }
        }
        return log.getBytes(Charset.forName(Utils.stringType));
    }

    /**
     * Loads the reminder list from disk. If already loaded it posts instantly
     * subscribe to Event.DATA_LOAD_PASS and Event.DATA_LOAD_FAIL for callbacks
     *
     * @param forceRefresh True to force reload from disk
     */
    public final void loadData(boolean forceRefresh) {
        if (!forceRefresh && hasReminderLog()) {
            postReminderItemEvent(ReminderItemEvent.DATA_LOAD_PASS);
        } else {
            taskLoad = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    ReminderLog log = loadFromFile(uniqueName);
                    if (log != null) {
                        reminderLog = log;
                        reminderLog.updateLog();
                        return true;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if (value) {
                        postReminderItemEvent(ReminderItemEvent.DATA_LOAD_PASS);
                    } else {
                        postReminderItemEvent(ReminderItemEvent.DATA_LOAD_FAIL);
                    }
                    taskLoad = null;
                }
            };
            taskLoad.execute();
        }
    }

    public final void updateVersion() {
        //Change how curMessage works
        if(messageList == null) {
            messageList = new ArrayList<>();
        }
        if(version == 0) {
            int curMessage = getCurMessage();
            if(curMessage >= messageList.size()) {
                curMessage = 0;
            }
            setCurMessage(curMessage);
        }
        version = CURRENT_VERSION;
    }

    /**
     * Events
     */
    public enum ReminderItemEvent {
        DATA_LOAD_PASS,
        DATA_LOAD_FAIL,
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

}
