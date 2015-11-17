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

import com.jamesmorrisstudios.appbaselibrary.notification.Notifier;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemData;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

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

        if (status) {
            DateTimeItem lastWake = ReminderList.getInstance().getLastWake();
            Log.v("ALARM RECEIVER", "Woke At: " + now.timeItem.getHourInTimeFormatString() + ":" + now.timeItem.getMinuteString());

            Log.v("ALARM RECEIVER", "Intent: "+intent.hasExtra("DATETIME")+" : "+intent.hasExtra("FIRSTDATETIME"));

            switch (RemindUtils.getWakeAction(intent)) {
                case BOOT_COMPLETED:
                    Log.v("ALARM RECEIVER", "Device just woke");
                    //Set up our midnight recalculate wake
                    Scheduler.getInstance().scheduleRepeatingMidnight();
                    //Recalculate wakes if we were off for over a day
                    if (!now.dateItem.equals(lastWake.dateItem)) {
                        ReminderList.getInstance().recalculateWakes();
                    }
                    //Schedule the next wake event
                    ReminderList.getInstance().scheduleAllWakes(now);
                    break;
                case MIDNIGHT:
                    Log.v("ALARM RECEIVER", "Midnight update");
                    //Recalculate all wakes for a new day
                    ReminderList.getInstance().recalculateWakes();
                    //Schedule the next wake event
                    ReminderList.getInstance().scheduleAllWakes(now);
                    break;
                case REMINDER:
                    Log.v("ALARM RECEIVER", "Reminder!");
                    Scheduler.getInstance().cancelWakeAutoSnooze(intent.getType());
                    Scheduler.getInstance().cancelWakeSnooze(intent.getType());
                    scheduleAutoSnooze(intent.getType(), now, getFirstDateTime(intent));
                    postNotifications(intent.getType(), now, false, getFirstDateTime(intent));
                    break;
                case REMINDER_SNOOZE:
                    Log.v("ALARM RECEIVER", "Reminder Snooze!");
                    Scheduler.getInstance().cancelWakeSnooze(intent.getType());
                    Scheduler.getInstance().cancelWakeAutoSnooze(intent.getType());
                    scheduleAutoSnooze(intent.getType(), now, getFirstDateTime(intent));
                    postNotifications(intent.getType(), now, true, getFirstDateTime(intent));
                    break;
                case REMINDER_AUTO_SNOOZE:
                    Log.v("ALARM RECEIVER", "Reminder Auto Snooze!");
                    Scheduler.getInstance().cancelWakeAutoSnooze(intent.getType());
                    scheduleAutoSnooze(intent.getType(), now, getFirstDateTime(intent));
                    postNotifications(intent.getType(), now, true, getFirstDateTime(intent));
                    break;
            }
            ReminderList.getInstance().logLastWake(now);
        } else {
            Log.v("ALARM RECEIVER", "Failed to load");
        }
        //Save changes to the reminders
        ReminderList.getInstance().saveDataSync();

        Log.v("ALARM RECEIVER", "Completed Wake");

        //Release the wakelock
        wl.release();
    }

    private DateTimeItem getFirstDateTime(Intent intent) {
        return DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
    }

    private void scheduleAutoSnooze(@NonNull String uniqueName, @NonNull DateTimeItem now, @NonNull DateTimeItem firstDateTime) {
        ReminderItem item = ReminderList.getInstance().getReminderCopy(uniqueName);
        if (item == null) {
            return;
        }
        if(item.getAutoSnooze() == ReminderItemData.SnoozeOptions.DISABLED) {
            return;
        }
        DateTimeItem next = UtilsTime.getDateTimePlusMinutes(now, item.getAutoSnooze().minutes);
        Scheduler.getInstance().scheduleWakeAutoSnooze(next, uniqueName, firstDateTime);
    }

    private void postNotifications(@NonNull String uniqueName, @NonNull DateTimeItem now, boolean snoozed, @NonNull DateTimeItem firstDateTime) {
        ReminderItem item = ReminderList.getInstance().getReminderCopy(uniqueName);
        if (item == null) {
            return;
        }
        ReminderItem.logReminderShown(item.getUniqueName(), now, firstDateTime, snoozed);
        Notifier.buildNotification(item.getNotification(false, now, snoozed, firstDateTime));
        Log.v("ALARM RECEIVER", "Post Notification: " + item.getNotificationId());
        item.rescheduleNextWake(now);
        ReminderList.getInstance().saveReminderItem(item);
    }

}
