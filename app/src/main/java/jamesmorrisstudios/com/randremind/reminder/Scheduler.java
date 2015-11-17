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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import java.util.Calendar;

import jamesmorrisstudios.com.randremind.receiver.AlarmReceiver;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 * Reminder wake scheduler class.
 * This class is responsible for setting all new wake alarms with the android system.
 * It can also cancel unused alarms if needed
 * <p/>
 * Created by James on 4/23/2015.
 */
public final class Scheduler {
    private static Scheduler instance = null;

    /**
     * Required private constructor to maintain singleton
     */
    private Scheduler() {}

    /**
     * @return The singleton instance of the Scheduler
     */
    @NonNull
    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    private Intent getIntent(@NonNull RemindUtils.WakeAction action, @NonNull DateTimeItem dateTime, @NonNull DateTimeItem firstDateTime, @NonNull String uniqueName) {
        Intent i = new Intent(AppBase.getContext(), AlarmReceiver.class);
        i.setType(uniqueName);
        i.setAction(action.getKey());
        i.putExtra("DATETIME", DateTimeItem.encodeToString(dateTime));
        i.putExtra("FIRSTDATETIME", DateTimeItem.encodeToString(firstDateTime));
        return i;
    }

    private Intent getIntent(@NonNull RemindUtils.WakeAction action, @NonNull String uniqueName) {
        Intent i = new Intent(AppBase.getContext(), AlarmReceiver.class);
        i.setType(uniqueName);
        i.setAction(action.getKey());
        return i;
    }

    private Intent getIntent(@NonNull RemindUtils.WakeAction action) {
        Intent i = new Intent(AppBase.getContext(), AlarmReceiver.class);
        i.setAction(action.getKey());
        return i;
    }

    private long timeMillis(@NonNull DateTimeItem dateTime) {
        return UtilsTime.getCalendar(dateTime).getTimeInMillis();
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) AppBase.getContext().getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Schedules the next app wake time
     *
     * @param dateTime Time to schedule for
     */
    public final void scheduleWake(@NonNull DateTimeItem dateTime, @NonNull String uniqueName) {
        Log.v("SCHEDULER", "Alarm Set For: " + uniqueName + " at " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 0, getIntent(RemindUtils.WakeAction.REMINDER, dateTime, dateTime, uniqueName), PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, timeMillis(dateTime), pi);
    }

    /**
     * Cancels the next scheduled wake.
     * Does not cancel the midnight update alarm
     */
    public final void cancelWake(@NonNull String uniqueName) {
        Log.v("SCHEDULER", "Alarm deleted For: " + uniqueName);
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 0, getIntent(RemindUtils.WakeAction.REMINDER, uniqueName), 0);
        getAlarmManager().cancel(pi);
    }

    /**
     * Schedules the next app wake time
     *
     * @param dateTime Time to schedule for
     */
    public final void scheduleWakeAutoSnooze(@NonNull DateTimeItem dateTime, @NonNull String uniqueName, @NonNull DateTimeItem firstDateTime) {
        Log.v("SCHEDULER", "Auto Snooze Set For: " + uniqueName + " at " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 2, getIntent(RemindUtils.WakeAction.REMINDER_AUTO_SNOOZE, dateTime, firstDateTime, uniqueName), PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, timeMillis(dateTime), pi);
    }

    /**
     * Cancels the next scheduled wake.
     * Does not cancel the midnight update alarm
     */
    public final void cancelWakeAutoSnooze(@NonNull String uniqueName) {
        Log.v("SCHEDULER", "Auto Snooze deleted For: " + uniqueName);
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 2, getIntent(RemindUtils.WakeAction.REMINDER_AUTO_SNOOZE, uniqueName), 0);
        getAlarmManager().cancel(pi);
    }

    /**
     * Schedules the next app wake time
     *
     * @param dateTime Time to schedule for
     */
    public final void scheduleWakeSnooze(@NonNull DateTimeItem dateTime, @NonNull String uniqueName, @NonNull DateTimeItem firstDateTime) {
        Log.v("SCHEDULER", "Snooze Set For: " + uniqueName + " at " + dateTime.timeItem.getHourInTimeFormatString() + ":" + dateTime.timeItem.getMinuteString());
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 3, getIntent(RemindUtils.WakeAction.REMINDER_SNOOZE, dateTime, firstDateTime, uniqueName), PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, timeMillis(dateTime), pi);
    }

    /**
     * Cancels the next scheduled wake.
     * Does not cancel the midnight update alarm
     */
    public final void cancelWakeSnooze(@NonNull String uniqueName) {
        Log.v("SCHEDULER", "Snooze deleted For: " + uniqueName);
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 3, getIntent(RemindUtils.WakeAction.REMINDER_SNOOZE, uniqueName), 0);
        getAlarmManager().cancel(pi);
    }

    /**
     * Schedules the repeating midnight timer
     */
    public final void scheduleRepeatingMidnight() {
        Log.v("SCHEDULER", "Repeating midnight alarm set");
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 1, getIntent(RemindUtils.WakeAction.MIDNIGHT), 0);
        Calendar calendar = UtilsTime.getCalendar();
        calendar.add(Calendar.DATE, 1); //Increment to tomorrow
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pi);
    }

    /**
     * Cancels the midnight update alarm
     */
    public final void cancelMidnightAlarm() {
        PendingIntent pi = PendingIntent.getBroadcast(AppBase.getContext(), 1, getIntent(RemindUtils.WakeAction.MIDNIGHT), 0);
        getAlarmManager().cancel(pi);
    }

}
