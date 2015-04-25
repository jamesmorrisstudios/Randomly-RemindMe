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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.application.App;

/**
 * Notification handler class.
 * This generates and displays notifications given a list of reminder items
 *
 * Created by James on 4/24/2015.
 */
public final class Notifier {
    private static Notifier instance = null;

    /**
     * Required private constructor to maintain singleton
     */
    private Notifier() {}

    /**
     * @return The singleton instance of the Notifier
     */
    public static Notifier getInstance() {
        if(instance == null) {
            instance = new Notifier();
        }
        return instance;
    }

    /**
     * Builds and displays a notification with the given parameters
     * @param text Text to display
     * @param notificationTone Notification tone. Null to have none
     * @param vibrate True to enable vibrate
     * @param id Id to associate notification with. These should be unique to the reminderItem
     */
    private void buildNotification(@NonNull String text, @Nullable Uri notificationTone, boolean vibrate, int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        TimeItem timeNow = new TimeItem(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        Log.v("Notification shown", text+" "+timeNow.getHourInTimeFormatString()+":"+timeNow.getMinuteString());

        int defaults = Notification.DEFAULT_LIGHTS;
        if(vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        NotificationCompat.Builder mBuilder;
        if(notificationTone == null) {
            mBuilder = new NotificationCompat.Builder(App.getContext())
                            .setDefaults(defaults)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(App.getContext().getString(R.string.app_name))
                            .setContentText(text);
        } else {
            mBuilder = new NotificationCompat.Builder(App.getContext())
                            .setSound(notificationTone)
                            .setDefaults(defaults)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(App.getContext().getString(R.string.app_name))
                            .setContentText(text);
        }
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
    }

    public final void notifyInstantly(ReminderItem item) {
        String title = item.title;
        if(title == null || title.isEmpty()) {
            title = App.getContext().getString(R.string.default_title);
        }
        buildNotification(title, item.getNotificationTone(), item.notificationVibrate, item.notificationId);
    }

    /**
     * Gets a list of reminder items that are due to have a notification shown and builds and shows them.
     * If none are ready to be shown it does nothing
     */
    public final void postNextNotification() {
        ReminderList.getInstance().loadDataSync();
        ArrayList<ReminderItem> items = ReminderList.getInstance().getCurrentWakes();
        for(ReminderItem item : items) {
            String title = item.title;
            if(title == null || title.isEmpty()) {
                title = App.getContext().getString(R.string.default_title);
            }
            buildNotification(title, item.getNotificationTone(), item.notificationVibrate, item.notificationId);
        }
    }
}
