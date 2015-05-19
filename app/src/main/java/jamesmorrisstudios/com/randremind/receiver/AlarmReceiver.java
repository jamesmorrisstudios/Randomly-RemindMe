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

package jamesmorrisstudios.com.randremind.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.notification.Notifier;
import com.jamesmorrisstudios.utilitieslibrary.preferences.Preferences;
import com.jamesmorrisstudios.utilitieslibrary.time.DateTimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;

/**
 * Alarm receiver class.
 * This class manages all alarm wakes for this app.
 */
public final class AlarmReceiver extends BroadcastReceiver {

    /**
     * Empty constructor
     */
    public AlarmReceiver() {
    }

    /**
     * Receive an alarm from one of our wake settings
     *
     * @param context Context
     * @param intent  Intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        //Get the time right away as we use this and it needs to be precise
        //We have some wiggle room before we need the wakelock
        DateTimeItem now = UtilsTime.getDateTimeNow();

        //Get our wakelock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Randomly RemindMe Wake For Reminder");
        wl.acquire();

        boolean status = true;

        //Only load the reminders if they aren't already loaded
        //This way if the user has the app open their changes aren't overwritten
        if (!ReminderList.getInstance().hasReminders()) {
            Log.v("ALARM RECEIVER", "No Data loaded, loading...");
            status = ReminderList.getInstance().loadDataSync();
        }

        if(status) {
            DateTimeItem lastWake = getLastWake();
            TimeItem prevTime;
            //If the last wake was a different day
            if (now.dateItem.equals(lastWake.dateItem)) {
                prevTime = lastWake.timeItem;
            } else {
                prevTime = new TimeItem(0, 0);
            }

            Log.v("ALARM RECEIVER", "Woke At: " + now.timeItem.getHourInTimeFormatString() + ":" + now.timeItem.getMinuteString());

            if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Log.v("ALARM RECEIVER", "Device just woke");
                //Set up our midnight recalculate wake
                Scheduler.getInstance().scheduleRepeatingMidnight();
                //Recalculate wakes if we were off for over a day
                if (!now.dateItem.equals(lastWake.dateItem)) {
                    ReminderList.getInstance().recalculateWakes();
                }
                //Schedule the next wake event
                ReminderList.getInstance().scheduleAllWakes(now);
            } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.WAKEMIDNIGHT")) {
                Log.v("ALARM RECEIVER", "Midnight update");
                //Recalculate all wakes for a new day
                ReminderList.getInstance().recalculateWakes();
                //Schedule the next wake event
                ReminderList.getInstance().scheduleAllWakes(now);
            } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.WAKEREMINDER") && intent.getType() != null && !intent.getType().isEmpty()) {
                Log.v("ALARM RECEIVER", "Reminder!");
                //Post a notification if we have one
                postNotifications(intent.getType(), now);
                //Schedule the next wake event
                //ReminderList.getInstance().scheduleAllWakes(now.timeItem);
            }

            logLastWake(now);

            //Get now again just to see how long this method took. It is not used for anything else
            now = UtilsTime.getDateTimeNow();
            Log.v("ALARM RECEIVER", "Completed Wake: " + now.timeItem.getHourInTimeFormatString() + ":" + now.timeItem.getMinuteString());
        } else {
            Log.v("ALARM RECEIVER", "Failed to load");
        }
        //Release the wakelock
        wl.release();
    }

    private void postNotifications(@NonNull String uniqueName, @NonNull DateTimeItem now) {
        ReminderItem item = ReminderList.getInstance().getReminder(uniqueName);
        if (item == null) {
            return;
        }
        ReminderItem.logReminderShown(item.uniqueName, now);
        Notifier.buildNotification(item.getNotification(false, now));
        Log.v("ALARM RECEIVER", "Post Notification: " + item.getNotificationId());
        item.rescheduleNextWake(now);
    }

    private void logLastWake(@NonNull DateTimeItem time) {
        String lastWake = DateTimeItem.encodeToString(time);
        Preferences.putString(AppUtil.getContext().getString(R.string.pref_alarm_receiver), "LAST_WAKE", lastWake);
    }

    @NonNull
    private DateTimeItem getLastWake() {
        String lastWake = Preferences.getString(AppUtil.getContext().getString(R.string.pref_alarm_receiver), "LAST_WAKE");
        return DateTimeItem.decodeFromString(lastWake);
    }

}
