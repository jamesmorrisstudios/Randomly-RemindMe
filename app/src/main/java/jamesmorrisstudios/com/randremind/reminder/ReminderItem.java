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
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Serializer;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.filewriting.FileWriter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
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

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.receiver.NotificationReceiver;
import jamesmorrisstudios.com.randremind.util.IconUtil;

/**
 * Handler that has a reminder item data object set to it to work with it
 * <p/>
 * Created by James on 4/20/2015.
 */
public final class ReminderItem extends BaseRecycleItem {
    private AsyncTask<Void, Void, Boolean> taskLoad = null;
    private ReminderItemData reminderItemData = null;

    //Set if any entry is dirty and was changed
    private boolean titleDirty = false;
    private boolean messageListDirty = false;
    private boolean messageInOrderDirty = false;
    private boolean enabledDirty = false;
    private boolean startTimeDirty = false;
    private boolean endTimeDirty = false;
    private boolean specificTimeListDirty = false;
    private boolean numberPerDayDirty = false;
    private boolean rangeTimingDirty = false;
    private boolean daysToRunDirty = false;
    private boolean weeksToRunDirty = false;
    private boolean notificationToneDirty = false;
    private boolean notificationToneNameDirty = false;
    private boolean notificationVibratePatternDirty = false;
    private boolean notificationLEDDirty = false;
    private boolean notificationLEDColorDirty = false;
    private boolean notificationPriorityDirty = false;
    private boolean notificationIconDirty = false;
    private boolean notificationAccentColorDirty = false;
    private boolean snoozeDirty = false;
    private boolean autoSnoozeDirty = false;
    private boolean curMessageDirty = false;
    private boolean alertTimesDirty = false;
    private boolean notifCounterDirty = false;

    //This is always a deep copy of the reminder item data. The original is NEVER given to this
    public final void setReminderItemData(ReminderItemData reminderItemData) {
        clearDirty();
        this.reminderItemData = reminderItemData;
    }

    public final void clearReminderItemData() {
        clearDirty();
        this.reminderItemData = null;
    }

    //Copies any changed data into the given object
    public final void commitChanges(ReminderItemData newData) {
        if(titleDirty) {
            newData.title = reminderItemData.title;
        }
        if(messageListDirty) {
            newData.messageList = new ArrayList<>(reminderItemData.messageList);
        }
        if(messageInOrderDirty) {
            newData.messageInOrder = reminderItemData.messageInOrder;
        }
        if(enabledDirty) {
            newData.enabled = reminderItemData.enabled;
        }
        if(startTimeDirty) {
            newData.startTime = reminderItemData.startTime.copy();
        }
        if(endTimeDirty) {
            newData.endTime = reminderItemData.endTime.copy();
        }
        if(specificTimeListDirty) {
            newData.specificTimeList = UtilsTime.cloneArrayListTime(reminderItemData.specificTimeList);
        }
        if(numberPerDayDirty) {
            newData.numberPerDay = reminderItemData.numberPerDay;
        }
        if(rangeTimingDirty) {
            newData.rangeTiming = reminderItemData.rangeTiming;
        }
        if(daysToRunDirty) {
            newData.daysToRun = reminderItemData.daysToRun.clone();
        }
        if(weeksToRunDirty) {
            newData.weeksToRun = reminderItemData.weeksToRun.clone();
        }
        if(notificationToneDirty) {
            newData.notificationTone = reminderItemData.notificationTone;
        }
        if(notificationToneNameDirty) {
            newData.notificationToneName = reminderItemData.notificationToneName;
        }
        if(notificationVibratePatternDirty) {
            newData.notificationVibratePattern = reminderItemData.notificationVibratePattern;
        }
        if(notificationLEDDirty) {
            newData.notificationLED = reminderItemData.notificationLED;
        }
        if(notificationLEDColorDirty) {
            newData.notificationLEDColor = reminderItemData.notificationLEDColor;
        }
        if(notificationPriorityDirty) {
            newData.notificationPriority = reminderItemData.notificationPriority;
        }
        if(notificationIconDirty) {
            newData.notificationIcon = reminderItemData.notificationIcon;
        }
        if(notificationAccentColorDirty) {
            newData.notificationAccentColor = reminderItemData.notificationAccentColor;
        }
        if(snoozeDirty) {
            newData.snooze = reminderItemData.snooze;
        }
        if(autoSnoozeDirty) {
            newData.autoSnooze = reminderItemData.autoSnooze;
        }
        if(curMessageDirty) {
            newData.curMessage = reminderItemData.curMessage;
        }
        if(alertTimesDirty) {
            newData.alertTimes = reminderItemData.alertTimes;
        }
        if(notifCounterDirty) {
            newData.notifCounter = reminderItemData.notifCounter;
        }
    }

    public final boolean isAnyDirty() {
        return titleDirty |
        messageListDirty |
        messageInOrderDirty |
        enabledDirty |
        startTimeDirty |
        endTimeDirty |
        specificTimeListDirty |
        numberPerDayDirty |
        rangeTimingDirty |
        daysToRunDirty |
        weeksToRunDirty |
        notificationToneDirty |
        notificationToneNameDirty |
        notificationVibratePatternDirty |
        notificationLEDDirty |
        notificationLEDColorDirty |
        notificationPriorityDirty |
        notificationIconDirty |
        notificationAccentColorDirty |
        snoozeDirty |
        autoSnoozeDirty |
        curMessageDirty |
        alertTimesDirty |
        notifCounterDirty;
    }

    public void clearDirty() {
        titleDirty = false;
        messageListDirty = false;
        messageInOrderDirty = false;
        enabledDirty = false;
        startTimeDirty = false;
        endTimeDirty = false;
        specificTimeListDirty = false;
        numberPerDayDirty = false;
        rangeTimingDirty = false;
        daysToRunDirty = false;
        weeksToRunDirty = false;
        notificationToneDirty = false;
        notificationToneNameDirty = false;
        notificationVibratePatternDirty = false;
        notificationLEDDirty = false;
        notificationLEDColorDirty = false;
        notificationPriorityDirty = false;
        notificationIconDirty = false;
        notificationAccentColorDirty = false;
        snoozeDirty = false;
        autoSnoozeDirty = false;
        curMessageDirty = false;
        alertTimesDirty = false;
        notifCounterDirty = false;
    }

    public String getUniqueName() {
        return reminderItemData.uniqueName;
    }

    public String getTitle() {
        return reminderItemData.title;
    }

    public void setTitle(String title) {
        if(reminderItemData.title == null || !reminderItemData.title.equals(title)) {
            titleDirty = true;
            reminderItemData.title = title;
        }
    }

    public boolean isEnabled() {
        return reminderItemData.enabled;
    }

    public void setEnabled(boolean enabled) {
        if(reminderItemData.enabled != enabled) {
            enabledDirty = true;
            reminderItemData.enabled = enabled;
        }
    }

    public ReminderItemData.SnoozeOptions getSnooze() {
        return reminderItemData.snooze;
    }

    public void setSnooze(@NonNull ReminderItemData.SnoozeOptions snooze) {
        snoozeDirty = true;
        reminderItemData.snooze = snooze;
    }

    public ReminderItemData.SnoozeOptions getAutoSnooze() {
        return reminderItemData.autoSnooze;
    }

    public void setAutoSnooze(@NonNull ReminderItemData.SnoozeOptions autoSnooze) {
        autoSnoozeDirty = true;
        reminderItemData.autoSnooze = autoSnooze;
    }

    public ArrayList<String> getMessageList() {
        return reminderItemData.messageList;
    }

    public void setMessageList(ArrayList<String> messageList) {
        messageListDirty = true;
        reminderItemData.messageList = messageList;
    }

    public boolean isMessageInOrder() {
        return reminderItemData.messageInOrder;
    }

    public void setMessageInOrder(boolean messageInOrder) {
        if(reminderItemData.messageInOrder != messageInOrder) {
            messageInOrderDirty = true;
            reminderItemData.messageInOrder = messageInOrder;
        }
    }

    public TimeItem getStartTime() {
        return reminderItemData.startTime;
    }

    public TimeItem updateStartTime() {
        startTimeDirty = true;
        return reminderItemData.startTime;
    }

    public TimeItem getEndTime() {
        return reminderItemData.endTime;
    }

    public TimeItem updateEndTime() {
        endTimeDirty = true;
        return reminderItemData.endTime;
    }

    public ArrayList<TimeItem> getSpecificTimeList() {
        return reminderItemData.specificTimeList;
    }

    public void setSpecificTimeList(ArrayList<TimeItem> specificTimeList) {
        specificTimeListDirty = true;
        reminderItemData.specificTimeList = specificTimeList;
    }

    public int getNumberPerDay() {
        return reminderItemData.numberPerDay;
    }

    public void setNumberPerDay(int numberPerDay) {
        if(reminderItemData.numberPerDay != numberPerDay) {
            numberPerDayDirty = true;
            reminderItemData.numberPerDay = numberPerDay;
        }
    }

    public boolean isRangeTiming() {
        return reminderItemData.rangeTiming;
    }

    public void setRangeTiming(boolean rangeTiming) {
        if(reminderItemData.rangeTiming != rangeTiming) {
            rangeTimingDirty = true;
            reminderItemData.rangeTiming = rangeTiming;
        }
    }

    public boolean[] getDaysToRun() {
        return reminderItemData.daysToRun;
    }

    public boolean[] updateDaysToRun() {
        daysToRunDirty = true;
        return reminderItemData.daysToRun;
    }

    public void setDaysToRun(boolean[] daysToRun) {
        daysToRunDirty = true;
        reminderItemData.daysToRun = daysToRun;
    }

    public boolean[] getWeeksToRun() {
        return reminderItemData.weeksToRun;
    }

    public void setWeeksToRun(boolean[] weeksToRun) {
        weeksToRunDirty = true;
        reminderItemData.weeksToRun = weeksToRun;
    }


    public void setNotificationTone(String notificationTone) {
        if(reminderItemData.notificationTone == null || !reminderItemData.notificationTone.equals(notificationTone)) {
            notificationToneDirty = true;
            reminderItemData.notificationTone = notificationTone;
        }
    }

    public String getNotificationToneName() {
        return reminderItemData.notificationToneName;
    }

    public void setNotificationToneName(String notificationToneName) {
        if(reminderItemData.notificationToneName == null || !reminderItemData.notificationToneName.equals(notificationToneName)) {
            notificationToneNameDirty = true;
            reminderItemData.notificationToneName = notificationToneName;
        }
    }

    public NotificationVibrate getNotificationVibratePattern() {
        return reminderItemData.notificationVibratePattern;
    }

    public void setNotificationVibratePattern(NotificationVibrate notificationVibratePattern) {
        if(reminderItemData.notificationVibratePattern != notificationVibratePattern) {
            notificationVibratePatternDirty = true;
            reminderItemData.notificationVibratePattern = notificationVibratePattern;
        }
    }

    public boolean isNotificationLED() {
        return reminderItemData.notificationLED;
    }

    public void setNotificationLED(boolean notificationLED) {
        if(reminderItemData.notificationLED != notificationLED) {
            notificationLEDDirty = true;
            reminderItemData.notificationLED = notificationLED;
        }
    }

    public int getNotificationLEDColor() {
        return reminderItemData.notificationLEDColor;
    }

    public void setNotificationLEDColor(int notificationLEDColor) {
        if(reminderItemData.notificationLEDColor != notificationLEDColor) {
            notificationLEDColorDirty = true;
            reminderItemData.notificationLEDColor = notificationLEDColor;
        }
    }

    public NotificationPriority getNotificationPriority() {
        return reminderItemData.notificationPriority;
    }

    public void setNotificationPriority(NotificationPriority notificationPriority) {
        if(reminderItemData.notificationPriority != notificationPriority) {
            notificationPriorityDirty = true;
            reminderItemData.notificationPriority = notificationPriority;
        }
    }

    public int getNotificationIcon() {
        return reminderItemData.notificationIcon;
    }

    public void setNotificationIcon(int notificationIcon) {
        if(reminderItemData.notificationIcon != notificationIcon) {
            notificationIconDirty = true;
            reminderItemData.notificationIcon = notificationIcon;
        }
    }

    public int getNotificationAccentColor() {
        return reminderItemData.notificationAccentColor;
    }

    public void setNotificationAccentColor(int notificationAccentColor) {
        if(reminderItemData.notificationAccentColor != notificationAccentColor) {
            notificationAccentColorDirty = true;
            reminderItemData.notificationAccentColor = notificationAccentColor;
        }
    }

    public int getCurMessage() {
        return reminderItemData.curMessage;
    }

    public void setCurMessage(int curMessage) {
        if(reminderItemData.curMessage != curMessage) {
            curMessageDirty = true;
            reminderItemData.curMessage = curMessage;
        }
    }

    public final void saveReminderLog() {
        if(reminderItemData == null || reminderItemData.reminderLog == null) {
            return;
        }
        saveToFile(reminderItemData.reminderLog, reminderItemData.uniqueName);
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
        return reminderItemData.uniqueName.hashCode();
    }

    /**
     * @return The notification tone as a Uri
     */
    public final Uri getNotificationTone() {
        if (reminderItemData.notificationTone == null) {
            return null;
        }
        return Uri.parse(reminderItemData.notificationTone);
    }

    /**
     * Generate new alert times given the current parameters
     */
    public final void updateAlertTimes() {
        alertTimesDirty = true;
        if (!reminderItemData.rangeTiming) {
            reminderItemData.alertTimes = new ArrayList<>();
            for (TimeItem item : reminderItemData.specificTimeList) {
                reminderItemData.alertTimes.add(item.copy());
                Log.v(reminderItemData.title, item.getHourInTimeFormatString() + ":" + item.getMinuteString());
            }
            return;
        }
        int diff = getDiffMinutes();
        int startOffset = timeToMinutes(reminderItemData.startTime);

        generateEvenishSplit(diff, startOffset, 0.5f, reminderItemData.numberPerDay);
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
        reminderItemData.alertTimes = new ArrayList<>();
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
            reminderItemData.alertTimes.add(minutesToTimeItem(value + offset));
            Log.v(reminderItemData.title, reminderItemData.alertTimes.get(reminderItemData.alertTimes.size() - 1).getHourInTimeFormatString() + ":" + reminderItemData.alertTimes.get(reminderItemData.alertTimes.size() - 1).getMinuteString());
        }
    }

    public final ReminderLog getReminderLog() {
        return reminderItemData.reminderLog;
    }

    /**
     * @return The difference in minutes
     */
    private int getDiffMinutes() {
        return timeToMinutes(reminderItemData.endTime) - timeToMinutes(reminderItemData.startTime);
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
        if(reminderItemData.weeksToRun[0]) {
            return true;
        }
        UtilsTime.WeekOfMonth week = UtilsTime.getWeekOfMonth(now.dateItem);
        Log.v("ReminderItem", "Week of month: "+week.name+" is last of month: "+UtilsTime.isLastWeekOfMonth(now.dateItem));
        if(reminderItemData.weeksToRun[1] && week == UtilsTime.WeekOfMonth.FIRST) {
            return true;
        }
        if(reminderItemData.weeksToRun[2] && week == UtilsTime.WeekOfMonth.SECOND) {
            return true;
        }
        if(reminderItemData.weeksToRun[3] && week == UtilsTime.WeekOfMonth.THIRD) {
            return true;
        }
        if(reminderItemData.weeksToRun[4] && week == UtilsTime.WeekOfMonth.FOURTH) {
            return true;
        }
        if(reminderItemData.weeksToRun[5] && week == UtilsTime.WeekOfMonth.FIFTH) {
            return true;
        }
        if(reminderItemData.weeksToRun[6] && UtilsTime.isLastWeekOfMonth(now.dateItem)) {
            return true;
        }
        return false;
    }

    public final void scheduleNextWake(DateTimeItem now) {
        if (!reminderItemData.enabled) {
            return;
        }
        if (!reminderItemData.daysToRun[UtilsTime.getCurrentDayOfWeek().getIndex()]) {
            return;
        }
        if(!isValidWeekToRun(now)) {
            return;
        }
        if (reminderItemData.alertTimes.isEmpty()) {
            return;
        }
        TimeItem time = null;
        for (TimeItem alertTime : reminderItemData.alertTimes) {
            //alert time is after the current time
            if (!UtilsTime.timeBeforeOrEqual(alertTime, now.timeItem) || (now.timeItem.minute == 0 && now.timeItem.hour == 0)) {
                if (time == null || UtilsTime.timeBefore(alertTime, time)) {
                    time = alertTime;
                }
            }
        }
        if (time != null) {
            Scheduler.getInstance().scheduleWake(new DateTimeItem(now.dateItem, time), reminderItemData.uniqueName);
        }
    }

    public final void deleteNextWake() {
        Scheduler.getInstance().cancelWake(reminderItemData.uniqueName);
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
        String title = reminderItemData.title;
        if (title == null || title.isEmpty()) {
            title = AppBase.getContext().getString(R.string.title);
        }

        String content = "";

        if(reminderItemData.messageList.size() >= 1) {
            if(getCurrent) {
                content = reminderItemData.messageList.get(UtilsMath.inBoundsInt(0, reminderItemData.messageList.size() - 1, reminderItemData.curMessage));
            } else {
                curMessageDirty = true;
                if (reminderItemData.messageInOrder) {
                    int curMessage = reminderItemData.curMessage + 1;
                    if (curMessage >= reminderItemData.messageList.size()) {
                        curMessage = 0;
                    }
                    content = reminderItemData.messageList.get(curMessage);
                    reminderItemData.curMessage = curMessage;
                } else {
                    Random rand = new Random();
                    int lastMessage = rand.nextInt(reminderItemData.messageList.size());
                    reminderItemData.curMessage = lastMessage;
                    content = reminderItemData.messageList.get(lastMessage);
                }
            }
        }

        reminderItemData.notifCounter++;
        if(reminderItemData.notifCounter > 5) {
            reminderItemData.notifCounter = 0;
        }

        for(int i=0; i<reminderItemData.notifCounter; i++) {
            content += " ";
        }

        NotificationContent notif;
        String keySystem = AppBase.getContext().getString(R.string.pref_notification_custom);
        String keytheme = AppBase.getContext().getString(R.string.pref_notification_theme);
        NotificationContent.NotificationType type = NotificationContent.NotificationType.NORMAL;
        NotificationContent.NotificationTheme theme = NotificationContent.NotificationTheme.DARK;
        @DrawableRes int iconCancel, iconCheck, iconSnooze;

        if (Prefs.getBoolean(pref, keySystem, true)) {
            if(getSnooze() == ReminderItemData.SnoozeOptions.DISABLED) {
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

        notif = new NotificationContent(theme, type, title, content, this.getNotificationTone(), IconUtil.getIconRes(reminderItemData.notificationIcon), reminderItemData.notificationAccentColor, getNotificationId(), reminderItemData.notifCounter);

        String keyOnGoing = AppBase.getContext().getString(R.string.pref_notification_ongoing);
        notif.setOnGoing(Prefs.getBoolean(pref, keyOnGoing, false));

        notif.setVibrate(reminderItemData.notificationVibratePattern);
        notif.setNotificationPriority(reminderItemData.notificationPriority);

        if (reminderItemData.notificationLED) {
            notif.enableLed(reminderItemData.notificationLEDColor);
        }

        Intent intentClicked = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentClicked.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED");
        intentClicked.setType(reminderItemData.uniqueName);
        intentClicked.putExtra("NAME", reminderItemData.uniqueName);
        intentClicked.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentClicked.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentClicked.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentCancel = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentCancel.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_DELETED");
        intentCancel.setType(reminderItemData.uniqueName);
        intentCancel.putExtra("NAME", reminderItemData.uniqueName);
        intentCancel.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentCancel.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentCancel.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentDismiss = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentDismiss.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_DISMISS");
        intentDismiss.setType(reminderItemData.uniqueName);
        intentDismiss.putExtra("NAME", reminderItemData.uniqueName);
        intentDismiss.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentDismiss.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentDismiss.putExtra("NOTIFICATION_ID", getNotificationId());

        Intent intentSnooze = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentSnooze.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_SNOOZE");
        intentSnooze.setType(reminderItemData.uniqueName);
        intentSnooze.putExtra("NAME", reminderItemData.uniqueName);
        intentSnooze.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        intentSnooze.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        intentSnooze.putExtra("NOTIFICATION_ID", getNotificationId());
        intentSnooze.putExtra("SNOOZE_LENGTH", reminderItemData.snooze.minutes);

        Intent intentAck = new Intent(AppBase.getContext(), NotificationReceiver.class);
        intentAck.setAction("jamesmorrisstudios.com.randremind.NOTIFICATION_ACKNOWLEDGE");
        intentAck.setType(reminderItemData.uniqueName);
        intentAck.putExtra("NAME", reminderItemData.uniqueName);
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
        if(getSnooze() != ReminderItemData.SnoozeOptions.DISABLED) {
            notif.addAction(new NotificationAction(iconSnooze, "", pSnooze));
        }
        notif.addAction(new NotificationAction(iconCheck, "", pAck));

        return notif;
    }

    public final void deleteReminderLog() {
        FileWriter.deleteFile("LOG" + reminderItemData.uniqueName, FileWriter.FileLocation.INTERNAL);
    }

    public final boolean hasReminderLog() {
        return reminderItemData.reminderLog != null && reminderItemData.reminderLog.days != null;
    }

    @NonNull
    public final byte[] getReminderLogCsv() {
        String append = "\n";
        String log = "id,datestamp, datestampformat,timestamp, timestampformat,type"+append;
        for(ReminderLogDay day : reminderItemData.reminderLog.days) {
            String prepend = reminderItemData.uniqueName+","+UtilsTime.getTimeMillis(day.date)+","+UtilsTime.getShortDateFormatted(day.date)+",";
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
    public final void loadReminderLogData(boolean forceRefresh) {
        if (!forceRefresh && hasReminderLog()) {
            postReminderItemEvent(ReminderItemEvent.DATA_LOAD_PASS);
        } else {
            taskLoad = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return loadReminderLogDataSync();
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

    public final boolean loadReminderLogDataSync() {
        ReminderLog log = loadFromFile(reminderItemData.uniqueName);
        if (log != null) {
            reminderItemData.reminderLog = log;
            reminderItemData.reminderLog.updateLog();
            return true;
        }
        return false;
    }

    public final void updateVersion() {
        //Change how curMessage works
        if(reminderItemData.messageList == null) {
            reminderItemData.messageList = new ArrayList<>();
        }
        if(reminderItemData.alertTimes == null) {
            Log.v("ReminderItem", "Updating to newest alert times system");
            reminderItemData.alertTimes = new ArrayList<>();
            updateAlertTimes();
            rescheduleNextWake(UtilsTime.getDateTimeNow());
        }
        reminderItemData.version = ReminderItemData.CURRENT_VERSION;
    }

    /**
     * Events
     */
    public enum ReminderItemEvent {
        DATA_LOAD_PASS,
        DATA_LOAD_FAIL,
    }



}
