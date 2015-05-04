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

import com.jamesmorrisstudios.utilitieslibrary.notification.Notifier;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.activities.MainActivity;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;

/**
 * Alarm receiver class.
 * This class manages all alarm wakes for this app.
 */
public final class NotificationReceiver extends BroadcastReceiver {

    /**
     * Empty constructor
     */
    public NotificationReceiver() {}

    /**
     * Receive an alarm from one of our wake settings
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        //Get our wakelock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Randomly RemindMe Wake For Notification");
        wl.acquire();

        TimeItem timeNow = UtilsTime.getTimeNow();
        Log.v("Notification RECEIVER", "Woke At: " + timeNow.getHourInTimeFormatString() + ":" + timeNow.getMinuteString());

        if(intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED")) {
            Log.v("Notification RECEIVER", "notification click");
            if(intent.getExtras() != null && intent.getExtras().containsKey("NAME") && intent.getExtras().containsKey("NOTIFICATION_ID")) {
                logClicked(intent.getExtras().getString("NAME"), intent.getExtras().getInt("NOTIFICATION_ID"), intent.getExtras().containsKey("PREVIEW"), context);
            }
        } else if(intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_DELETED")) {
            Log.v("Notification RECEIVER", "notification delete");
            if(intent.getExtras() != null && intent.getExtras().containsKey("NAME") && intent.getExtras().containsKey("NOTIFICATION_ID")) {
                logDeleted(intent.getExtras().getString("NAME"), intent.getExtras().getInt("NOTIFICATION_ID"), intent.getExtras().containsKey("PREVIEW"));
            }
        } else if(intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_DISMISS")) {
            Log.v("Notification RECEIVER", "notification dismiss");
            if(intent.getExtras() != null && intent.getExtras().containsKey("NAME") && intent.getExtras().containsKey("NOTIFICATION_ID")) {
                logDismiss(intent.getExtras().getString("NAME"), intent.getExtras().getInt("NOTIFICATION_ID"), intent.getExtras().containsKey("PREVIEW"));
            }
        } else if(intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_ACKNOWLEDGE")) {
            Log.v("Notification RECEIVER", "notification acknowledge");
            if(intent.getExtras() != null && intent.getExtras().containsKey("NAME") && intent.getExtras().containsKey("NOTIFICATION_ID")) {
                logAck(intent.getExtras().getString("NAME"), intent.getExtras().getInt("NOTIFICATION_ID"), intent.getExtras().containsKey("PREVIEW"));
            }
        }

        Log.v("Notification RECEIVER", "Completed Wake: " + timeNow.getHourInTimeFormatString() + ":" + timeNow.getMinuteString());

        //Release the wakelock
        wl.release();
    }

    private void logClicked(String name, int notificationId, boolean preview, Context context) {
        Log.v("Notification RECEIVER", "log clicked: Preview: "+preview);
        Log.v("ALARM RECEIVER", "ID Clicked: "+notificationId);
        logAck(name, notificationId, preview);
        if(!preview) {
            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            intent.putExtra("NAME", name);
            intent.putExtra("REMINDER", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void logDeleted(String name, int notificationId, boolean preview) {
        Log.v("Notification RECEIVER", "ID Deleted: "+notificationId);
        //Do nothing?...
    }

    private void logDismiss(String name, int notificationId, boolean preview) {
        Log.v("Notification RECEIVER", "ID Dismissed: "+notificationId);
        Notifier.dismissNotification(notificationId);
        //if(!preview) {
            //Do nothing?...
        //}
    }

    private void logAck(String name, int notificationId, boolean preview) {
        Log.v("Notification RECEIVER", "ID Ack: "+notificationId);
        Notifier.dismissNotification(notificationId);
        if(!preview) {
            ReminderItem.logReminderClicked(name);
        }
    }

}