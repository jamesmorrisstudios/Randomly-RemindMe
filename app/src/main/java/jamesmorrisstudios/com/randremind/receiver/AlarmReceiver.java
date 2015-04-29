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
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import java.util.ArrayList;

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
    public AlarmReceiver() {}

    /**
     * Receive an alarm from one of our wake settings
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        //Get our wakelock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Randomly RemindMe Wake For Reminder");
        wl.acquire();

        ReminderList.getInstance().loadDataSync();

        TimeItem timeNow = UtilsTime.getTimeNow();
        Log.v("ALARM RECEIVER", "Woke At: " + timeNow.getHourInTimeFormatString() + ":" + timeNow.getMinuteString());

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.v("ALARM RECEIVER", "Device just woke");
            //Set up our midnight recalculate wake
            Scheduler.getInstance().scheduleRepeatingMidnight();

            //Make sure our wakes are recalculated and cleaned up as we don't know how long we were off for
            ReminderList.getInstance().recalculateWakes();
            ReminderList.getInstance().trimWakesToCurrent();
            //Schedule the next wake event
            Scheduler.getInstance().scheduleNextWake();
        } else if(intent.getExtras().containsKey("REPEAT")){
            Log.v("ALARM RECEIVER", "Midnight update");
            //Recalculate all wakes for a new day
            ReminderList.getInstance().recalculateWakes();
            //Post a notification if we have one (likely don't)
            postNextNotification();
            //Schedule the next wake event
            Scheduler.getInstance().scheduleNextWake();
        } else if(intent.getExtras().containsKey("REMINDER_WAKE")){
            Log.v("ALARM RECEIVER", "Reminder!");
            //Post a notification if we have one
            postNextNotification();
            //Schedule the next wake event
            Scheduler.getInstance().scheduleNextWake();
        }

        Log.v("ALARM RECEIVER", "Completed Wake: " + timeNow.getHourInTimeFormatString() + ":" + timeNow.getMinuteString());

        ReminderList.getInstance().saveDataSync();

        //Release the wakelock
        wl.release();
    }

    private void postNextNotification() {
        ArrayList<ReminderItem> items = ReminderList.getInstance().getCurrentWakes();
        for(ReminderItem item : items) {
            String title = item.title;
            if(title == null || title.isEmpty()) {
                title = AppUtil.getContext().getString(R.string.default_title);
            }
            String content = item.content;
            if(content == null || content.isEmpty()) {
                content = AppUtil.getContext().getString(R.string.default_content);
            }
            Notifier.buildNotification(title, content, item.getNotificationTone(), R.drawable.notification_icon, item.notificationVibrate,
                    item.notificationHighPriority, item.notificationLED, item.notificationLEDColor, item.notificationId);
        }
    }

}
