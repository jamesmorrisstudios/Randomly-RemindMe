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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by James on 4/20/2015.
 */
public class ReminderItem {
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
    @SerializedName("daysToRun")
    public boolean[] daysToRun; //Sunday -> Saturday
    //Alert Type
    @SerializedName("notification")
    public boolean notification;
    @SerializedName("alarm")
    public boolean alarm;
    @SerializedName("vibrate")
    public boolean vibrate;
    //TODO alarm tone and notification tone
    //Messages
    @SerializedName("messages")
    public ArrayList<String> messages;
    @SerializedName("messageOrder")
    public MessageOrder messageOrder;
    //Generated data
    public ArrayList<TimeItem> alertTimes;
    public int currentMessage;

    public enum Distribution {
        EVEN, PART_RANDOM, MOST_RANDOM, FULL_RANDOM
    }

    public enum MessageOrder {
        RANDOM, INCREMENT
    }

    /**
     * Creates a new reminder item with all the default values set
     */
    public ReminderItem() {
        //Title
        title = "New Reminder";
        enabled = true;
        //Timing
        startTime = new TimeItem(9, 0);
        endTime = new TimeItem(20, 0);
        numberPerDay = 6;
        distribution = Distribution.PART_RANDOM;
        //Repeat
        daysToRun = new boolean[] {true, true, true, true, true, true, true};
        //Alert Type
        notification = true;
        alarm = false;
        //Messages
        messages = new ArrayList<>();
        messages.add(title);
        messageOrder = MessageOrder.RANDOM;
        //Generated values
        alertTimes = new ArrayList<>();
        currentMessage = 0;
    }

    public ReminderItem(String title, boolean enabled, TimeItem startTime, TimeItem endTime, int numberPerDay,
                        Distribution distribution, boolean[] daysToRun, boolean notification, boolean alarm,
                        boolean vibrate, ArrayList<String> messages, MessageOrder messageOrder,
                        ArrayList<TimeItem> alertTimes, int currentMessage) {
        this.title = title;
        this.enabled = enabled;
        this.startTime = startTime.copy();
        this.endTime = endTime;
        this.numberPerDay = numberPerDay;
        this.distribution = distribution;
        this.daysToRun = daysToRun.clone();
        this.notification = notification;
        this.alarm = alarm;
        this.vibrate = vibrate;
        this.messages = (ArrayList<String>) messages.clone();
        this.messageOrder = messageOrder;
        this.alertTimes = (ArrayList<TimeItem>) alertTimes.clone();
        this.currentMessage = currentMessage;
    }

    public final ReminderItem copy() {
        return new ReminderItem(title, enabled, startTime, endTime, numberPerDay, distribution,
                daysToRun, notification, alarm, vibrate, messages, messageOrder, alertTimes, currentMessage);
    }

}
