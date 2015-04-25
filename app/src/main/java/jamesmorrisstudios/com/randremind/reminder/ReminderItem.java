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
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.application.App;

/**
 * Individual reminder item that contains all needed items to be a reminder
 *
 * Created by James on 4/20/2015.
 */
public final class ReminderItem {
    //Unique data
    @SerializedName("uniqueName")
    public final String uniqueName;
    @SerializedName("notificationId")
    public int notificationId;
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
    @SerializedName("notificationToneString")
    public String notificationTone;
    @SerializedName("notificationToneName")
    public String notificationToneName;
    @SerializedName("notificationVibrate")
    public boolean notificationVibrate;
    //Alarms
    @SerializedName("alarm")
    public boolean alarm;
    @SerializedName("alarmToneString")
    public String alarmTone;
    @SerializedName("alarmToneName")
    public String alarmToneName;
    @SerializedName("alarmVibrate")
    public boolean alarmVibrate;
    //Generated data
    @SerializedName("alertTimes")
    public ArrayList<TimeItem> alertTimes;

    /**
     * Timing distribution
     */
    public enum Distribution {
        EVEN, PART_RANDOM, MOST_RANDOM, FULL_RANDOM
    }

    /**
     * Creates a new reminder item with all the default values set
     */
    public ReminderItem() {
        //Unique name
        this.uniqueName = getUniqueName();
        this.notificationId = getNotifictionId();
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
        this.notificationToneName = App.getContext().getString(R.string.sound_none);
        this.notificationVibrate = false;
        //Alarms
        this.alarm = false;
        this.alarmTone = null;
        this.alarmToneName = App.getContext().getString(R.string.sound_none);
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
    public ReminderItem(@NonNull String uniqueName, int notificationId, @NonNull String title, boolean enabled, @NonNull TimeItem startTime, @NonNull TimeItem endTime, int numberPerDay,
                        @NonNull Distribution distribution, boolean repeat, @NonNull boolean[] daysToRun,
                        boolean notification, String notificationTone, String notificationToneName, boolean notificationVibrate,
                        boolean alarm, String alarmTone, String alarmToneName, boolean alarmVibrate, @NonNull ArrayList<TimeItem> alertTimes) {
        this.uniqueName = uniqueName;
        this.notificationId = notificationId;
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
        return new ReminderItem(uniqueName, notificationId, title, enabled, startTime, endTime, numberPerDay,
                distribution, repeat, daysToRun, notification, notificationTone, notificationToneName,
                notificationVibrate, alarm, alarmTone, alarmToneName, alarmVibrate, alertTimes);
    }

    /**
     * @return The notification tone as a Uri
     */
    public final Uri getNotificationTone() {
        if(notificationTone == null) {
            return null;
        }
        return Uri.parse(notificationTone);
    }

    /**
     * @return The alarm tone as a Uri
     */
    public final Uri getAlarmTone() {
        if(alarmTone == null) {
            return null;
        }
        return Uri.parse(alarmTone);
    }

    /**
     * @param obj Object to compare to
     * @return True if equal based on unique id.
     */
    @Override
    public boolean equals (@Nullable Object obj){
        if(obj != null && obj instanceof ReminderItem) {
            ReminderItem item = (ReminderItem) obj;
            return this.uniqueName.equals(item.uniqueName);
        } else {
            return false;
        }
    }

    /**
     * Generate new alert times given the current parameters
     */
    public final void updateAlertTimes() {
        this.alertTimes.clear();
        int diff = getDiffMinutes();
        int startOffset = timeToMinutes(startTime);

        //If even, partRandom, or mostRandom start with an even distribution with some wiggle room
        float wiggle = 0;
        switch(distribution) {
            case EVEN:
                wiggle = 0;
                generateEvenishSplit(diff, startOffset, wiggle, numberPerDay);
                break;
            case PART_RANDOM:
                wiggle = 0.35f;
                generateEvenishSplit(diff, startOffset, wiggle, numberPerDay);
                break;
            case MOST_RANDOM:
                wiggle = 0.75f;
                generateEvenishSplit(diff, startOffset, wiggle, numberPerDay);
                break;
            case FULL_RANDOM: //TODO make this more random
                wiggle = 1.0f;
                generateEvenishSplit(diff, startOffset, wiggle, numberPerDay);
                break;
        }
    }

    /**
     * Creates a distribution of values from offset to diff+offset
     * @param diff Total range of values
     * @param offset Offset amount to shift the entire range
     * @param wiggle Percent of space between items to allow randomization
     * @param numberItems Number of items to generate
     */
    private void generateEvenishSplit(int diff, int offset, float wiggle, int numberItems) {
        Random rand = new Random();
        int itemSplit = Math.round((diff * 1.0f) / numberItems);
        int[] values = new int[numberItems];
        for(int i=0; i<values.length; i++) {
            values[i] = Math.round((i * 1.0f) / (numberItems) * diff) + itemSplit/2 + Math.round((itemSplit * wiggle) * (rand.nextFloat() - 0.5f) );
            if(i > 0) {
                values[i] = Math.min(Math.max(values[i], values[i-1] + 30), diff);
            }
        }

        for (int value : values) {
            alertTimes.add(minutesToTimeItem(value + offset));
            Log.v(title, alertTimes.get(alertTimes.size()-1).getHourInTimeFormatString()+":"+alertTimes.get(alertTimes.size()-1).getMinuteString());
        }
    }

    /**
     * @return The difference in minutes
     */
    private int getDiffMinutes() {
        return timeToMinutes(endTime) - timeToMinutes(startTime);
    }

    /**
     * Converts a time item to minutes value
     * @param time Time item
     * @return Value in minutes
     */
    private int timeToMinutes(@NonNull TimeItem time) {
        return time.hour * 60 + time.minute;
    }

    /**
     * Converts minutes to time item
     * @param totalMinutes Minutes
     * @return Time item
     */
    private TimeItem minutesToTimeItem(int totalMinutes) {
        int hour = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return new TimeItem(hour, minutes);
    }

    /**
     * Truncates the UUID for a unique id
     * @return A unique notification id
     */
    private static int getNotifictionId() {
        return (int)UUID.randomUUID().getMostSignificantBits();
    }

    /**
     * Generates a unique name for the reminder
     * @return Unique name
     */
    @NonNull
    private static String getUniqueName() {
        return UUID.randomUUID().toString();
    }

}
