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

        DateTimeItem dateTimeNow = UtilsTime.getDateTimeNow();
        Log.v("Notification RECEIVER", "Woke At: " + dateTimeNow.timeItem.getHourInTimeFormatString() + ":" + dateTimeNow.timeItem.getMinuteString());

        if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED")) {
            Log.v("Notification RECEIVER", "notification click");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                logClicked(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, context, firstDateTime);
            }

        } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_CLICKED_SILENT")) {
            Log.v("Notification RECEIVER", "notification click silent");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                logAck(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, firstDateTime);
            }

        } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_DELETED")) {
            Log.v("Notification RECEIVER", "notification delete");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                logDeleted(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, firstDateTime);
            }

        } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_DISMISS")) {
            Log.v("Notification RECEIVER", "notification dismiss");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                logDismiss(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, firstDateTime);
            }

        } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_ACKNOWLEDGE")) {
            Log.v("Notification RECEIVER", "notification acknowledge");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                logAck(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, firstDateTime);
            }

        } else if (intent.getAction() != null && intent.getAction().equals("jamesmorrisstudios.com.randremind.NOTIFICATION_SNOOZE")) {
            Log.v("Notification RECEIVER", "notification snooze");
            if (intent.getExtras() != null && intent.hasExtra("NAME") && intent.hasExtra("NOTIFICATION_ID") && intent.hasExtra("DATETIME")) {
                DateTimeItem dateTimePosted = DateTimeItem.decodeFromString(intent.getStringExtra("DATETIME"));
                DateTimeItem firstDateTime = null;
                if(intent.hasExtra("FIRSTDATETIME")) {
                    firstDateTime = DateTimeItem.decodeFromString(intent.getStringExtra("FIRSTDATETIME"));
                }
                int snoozeLength = intent.getIntExtra("SNOOZE_LENGTH", 0);
                logSnooze(intent.getStringExtra("NAME"), intent.getIntExtra("NOTIFICATION_ID", 0), intent.hasExtra("PREVIEW"), dateTimePosted, dateTimeNow, snoozeLength, firstDateTime);
            }
        }

        Log.v("Notification RECEIVER", "Completed Wake: " + dateTimeNow.timeItem.getHourInTimeFormatString() + ":" + dateTimeNow.timeItem.getMinuteString());

        //Release the wakelock
        wl.release();
    }

    private void cancelSnooze(@NonNull String uniqueName) {
        Scheduler.getInstance().cancelWakeAutoSnooze(uniqueName);
        Scheduler.getInstance().cancelWakeSnooze(uniqueName);
    }

    private void logClicked(@NonNull String name, int notificationId, boolean preview, @NonNull DateTimeItem dateTime, @NonNull Context context, @Nullable DateTimeItem firstDateTime) {
        Log.v("Notification RECEIVER", "log clicked: Preview: " + preview);
        Log.v("Notification RECEIVER", "ID Clicked: " + notificationId);
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        logAck(name, notificationId, preview, dateTime, firstDateTime);
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

    private void logSnooze(@NonNull String name, int notificationId, boolean preview, @NonNull DateTimeItem dateTimePosted, @NonNull DateTimeItem dateTimeNow, int snoozeLength, @Nullable DateTimeItem firstDateTime) {
        Log.v("Notification RECEIVER", "ID snooze: " + notificationId);
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        if(snoozeLength <= 0) {
            return;
        }
        DateTimeItem next = UtilsTime.getDateTimePlusMinutes(dateTimeNow, snoozeLength);
        if(firstDateTime == null) {
            firstDateTime = dateTimePosted;
        }
        Scheduler.getInstance().scheduleWakeSnooze(next, name, firstDateTime);
        ReminderItem.logReminderClicked(name, dateTimeNow, firstDateTime, true);
    }

    private void logDeleted(@NonNull String name, int notificationId, boolean preview, @NonNull DateTimeItem dateTime, @Nullable DateTimeItem firstDateTime) {
        Log.v("Notification RECEIVER", "ID Deleted: " + notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        if(firstDateTime == null) {
            firstDateTime = dateTime;
        }
        ReminderItem.logReminderDismissed(name, dateTime, firstDateTime);
    }

    private void logDismiss(@NonNull String name, int notificationId, boolean preview, @NonNull DateTimeItem dateTime, @Nullable DateTimeItem firstDateTime) {
        Log.v("Notification RECEIVER", "ID Dismissed: " + notificationId);
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        if(firstDateTime == null) {
            firstDateTime = dateTime;
        }
        ReminderItem.logReminderDismissed(name, dateTime, firstDateTime);
    }

    private void logAck(@NonNull String name, int notificationId, boolean preview, @NonNull DateTimeItem dateTime, @Nullable DateTimeItem firstDateTime) {
        Log.v("Notification RECEIVER", "ID Ack: " + notificationId);
        Notifier.dismissNotification(notificationId);
        if(preview) {
            return;
        }
        cancelSnooze(name);
        if(firstDateTime == null) {
            firstDateTime = dateTime;
        }
        ReminderItem.logReminderClicked(name, dateTime, firstDateTime, false);
    }

}
