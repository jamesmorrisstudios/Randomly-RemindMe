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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.FileWriter;
import com.jamesmorrisstudios.utilitieslibrary.Serializer;
import com.jamesmorrisstudios.utilitieslibrary.notification.Notifier;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Reminder list control class. Add, remove, save, delete reminders
 *
 * Created by James on 4/20/2015.
 */
public final class ReminderList {
    //Constants
    private static final String TAG = "ReminderList";
    private static final String saveName = "SAVEDATA";
    private static final String stringType = "UTF-8";
    //Reminder singleton instance
    private static ReminderList instance = null;
    //Reminder List
    private Reminders reminders = new Reminders();
    //The currently selected reminder as a copy
    private int currentIndex = -1;
    private ReminderItem currentItem;

    AsyncTask<Void, Void, Boolean> taskLoad = null;
    AsyncTask<Void, Void, Boolean> taskSave = null;

    /**
     * Required private constructor to maintain singleton
     */
    private ReminderList() {}

    /**
     * @return The singleton instance of the reminderList
     */
    public static ReminderList getInstance() {
        if(instance == null) {
            instance = new ReminderList();
        }
        return instance;
    }

    /**
     * Events to post
     *
     * @param event Enum to post
     */
    private static void postReminderListEvent(@NonNull ReminderListEvent event) {
        Bus.postEnum(event);
    }

    /**
     * Events
     */
    public enum ReminderListEvent {
        DATA_LOAD_PASS,
        DATA_LOAD_FAIL,
        DATA_SAVE_PASS,
        DATA_SAVE_FAIL
    }

    /**
     * Loads the reminder list data on the calling thread instead of an asynctask
     * @return True if successful
     */
    public final boolean loadDataSync() {
        boolean status = loadFromFile();
        if(status) {
            validateSaveData();
        }
        return status;
    }

    /**
     * Loads the reminder list from disk. If already loaded it posts instantly
     * subscribe to Event.DATA_LOAD_PASS and Event.DATA_LOAD_FAIL for callbacks
     * @param forceRefresh True to force reload from disk
     */
    public final void loadData(boolean forceRefresh) {
        if(!forceRefresh && hasReminders()) {
            ReminderList.postReminderListEvent(ReminderListEvent.DATA_LOAD_PASS);
        } else {
            taskLoad = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return loadFromFile();
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if(value) {
                        validateSaveData();
                        ReminderList.postReminderListEvent(ReminderListEvent.DATA_LOAD_PASS);
                    } else {
                        ReminderList.postReminderListEvent(ReminderListEvent.DATA_LOAD_FAIL);
                    }
                    taskLoad = null;
                }
            };
            taskLoad.execute();
        }
    }

    public final boolean isLoadInProgress() {
        return taskLoad != null;
    }

    /**
     * Saves the reminder list data on the calling thread instead of an asynctask
     * @return True if successful
     */
    public final boolean saveDataSync() {
        return saveToFile();
    }

    /**
     * Saves the reminderItem data back to disk with an asynctask
     * Subscribe to Event.DATA_SAVE_PASS and Event.DATA_SAVE_FAIL for completion events
     */
    public final void saveData() {
        taskSave = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return saveToFile();
            }

            @Override
            protected void onPostExecute(Boolean value) {
                if(value) {
                    ReminderList.postReminderListEvent(ReminderListEvent.DATA_SAVE_PASS);
                } else {
                    ReminderList.postReminderListEvent(ReminderListEvent.DATA_SAVE_FAIL);
                }
                taskSave = null;
            }
        };
        taskSave.execute();
    }

    public final boolean isSaveInProgress() {
        return taskSave != null;
    }

    /**
     * @return True if reminders exist
     */
    public final boolean hasReminders() {
        return !reminders.data.isEmpty();
    }

    /**
     * Get the reminders. This will be an empty list if you have not already loaded them.
     * @return The list of reminders
     */
    @NonNull
    public final ArrayList<ReminderItem> getData() {
        return reminders.data;
    }

    /**
     * @param item Reminder reminder to set to
     */
    public final void setCurrentReminder(@NonNull ReminderItem item) {
        int index = 0;
        for(ReminderItem itemInt : reminders.data) {
            if(itemInt.equals(item)) {
                this.currentIndex = index;
                this.currentItem = reminders.data.get(currentIndex).copy();
                return;
            }
            index++;
        }
    }

    public final void setCurrentReminder(@NonNull String uniqueName) {
        int index = 0;
        for(ReminderItem itemInt : reminders.data) {
            if(itemInt.uniqueName.equals(uniqueName)) {
                this.currentIndex = index;
                this.currentItem = reminders.data.get(currentIndex).copy();
                return;
            }
            index++;
        }
    }

    @Nullable
    public final ReminderItem getReminder(@NonNull String uniqueName) {
        for(ReminderItem item : reminders.data) {
            if(item.uniqueName.equals(uniqueName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Clears the currently set reminder. If not saved any changes to it will be lost
     */
    public final void clearCurrentReminder() {
        this.currentIndex = -1;
        this.currentItem = null;
    }

    /**
     * Resets the current reminder back to the last saved version
     */
    public final void cancelCurrentReminderChanges() {
        if(currentIndex != -1) {
            this.currentItem = null;
            currentItem = reminders.data.get(currentIndex).copy();
        }
    }

    /**
     * Deletes the current reminder from the list.
     * Clears it from the currently set
     */
    public final void deleteCurrentReminder() {
        if(currentIndex != -1) {
            reminders.data.remove(currentIndex);
        }
        clearCurrentReminder();
    }

    /**
     * Create a new reminder with default values and set it to current
     */
    public final void createNewReminder() {
        currentIndex = reminders.data.size();
        reminders.data.add(new ReminderItem());
        currentItem = reminders.data.get(currentIndex).copy();
    }

    /**
     * Displays a notification preview of the current reminder
     */
    public final void previewCurrent() {
        ReminderItem item = getCurrentReminder();
        if(item == null) {
            return;
        }
        Notifier.buildNotification(item.getNotification(true));
    }

    /**
     * Saves the current reminder back to the list.
     * If its a new reminder it is added to the end of the list
     * The current reminder is NOT cleared
     */
    public final void saveCurrentReminder() {
        if(currentItem != null) {
            currentItem.updateAlertTimes();
            //Existing reminder so copy over the original
            reminders.data.set(currentIndex, currentItem.copy());
        }
    }

    /**
     * Duplicates the currently selected reminder reminder
     * and moves the new reminder to the end of the list.
     * The current reminder stays selected
     */
    public final void duplicateReminder() {
        currentItem.updateAlertTimes();
        reminders.data.add(currentItem.duplicate());
    }

    /**
     * @return True if a current reminder is set
     */
    public final boolean hasCurrentReminder() {
        return currentItem != null;
    }

    /**
     * @return The current reminder, null if none
     */
    @Nullable
    public final ReminderItem getCurrentReminder() {
        return currentItem;
    }

    /**
     * Updates all reminders wake times
     * This is stored in the Android shared prefs. Not the primary serialized save for this app.
     */
    public final void recalculateWakes() {
        for(ReminderItem item : reminders.data) {
            item.updateAlertTimes();
        }
    }

    /**
     * @return Return list of all reminder items that have a wake time that is current or past.
     */
    @NonNull
    public final ArrayList<ReminderItem> getCurrentWakes(TimeItem startTime, TimeItem endTime) {
        ArrayList<ReminderItem> items = new ArrayList<>();
        //Loop through all reminders
        for(ReminderItem item : reminders.data) {
            //See if the reminder is enabled
            if(item.enabled && item.daysToRun[getDayOfWeek()] && (timeInBoundsInclusive(item.startTime, item.endTime) || !item.rangeTiming)) {
                //Get the alert times for the given reminder
                ArrayList<TimeItem> alertTimes = ReminderItem.getAlertTimes(item.uniqueName);
                for(TimeItem time : alertTimes) {
                    //Check if the alert needs to be shown.
                    if(timeInBoundsInclusiveEnd(startTime, endTime, time)) {
                        //Max of one wake per reminder!
                        items.add(item);
                        break;
                    }
                }
            }
        }
        return items;
    }

    /**
     * @return The current day of the week
     */
    private int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1; //These are indexed starting at 1
    }

    /**
     * @return The time reminder of the next wake in this cycle. Null if no more today
     */
    @Nullable
    public final TimeItem getNextWake(TimeItem timeNow) {
        TimeItem time = null;
        //Schedule the next wake we have in this days cycle if any
        for(ReminderItem item : reminders.data) {
            if(!item.enabled) {
                continue;
            }
            ArrayList<TimeItem> alertTimes = ReminderItem.getAlertTimes(item.uniqueName);
            if(alertTimes.isEmpty()) {
                continue;
            }
            for(TimeItem alertTime : alertTimes) {
                //alert time is after the current time and (the current reminder is null or this time is before it)
                if(!timeBeforeOrEqual(alertTime, timeNow) && (time == null || timeBeforeOrEqual(alertTime, time))) {
                    time = alertTime;
                }
            }
        }
        return time;
    }

    /**
     * Check that the given time is within the given bounds inclusive of end value only
     * @param start Start time
     * @param end End time
     * @param value Time to check against
     * @return True if within
     */
    private boolean timeInBoundsInclusiveEnd(@NonNull TimeItem start, @NonNull TimeItem end, @NonNull TimeItem value) {
        return timeBefore(start, value) && timeBeforeOrEqual(value, end);
    }

    /**
     * Check that the current time is within the given bounds inclusive of both end points
     * @param start Start time
     * @param end End time
     * @return True if within
     */
    private boolean timeInBoundsInclusive(@NonNull TimeItem start, @NonNull TimeItem end) {
        TimeItem timeNow = UtilsTime.getTimeNow();
        return timeBeforeOrEqual(start, timeNow) && timeBeforeOrEqual(timeNow, end);
    }

    /**
     * @param newTime New time
     * @param oldTime Old time
     * @return True if new time is before or equal to old time
     */
    private boolean timeBeforeOrEqual(@NonNull TimeItem newTime, @NonNull TimeItem oldTime) {
        return (newTime.hour * 60 + newTime.minute) - (oldTime.hour * 60 + oldTime.minute) <= 0;
    }

    /**
     * @param newTime New time
     * @param oldTime Old time
     * @return True if new time is before old time
     */
    private boolean timeBefore(@NonNull TimeItem newTime, @NonNull TimeItem oldTime) {
        return (newTime.hour * 60 + newTime.minute) - (oldTime.hour * 60 + oldTime.minute) < 0;
    }

    /**
     * This is to fix a bug where duplicated reminders had the same unique name.
     */
    private void validateSaveData() {
        if(reminders.data == null || reminders.data.isEmpty() || reminders.data.size() < 2) {
            return;
        }
        for(int i=0; i<reminders.data.size(); i++) {
            ReminderItem firstItem = reminders.data.get(i);
            for(int j=i+1; j<reminders.data.size(); j++) {
                ReminderItem secondItem = reminders.data.get(j);
                if(firstItem.uniqueName.equals(secondItem.uniqueName)) {
                    secondItem.uniqueName = ReminderItem.getUniqueName();
                }
            }
        }
    }

    /**
     * Saves the reminder list to file
     * @return True if successful
     */
    private boolean saveToFile() {
        byte[] bytes = Serializer.serializeClass(reminders);
        return bytes != null && FileWriter.writeFile(saveName, bytes, false);
    }

    /**
     * Loads the reminder list from file
     * @return True if successful
     */
    private boolean loadFromFile() {
        if(!FileWriter.doesFileExist(saveName, false)) {
            return true;
        }
        byte[] bytes = FileWriter.readFile(saveName, false);
        if(bytes == null) {
            return false;
        }
        reminders = Serializer.deserializeClass(bytes, Reminders.class);
        if(reminders != null && reminders.data != null) {
            Log.v("ReminderList", "load save pass");
            return true;
        }
        return false;
    }

}
