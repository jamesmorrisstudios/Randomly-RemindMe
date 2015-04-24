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

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Individual reminder item that contains all needed items to be a reminder
 *
 * Created by James on 4/20/2015.
 */
public final class ReminderItem {
    @SerializedName("uniqueName")
    public final String uniqueName;
    //Title
    @SerializedName("title")
    public String title;
    @SerializedName("enabled")
    public boolean enabled;
    //Timing
    @SerializedName("startTime")
    public TimeItem startTime;
    @SerializedName("endTime")
    public TimeItem endTime;
    @SerializedName("numberPerDay")
    public int numberPerDay;
    @SerializedName("distribution")
    public Distribution distribution;
    //Repeat
    @SerializedName("repeat")
    public boolean repeat;
    @SerializedName("daysToRun")
    public boolean[] daysToRun; //Sunday -> Saturday
    //Notifications
    @SerializedName("notification")
    public boolean notification;
    @SerializedName("notificationTone")
    public Uri notificationTone;
    @SerializedName("notificationToneName")
    public String notificationToneName;
    @SerializedName("notificationVibrate")
    public boolean notificationVibrate;
    //Alarms
    @SerializedName("alarm")
    public boolean alarm;
    @SerializedName("alarmTone")
    public Uri alarmTone;
    @SerializedName("alarmToneName")
    public String alarmToneName;
    @SerializedName("alarmVibrate")
    public boolean alarmVibrate;
    //TODO alarm tone and notification tone
    //Generated data
    public ArrayList<TimeItem> alertTimes;

    public enum Distribution {
        EVEN, PART_RANDOM, MOST_RANDOM, FULL_RANDOM
    }

    /**
     * Creates a new reminder item with all the default values set
     */
    public ReminderItem() {
        //Unique name
        this.uniqueName = getUniqueName();
        //Title
        this.title = "";
        this.enabled = true;
        //Timing
        this.startTime = new TimeItem(9, 0);
        this.endTime = new TimeItem(20, 0);
        this.numberPerDay = 6;
        this.distribution = Distribution.PART_RANDOM;
        //Repeat
        this.repeat = true;
        this.daysToRun = new boolean[] {true, true, true, true, true, true, true};
        //Notifications
        this.notification = true;
        this.notificationTone = null;
        this.notificationToneName = "None";
        this.notificationVibrate = false;
        //Alarms
        this.alarm = false;
        this.alarmTone = null;
        this.alarmToneName = "None";
        this.alarmVibrate = false;
        //Generated values
        this.alertTimes = new ArrayList<>();
    }

    /**
     * @param title Title
     * @param enabled True to enable this reminder
     * @param startTime Start time object
     * @param endTime End time object
     * @param numberPerDay Number per day
     * @param distribution Distribution
     * @param daysToRun Days to run
     * @param notification True to enable notification
     * @param notificationTone The uri of the desired notification tone
     * @param notificationToneName The readable name of the notification tone
     * @param notificationVibrate True to enable vibrate with the notification
     * @param alarm True to enable alarm
     * @param alarmTone The uri of the desired alarm tone
     * @param alarmToneName The readable name of the alarm tone
     * @param alarmVibrate True to enable vibrate with the alarm
     * @param alertTimes List of calculated alert times
     */
    public ReminderItem(@NonNull String title, boolean enabled, @NonNull TimeItem startTime, @NonNull TimeItem endTime, int numberPerDay,
                        @NonNull Distribution distribution, boolean repeat, @NonNull boolean[] daysToRun,
                        boolean notification, Uri notificationTone, String notificationToneName, boolean notificationVibrate,
                        boolean alarm, Uri alarmTone, String alarmToneName, boolean alarmVibrate, @NonNull ArrayList<TimeItem> alertTimes) {
        this.uniqueName = getUniqueName();
        this.title = title;
        this.enabled = enabled;
        this.startTime = startTime.copy();
        this.endTime = endTime;
        this.numberPerDay = numberPerDay;
        this.distribution = distribution;
        this.repeat = repeat;
        this.daysToRun = daysToRun.clone();
        this.notification = notification;
        this.notificationTone = notificationTone;
        this.notificationToneName = notificationToneName;
        this.notificationVibrate = notificationVibrate;
        this.alarm = alarm;
        this.alarmTone = alarmTone;
        this.alarmToneName = alarmToneName;
        this.alarmVibrate = alarmVibrate;
        this.alertTimes = (ArrayList<TimeItem>) alertTimes.clone(); //Ignore
    }

    /**
     * @return A deep copy of this item
     */
    @NonNull
    public final ReminderItem copy() {
        return new ReminderItem(title, enabled, startTime, endTime, numberPerDay, distribution,
                repeat, daysToRun, notification, notificationTone, notificationToneName, notificationVibrate,
                alarm, alarmTone, alarmToneName, alarmVibrate, alertTimes);
    }

    @Override
    public boolean equals (Object obj){
        if(obj != null && obj instanceof ReminderItem) {
            ReminderItem item = (ReminderItem) obj;
            return this.uniqueName.equals(item.uniqueName);
        } else {
            return false;
        }
    }

    private static String getUniqueName() {
        return UUID.randomUUID().toString();
    }

}
