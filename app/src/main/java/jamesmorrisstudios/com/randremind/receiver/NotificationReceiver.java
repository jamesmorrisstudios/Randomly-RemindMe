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
import android.support.annotation.Nullable;
import android.util.Log;

import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.notification.Notifier;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.activities.MainActivity;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 * Alarm receiver class.
 * This class manages all alarm wakes for this app.
 */
public final class NotificationReceiver extends BroadcastReceiver {

    /**
     * Empty constructor
     */
    public NotificationReceiver() {
    }

    /**
     * Receive an alarm from one of our wake settings
     *
     * @param context Context
     * @param intent  Intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        //Get our wakelock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Randomly RemindMe Wake For Notification");
        wl.acquire();
        //Get the wake time
        DateTimeItem dateTimeNow = UtilsTime.getDateTimeNow();
        switch(RemindUtils.getNotificationUserAction(intent)) {
            case CLICKED:
                Log.v("Notification RECEIVER", "notification click");
                if(isIntentValid(intent)) {
                    clickedAction(context, getUniqueName(intent), getNotificationId(intent), isPreview(intent), getDateTimePosted(intent), getFirstDateTime(intent), dateTimeNow, getSnoozeLength(intent));
                }
            case DELETED:
                Log.v("Notification RECEIVER", "notification delete");
                if(isIntentValid(intent)) {
                    deletedAction(context, getUniqueName(intent), getNotificationId(intent), isPreview(intent), getDateTimePosted(intent), getFirstDateTime(intent), dateTimeNow, getSnoozeLength(intent));
                }
            case DISMISSED:
                Log.v("Notification RECEIVER", "notification dismiss");
                if(isIntentValid(intent)) {
                    dismissAction(context, getUniqueName(intent), getNotificationId(intent), isPreview(intent), getDateTimePosted(intent), getFirstDateTime(intent), dateTimeNow, getSnoozeLength(intent));
                }
            case SNOOZED:
                Log.v("Notification RECEIVER", "notification snooze");
                if(isIntentValid(intent)) {
                    snoozeAction(context, getUniqueName(intent), getNotificationId(intent), isPreview(intent), getDateTimePosted(intent), getFirstDateTime(intent), dateTimeNow, getSnoozeLength(intent));
                }
            case ACKNOWLEDGED:
                Log.v("Notification RECEIVER", "notification acknowledge");
                if(isIntentValid(intent)) {
                    ackAction(context, getUniqueName(intent), getNotificationId(intent), isPreview(intent), getDateTimePosted(intent), getFirstDateTime(intent), dateTimeNow, getSnoozeLength(intent));
                }
            case INVALID:
                //Do Nothing
        }
        //Release the wakelock
        wl.release();
    }

    private void cancelSnooze(@NonNull String uniqueName) {
        Scheduler.getInstance().cancelWakeAutoSnooze(uniqueName);
        Scheduler.getInstance().cancelWakeSnooze(uniqueName);
    }

    private boolean isIntentValid(@NonNull Intent intent) {
        return intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME") && intent.hasExtra("FIRSTDATETIME") && intent.hasExtra("SNOOZE_LENGTH");
    }

    private String getUniqueName(@NonNull Intent intent) {
        return intent.getStringExtra("NAME");
    }

    private int getNotificationId(@NonNull Intent intent) {
        return intent.getIntExtra("NOTIFICATION_ID", 0);
    }

    @NonNull
    private DateTimeItem getDateTimePosted(@NonNull Intent intent) {
        return DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
    }

    @NonNull
    private DateTimeItem getFirstDateTime(@NonNull Intent intent) {
        return DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
    }

    private int getSnoozeLength(@NonNull Intent intent) {
        return intent.getIntExtra("SNOOZE_LENGTH", 0);
    }

    private boolean isPreview(@NonNull Intent intent) {
        return intent.hasExtra("PREVIEW");
    }

    private void clickedAction(@NonNull Context context, @NonNull String name, int notificationId, boolean preview,
                               @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem firstDateTime, @NonNull DateTimeItem dateTimeNow, int snoozeLength) {
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        switch(RemindUtils.getClickAction()) {
            case COMPLETE:
                ackAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case DISMISS:
                dismissAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case SNOOZE:
                snoozeAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case NOTHING:
                //Do nothing
        }
        if(RemindUtils.getClickOpenApp()) {
            openApp(context, name);
        }
    }

    //Swipe away
    private void deletedAction(@NonNull Context context, @NonNull String name, int notificationId, boolean preview,
                               @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem firstDateTime, @NonNull DateTimeItem dateTimeNow, int snoozeLength) {
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        switch(RemindUtils.getSwipeAction()) {
            case COMPLETE:
                ackAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case DISMISS:
                dismissAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case SNOOZE:
                snoozeAction(context, name, notificationId, preview, dateTimePosted, firstDateTime, dateTimeNow, snoozeLength);
            case NOTHING:
                //Do nothing
        }
        if(RemindUtils.getSwipeOpensApp()) {
            openApp(context, name);
        }
    }

    private void dismissAction(@NonNull Context context, @NonNull String name, int notificationId, boolean preview,
                               @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem firstDateTime, @NonNull DateTimeItem dateTimeNow, int snoozeLength) {
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        ReminderItem.logReminderDismissed(name, dateTimePosted, firstDateTime);
    }

    private void snoozeAction(@NonNull Context context, @NonNull String name, int notificationId, boolean preview,
                              @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem firstDateTime, @NonNull DateTimeItem dateTimeNow, int snoozeLength) {
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        if(snoozeLength <= 0) {
            return;
        }
        DateTimeItem next = UtilsTime.getDateTimePlusMinutes(dateTimeNow, snoozeLength);
        Scheduler.getInstance().scheduleWakeSnooze(next, name, firstDateTime);
        ReminderItem.logReminderClicked(name, dateTimeNow, firstDateTime, true);
    }

    private void ackAction(@NonNull Context context, @NonNull String name, int notificationId, boolean preview,
                           @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem firstDateTime, @NonNull DateTimeItem dateTimeNow, int snoozeLength) {
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        ReminderItem.logReminderClicked(name, dateTimePosted, firstDateTime, false);
    }

    private void openApp(@NonNull Context context, @NonNull String name) {
        boolean status = true;
        if (!ReminderList.getInstance().hasReminders()) {
            Log.v("Notification RECEIVER", "No Data loaded, loading...");
            status = ReminderList.getInstance().loadDataSync();
        }
        if (status) {
            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            intent.putExtra("NAME", name);
            intent.putExtra("REMINDER", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception ex) {
                Utils.toastShort(context.getString(R.string.app_open_fail));
            }
        }
    }
}
