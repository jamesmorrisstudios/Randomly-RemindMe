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
import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
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

    //Title
    private boolean titleDirty = false;
    private boolean enabledDirty = false;
    //Messages
    private boolean messageListDirty = false;
    private boolean messageInOrderDirty = false;
    //Timing
    private boolean startDateDirty = false;
    private boolean endEnableDirty = false;
    private boolean endDateDirty = false;
    //Criteria
    private boolean startTimeDirty = false;
    private boolean endTimeDirty = false;
    private boolean filterTypeDirty = false;
    private boolean daysOfWeekDirty = false;
    private boolean daysOfMonthDirty = false;
    private boolean weeksOfMonthDirty = false;
    private boolean monthsOfYearDirty = false;
    private boolean repeatCountDirty = false;
    private boolean repeatTypeDirty = false;
    private boolean daysOfYearDirty = false;
    //Triggers
    private boolean triggerModeDirty = false;
    private boolean triggerCountDirty = false;
    private boolean triggerPeriodDirty = false;
    private boolean specificTimeListDirty = false;
    private boolean intervalPeriodDirty = false;
    private boolean intervalCountDirty = false;
    //Notifications
    private boolean notificationToneDirty = false;
    private boolean notificationToneNameDirty = false;
    private boolean notificationVibratePatternDirty = false;
    private boolean notificationLEDDirty = false;
    private boolean notificationLEDColorDirty = false;
    private boolean notificationPriorityDirty = false;
    private boolean notificationIconDirty = false;
    private boolean notificationAccentColorDirty = false;
    //Snooze
    private boolean snoozeDirty = false;
    private boolean autoSnoozeDirty = false;
    //State management
    private boolean curMessageDirty = false;
    private boolean alertTimesDirty = false;
    //Options specific to a reminder
    private boolean showAdvancedDirty = false;
    //Side counter data
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

    public final void commitChanges(ReminderItemData newData) {
        //General
        if(titleDirty) {
            newData.title = reminderItemData.title;
        }
        if(enabledDirty) {
            newData.enabled = reminderItemData.enabled;
        }
        //Messages
        if(messageListDirty) {
            newData.messageList = new ArrayList<>(reminderItemData.messageList);
        }
        if(messageInOrderDirty) {
            newData.messageInOrder = reminderItemData.messageInOrder;
        }
        //Timing
        if(startDateDirty) {
            newData.startDate = new DateItem(reminderItemData.startDate);
        }
        if(endEnableDirty) {
            newData.endEnable = reminderItemData.endEnable;
        }
        if(endDateDirty) {
            newData.endDate = new DateItem(reminderItemData.endDate);
        }
        //Criteria
        if(startTimeDirty) {
            newData.startTime = reminderItemData.startTime.copy();
        }
        if(endTimeDirty) {
            newData.endTime = reminderItemData.endTime.copy();
        }
        if(filterTypeDirty) {
            newData.filterType = reminderItemData.filterType;
        }
        if(daysOfWeekDirty) {
            newData.daysOfWeek = reminderItemData.daysOfWeek.clone();
        }
        if(daysOfMonthDirty) {
            newData.daysOfMonth = reminderItemData.daysOfMonth.clone();
        }
        if(weeksOfMonthDirty) {
            newData.weeksOfMonth = reminderItemData.weeksOfMonth.clone();
        }
        if(monthsOfYearDirty) {
            newData.monthsOfYear = reminderItemData.monthsOfYear.clone();
        }
        if(repeatCountDirty) {
            newData.repeatCount = reminderItemData.repeatCount;
        }
        if(repeatTypeDirty) {
            newData.repeatType = reminderItemData.repeatType;
        }
        if(daysOfYearDirty) {
            newData.daysOfYear = UtilsTime.cloneArrayListDate(reminderItemData.daysOfYear);
        }
        //Triggers
        if(triggerModeDirty) {
            newData.triggerMode = reminderItemData.triggerMode;
        }
        if(triggerCountDirty) {
            newData.triggerCount = reminderItemData.triggerCount;
        }
        if(triggerPeriodDirty) {
            newData.triggerPeriod = reminderItemData.triggerPeriod;
        }
        if(specificTimeListDirty) {
            newData.specificTimeList = UtilsTime.cloneArrayListTime(reminderItemData.specificTimeList);
        }
        if(intervalPeriodDirty) {
            newData.intervalPeriod = reminderItemData.intervalPeriod;
        }
        if(intervalCountDirty) {
            newData.intervalCount = reminderItemData.intervalCount;
        }
        //Notifications
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
        //Snooze
        if(snoozeDirty) {
            newData.snooze = reminderItemData.snooze;
        }
        if(autoSnoozeDirty) {
            newData.autoSnooze = reminderItemData.autoSnooze;
        }
        //State management
        if(curMessageDirty) {
            newData.curMessage = reminderItemData.curMessage;
        }
        if(alertTimesDirty) {
            newData.alertTimes = new ArrayList<>(reminderItemData.alertTimes);
        }
        //Options specific to a reminder
        if(showAdvancedDirty) {
            newData.showAdvanced = reminderItemData.showAdvanced;
        }
        //Side counter data
        if(notifCounterDirty) {
            newData.notifCounter = reminderItemData.notifCounter;
        }
    }

    public final boolean isAnyDirty() {
        return
                //Title
                titleDirty |
                enabledDirty |
                //Messages
                messageListDirty |
                messageInOrderDirty |
                //Timing
                startDateDirty |
                endEnableDirty |
                endDateDirty |
                //Criteria
                startTimeDirty |
                endTimeDirty |
                filterTypeDirty |
                daysOfWeekDirty |
                daysOfMonthDirty |
                weeksOfMonthDirty |
                monthsOfYearDirty |
                repeatCountDirty |
                repeatTypeDirty |
                daysOfYearDirty |
                //Triggers
                triggerModeDirty |
                triggerCountDirty |
                triggerPeriodDirty |
                specificTimeListDirty |
                intervalPeriodDirty |
                intervalCountDirty |
                //Notifications
                notificationToneDirty |
                notificationToneNameDirty |
                notificationVibratePatternDirty |
                notificationLEDDirty |
                notificationLEDColorDirty |
                notificationPriorityDirty |
                notificationIconDirty |
                notificationAccentColorDirty |
                //Snooze
                snoozeDirty |
                autoSnoozeDirty |
                //State management
                curMessageDirty |
                alertTimesDirty |
                //Options specific to a reminder
                showAdvancedDirty |
                //Side counter data
                notifCounterDirty;
    }

    public void clearDirty() {
        //Title
        titleDirty = false;
        enabledDirty = false;
        //Messages
        messageListDirty = false;
        messageInOrderDirty = false;
        //Timing
        startDateDirty = false;
        endEnableDirty = false;
        endDateDirty = false;
        //Criteria
        startTimeDirty = false;
        endTimeDirty = false;
        filterTypeDirty = false;
        daysOfWeekDirty = false;
        daysOfMonthDirty = false;
        weeksOfMonthDirty = false;
        monthsOfYearDirty = false;
        repeatCountDirty = false;
        repeatTypeDirty = false;
        daysOfYearDirty = false;
        //Triggers
        triggerModeDirty = false;
        triggerCountDirty = false;
        triggerPeriodDirty = false;
        specificTimeListDirty = false;
        intervalPeriodDirty = false;
        intervalCountDirty = false;
        //Notifications
        notificationToneDirty = false;
        notificationToneNameDirty = false;
        notificationVibratePatternDirty = false;
        notificationLEDDirty = false;
        notificationLEDColorDirty = false;
        notificationPriorityDirty = false;
        notificationIconDirty = false;
        notificationAccentColorDirty = false;
        //Snooze
        snoozeDirty = false;
        autoSnoozeDirty = false;
        //State management
        curMessageDirty = false;
        alertTimesDirty = false;
        //Options specific to a reminder
        showAdvancedDirty = false;
        //Side counter data
        notifCounterDirty = false;
    }

    //Name and Version
    public String getUniqueName() {
        return reminderItemData.uniqueName;
    }

    public int getVersion() {
        return reminderItemData.version;
    }

    //General
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

    //Messages
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

    //Timing
    public DateItem getStartDate() {
        return reminderItemData.startDate;
    }

    public DateItem updateStartDate() {
        startDateDirty = true;
        return reminderItemData.startDate;
    }

    public boolean isEndEnable() {
        return reminderItemData.endEnable;
    }

    public void setEndEnable(boolean endEnable) {
        if(reminderItemData.endEnable != endEnable) {
            endEnableDirty = true;
            reminderItemData.endEnable = endEnable;
        }
    }

    public DateItem getEndDate() {
        return reminderItemData.endDate;
    }

    public DateItem updateEndDate() {
        endDateDirty = true;
        return reminderItemData.endDate;
    }

    //Criteria
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

    public ReminderItemData.FilterType getFilterType() {
        return reminderItemData.filterType;
    }

    public void setFilterType(ReminderItemData.FilterType filterType) {
        filterTypeDirty = true;
        reminderItemData.filterType = filterType;
    }

    public boolean[] getDaysOfWeek() {
        return reminderItemData.daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) {
        daysOfWeekDirty = true;
        reminderItemData.daysOfWeek = daysOfWeek;
    }

    public boolean[] getDaysOfMonth() {
        return reminderItemData.daysOfMonth;
    }

    public void setDaysOfMonth(boolean[] daysOfMonth) {
        daysOfMonthDirty = true;
        reminderItemData.daysOfMonth = daysOfMonth;
    }

    public boolean[] getWeeksOfMonth() {
        return reminderItemData.weeksOfMonth;
    }

    public void setWeeksOfMonth(boolean[] weeksOfMonth) {
        weeksOfMonthDirty = true;
        reminderItemData.weeksOfMonth = weeksOfMonth;
    }

    public boolean[] getMonthsOfYear() {
        return reminderItemData.monthsOfYear;
    }

    public void setMonthsOfYear(boolean[] monthsOfYear) {
        monthsOfYearDirty = true;
        reminderItemData.monthsOfYear = monthsOfYear;
    }

    public int getRepeatCount() {
        return reminderItemData.repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        if(reminderItemData.repeatCount != repeatCount) {
            repeatCountDirty = true;
            reminderItemData.repeatCount = repeatCount;
        }
    }

    public ReminderItemData.RepeatType getRepeatType() {
        return reminderItemData.repeatType;
    }

    public void setRepeatType(ReminderItemData.RepeatType repeatType) {
        if(reminderItemData.repeatType != repeatType) {
            repeatTypeDirty = true;
            reminderItemData.repeatType = repeatType;
        }
    }

    public ArrayList<DateItem> getDaysOfYear() {
        return reminderItemData.daysOfYear;
    }

    public void setDaysOfYear(ArrayList<DateItem> daysOfYear) {
        daysOfYearDirty = true;
        reminderItemData.daysOfYear = daysOfYear;
    }

    //Triggers

    public ReminderItemData.TriggerMode getTriggerMode() {
        return reminderItemData.triggerMode;
    }

    public void setTriggerMode(ReminderItemData.TriggerMode triggerMode) {
        if(reminderItemData.triggerMode != triggerMode) {
            triggerModeDirty = true;
            reminderItemData.triggerMode = triggerMode;
        }
    }

    public int getTriggerCount() {
        return reminderItemData.triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        if(reminderItemData.triggerCount != triggerCount) {
            triggerCountDirty = true;
            reminderItemData.triggerCount = triggerCount;
        }
    }

    public ReminderItemData.TimePeriod getTriggerPeriod() {
        return reminderItemData.triggerPeriod;
    }

        public void setTriggerPeriod(ReminderItemData.TimePeriod triggerPeriod) {
        if(reminderItemData.triggerPeriod != triggerPeriod) {
            triggerPeriodDirty = true;
            reminderItemData.triggerPeriod = triggerPeriod;
        }
    }

    public ArrayList<TimeItem> getSpecificTimeList() {
        return reminderItemData.specificTimeList;
    }

    public void setSpecificTimeList(ArrayList<TimeItem> specificTimeList) {
        specificTimeListDirty = true;
        reminderItemData.specificTimeList = specificTimeList;
    }

    public ReminderItemData.RepeatTypeShort getIntervalPeriod() {
        return reminderItemData.intervalPeriod;
    }

    public void setIntervalPeriod(ReminderItemData.RepeatTypeShort intervalPeriod) {
        if(reminderItemData.intervalPeriod != intervalPeriod) {
            intervalPeriodDirty = true;
            reminderItemData.intervalPeriod = intervalPeriod;
        }
    }

    public int getIntervalCount() {
        return reminderItemData.intervalCount;
    }

    public void setIntervalCount(int intervalCount) {
        if(reminderItemData.intervalCount != intervalCount) {
            intervalCountDirty = true;
            reminderItemData.intervalCount = intervalCount;
        }
    }

    //Notifications
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

    //Snooze
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

    //State management
    public int getCurMessage() {
        return reminderItemData.curMessage;
    }

    public void setCurMessage(int curMessage) {
        if(reminderItemData.curMessage != curMessage) {
            curMessageDirty = true;
            reminderItemData.curMessage = curMessage;
        }
    }

    //Options specific to a reminder
    public boolean isShowAdvanced() {
        return reminderItemData.showAdvanced;
    }

    public void setShowAdvanced(boolean showAdvanced) {
        if(reminderItemData.showAdvanced != showAdvanced) {
            showAdvancedDirty = true;
            reminderItemData.showAdvanced = showAdvanced;
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

    public static boolean logReminderDismissed(@NonNull String uniqueName, @NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime) {
        Log.v("REMINDER ITEM", "Log Dismiss Reminder");
        ReminderLog reminderLog = loadFromFile(uniqueName);
        if (reminderLog == null) {
            Log.v("REMINDER ITEM", "No save, creating new one");
            reminderLog = new ReminderLog();
        }
        reminderLog.updateLog();
        reminderLog.logDismissed(dateTime, firstDateTime);
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

        generateEvenishSplit(diff, startOffset, 0.5f, reminderItemData.triggerCount);
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
        if(reminderItemData.weeksOfMonth[0]) {
            return true;
        }
        UtilsTime.WeekOfMonth week = UtilsTime.getWeekOfMonth(now.dateItem);
        Log.v("ReminderItem", "Week of month: "+week.getName()+" is last of month: "+UtilsTime.isLastWeekOfMonth(now.dateItem));
        if(reminderItemData.weeksOfMonth[1] && week == UtilsTime.WeekOfMonth.FIRST) {
            return true;
        }
        if(reminderItemData.weeksOfMonth[2] && week == UtilsTime.WeekOfMonth.SECOND) {
            return true;
        }
        if(reminderItemData.weeksOfMonth[3] && week == UtilsTime.WeekOfMonth.THIRD) {
            return true;
        }
        if(reminderItemData.weeksOfMonth[4] && week == UtilsTime.WeekOfMonth.FOURTH) {
            return true;
        }
        if(reminderItemData.weeksOfMonth[5] && week == UtilsTime.WeekOfMonth.FIFTH) {
            return true;
        }
        if(reminderItemData.weeksOfMonth[6] && UtilsTime.isLastWeekOfMonth(now.dateItem)) {
            return true;
        }
        return false;
    }

    public final void scheduleNextWake(DateTimeItem now) {
        if (!reminderItemData.enabled) {
            return;
        }
        if (!reminderItemData.daysOfWeek[UtilsTime.getCurrentDayOfWeek().getIndex()]) {
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
        if(reminderItemData.notifCounter > 25) {
            reminderItemData.notifCounter = 0;
        }

        int numAfter = reminderItemData.notifCounter % 5;
        int numBefore = (int) Math.round(Math.floor(reminderItemData.notifCounter / 5.0));

        for(int i=0; i<numBefore; i++) {
            content = " "+ content;
        }

        for(int i=0; i<numAfter; i++) {
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
        notif.addAction(new NotificationAction(iconCancel, AppBase.getContext().getString(R.string.dismiss), pDismiss));
        if(getSnooze() != ReminderItemData.SnoozeOptions.DISABLED) {
            notif.addAction(new NotificationAction(iconSnooze, AppBase.getContext().getString(R.string.snooze), pSnooze));
        }
        notif.addAction(new NotificationAction(iconCheck, AppBase.getContext().getString(R.string.complete), pAck));

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
                log += item.type.getName();
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
        reminderItemData.intervalCount = 30;
        reminderItemData.intervalPeriod = ReminderItemData.RepeatTypeShort.MINUTES;


        //Change how curMessage works
        if(reminderItemData.messageList == null) {
            reminderItemData.messageList = new ArrayList<>();
        }
        if(reminderItemData.title == null) {
            reminderItemData.title = "";
        }
        if(reminderItemData.startTime == null) {
            reminderItemData.startTime = new TimeItem(9, 0);
        }
        if(reminderItemData.endTime == null) {
            reminderItemData.endTime = new TimeItem(20, 0);
        }
        if(reminderItemData.specificTimeList == null) {
            reminderItemData.specificTimeList = new ArrayList<>();
        }
        if(reminderItemData.snooze == null) {
            reminderItemData.snooze = ReminderItemData.SnoozeOptions.DISABLED;
        }
        if(reminderItemData.autoSnooze == null) {
            reminderItemData.autoSnooze = ReminderItemData.SnoozeOptions.DISABLED;
        }
        if(reminderItemData.notificationVibratePattern == null) {
            reminderItemData.notificationVibratePattern = NotificationVibrate.DEFAULT;
        }
        if(reminderItemData.notificationPriority == null) {
            reminderItemData.notificationPriority = NotificationPriority.DEFAULT;
        }
        if(reminderItemData.alertTimes == null) {
            Log.v("ReminderItem", "Updating to newest alert times system");
            reminderItemData.alertTimes = new ArrayList<>();
            updateAlertTimes();
            rescheduleNextWake(UtilsTime.getDateTimeNow());
        }
        if(reminderItemData.numberPerDay != -1 || reminderItemData.daysToRun != null || reminderItemData.weeksToRun != null) {
            //Create and set all the new data
            //Timing
            reminderItemData.startDate = UtilsTime.getDateNow();
            reminderItemData.endEnable = false;
            reminderItemData.endDate = UtilsTime.getDateNow();
            //Criteria
            reminderItemData.filterType = ReminderItemData.FilterType.NORMAL;
            if(reminderItemData.daysToRun != null) {
                reminderItemData.daysOfWeek = reminderItemData.daysToRun.clone();
            }
            reminderItemData.daysOfMonth = Utils.getFilledBoolArray(true, 31);
            reminderItemData.weeksOfMonth = reminderItemData.weeksToRun.clone();
            reminderItemData.weeksOfMonth[0] = true;
            reminderItemData.monthsOfYear = Utils.getFilledBoolArray(true, 12);
            reminderItemData.repeatCount = 1;
            reminderItemData.repeatType = ReminderItemData.RepeatType.DAYS;
            reminderItemData.daysOfYear = new ArrayList<>();
            //Triggers
            reminderItemData.triggerMode = ReminderItemData.TriggerMode.RANDOM;
            reminderItemData.triggerCount = reminderItemData.numberPerDay;
            reminderItemData.triggerPeriod = ReminderItemData.TimePeriod.DAY;
            reminderItemData.intervalCount = 30;
            reminderItemData.intervalPeriod = ReminderItemData.RepeatTypeShort.MINUTES;
            //Clear the old data
            reminderItemData.numberPerDay = -1;
            reminderItemData.rangeTiming = true;
            reminderItemData.daysToRun = null;
            reminderItemData.weeksToRun = null;
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
