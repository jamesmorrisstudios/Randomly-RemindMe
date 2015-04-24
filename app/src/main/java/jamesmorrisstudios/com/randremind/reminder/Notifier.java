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
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.application.App;

/**
 * Created by James on 4/24/2015.
 */
public class Notifier {
    //Scheduler singleton instance
    private static Notifier instance = null;

    /**
     * Required private constructor to maintain singleton
     */
    private Notifier() {}

    /**
     * @return The singleton instance of the Scheduler
     */
    public static Notifier getInstance() {
        if(instance == null) {
            instance = new Notifier();
        }
        return instance;
    }

    private void buildNotification(String text, Uri notificationTone, boolean vibrate, int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(App.getContext())
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Randomly RemindMe")
                        .setContentText(text);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
    }

    public final void postNextNotification() {
        ReminderList.getInstance().loadDataSync();
        ArrayList<ReminderItem> items = ReminderList.getInstance().getCurrentWakes();
        int id = 0;
        for(ReminderItem item : items) {
            buildNotification(item.title, item.notificationTone, item.notificationVibrate, id);
            id++;
        }
    }
}
