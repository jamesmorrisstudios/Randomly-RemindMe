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

import java.util.Calendar;

import jamesmorrisstudios.com.randremind.application.App;
import jamesmorrisstudios.com.randremind.receiver.AlarmReceiver;

/**
 * Reminder wake scheduler class.
 * This class is responsible for setting all new wake alarms with the android system.
 * It can also cancel unused alarms if needed
 *
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
        if(instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    /**
     * Cancels the next scheduled wake.
     * Does not cancel the midnight update alarm
     */
    public final void cancelNextWake() {
        AlarmManager am=(AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(App.getContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(App.getContext(), 0, i, 0);
        am.cancel(pi);
    }

    /**
     * Cancels the midnight update alarm
     */
    public final void cancelMidnightAlarm() {
        AlarmManager am=(AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(App.getContext(), AlarmReceiver.class);
        i.putExtra("REPEAT", true);
        PendingIntent pi = PendingIntent.getBroadcast(App.getContext(), 1, i, 0);
        am.cancel(pi);
    }

    /**
     * Schedule the next wake we have in this days cycle.
     */
    public final void scheduleNextWake() {
        TimeItem time = ReminderList.getInstance().getNextWake();
        if(time != null) {
            scheduleWake(time);
        }
    }

    /**
     * Schedules the next app wake time
     * @param time Time to schedule for
     */
    private void scheduleWake(@NonNull TimeItem time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time.hour);
        calendar.set(Calendar.MINUTE, time.minute);
        AlarmManager am=(AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(App.getContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(App.getContext(), 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    /**
     * Schedules the repeating midnight timer
     */
    public final void scheduleRepeatingMidnight() {
        AlarmManager am=(AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(App.getContext(), AlarmReceiver.class);
        i.putExtra("REPEAT", true);
        PendingIntent pi = PendingIntent.getBroadcast(App.getContext(), 1, i, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pi); //Every 24 hours
    }

}