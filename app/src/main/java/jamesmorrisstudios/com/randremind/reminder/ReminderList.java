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

import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.FileWriter;
import com.jamesmorrisstudios.utilitieslibrary.Serializer;
import com.jamesmorrisstudios.utilitieslibrary.notification.Notifier;
import com.jamesmorrisstudios.utilitieslibrary.time.DateTimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import java.util.ArrayList;

/**
 * Reminder list control class. Add, remove, save, delete reminders
 * <p/>
 * Created by James on 4/20/2015.
 */
public final class ReminderList {
    //Constants
    private static final String saveName = "SAVEDATA";
    //Reminder singleton instance
    private static ReminderList instance = null;
    AsyncTask<Void, Void, Boolean> taskLoad = null;
    AsyncTask<Void, Void, Boolean> taskSave = null;
    //Reminder List
    private Reminders reminders = null;
    //The currently selected reminder as a copy
    private int currentIndex = -1;
    private ReminderItem currentItem;

    /**
     * Required private constructor to maintain singleton
     */
    private ReminderList() {
    }

    /**
     * @return The singleton instance of the reminderList
     */
    public static ReminderList getInstance() {
        if (instance == null) {
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
     * Loads the reminder list data on the calling thread instead of an asynctask
     *
     * @return True if successful
     */
    public final boolean loadDataSync() {
        boolean status = loadFromFile();
        if (status) {
            validateSaveData();
        }
        return status;
    }

    /**
     * Loads the reminder list from disk. If already loaded it posts instantly
     * subscribe to Event.DATA_LOAD_PASS and Event.DATA_LOAD_FAIL for callbacks
     *
     * @param forceRefresh True to force reload from disk
     */
    public final void loadData(boolean forceRefresh) {
        if (!forceRefresh && hasReminders()) {
            ReminderList.postReminderListEvent(ReminderListEvent.DATA_LOAD_PASS);
        } else {
            taskLoad = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return loadFromFile();
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if (value) {
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
     *
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
                if (value) {
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
        return reminders != null && reminders.data != null && !reminders.data.isEmpty();
    }

    /**
     * Get the reminders. This will be an empty list if you have not already loaded them.
     *
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
        for (ReminderItem itemInt : reminders.data) {
            if (itemInt.equals(item)) {
                this.currentIndex = index;
                this.currentItem = reminders.data.get(currentIndex).copy();
                return;
            }
            index++;
        }
    }

    @Nullable
    public final ReminderItem getReminder(@NonNull String uniqueName) {
        for (ReminderItem item : reminders.data) {
            if (item.uniqueName.equals(uniqueName)) {
                return item;
            }
        }
        return null;
    }

    public final void setEnableReminder(@NonNull String uniqueName, boolean enable) {
        ReminderItem item = getReminder(uniqueName);
        if (item == null) {
            return;
        }
        if (currentItem != null && currentItem.equals(item)) {
            currentItem.enabled = enable;
            if (enable) {
                currentItem.rescheduleNextWake(UtilsTime.getDateTimeNow());
            } else {
                currentItem.deleteNextWake();
            }
            saveCurrentReminder();
            return;
        }
        item.enabled = enable;
        if (enable) {
            item.rescheduleNextWake(UtilsTime.getDateTimeNow());
        } else {
            item.deleteNextWake();
        }
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
        if (currentIndex != -1) {
            this.currentItem = null;
            currentItem = reminders.data.get(currentIndex).copy();
        }
    }

    /**
     * Deletes the current reminder from the list.
     * Clears it from the currently set
     */
    public final void deleteCurrentReminder() {
        if (currentIndex != -1) {
            reminders.data.get(currentIndex).deleteReminderLog();
            reminders.data.get(currentIndex).deleteAlertTimes();
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
        if (item == null) {
            return;
        }
        Notifier.buildNotification(item.getNotification(true, UtilsTime.getDateTimeNow()));
    }

    /**
     * Saves the current reminder back to the list.
     * If its a new reminder it is added to the end of the list
     * The current reminder is NOT cleared
     */
    public final void saveCurrentReminder() {
        if (currentItem != null) {
            currentItem.updateAlertTimes();
            currentItem.rescheduleNextWake(UtilsTime.getDateTimeNow());
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
        reminders.data.add(currentIndex + 1, currentItem.duplicate());
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

    public final void setCurrentReminder(@NonNull String uniqueName) {
        int index = 0;
        for (ReminderItem itemInt : reminders.data) {
            if (itemInt.uniqueName.equals(uniqueName)) {
                this.currentIndex = index;
                this.currentItem = reminders.data.get(currentIndex).copy();
                return;
            }
            index++;
        }
    }

    /**
     * Updates all reminders wake times
     * This is stored in the Android shared prefs. Not the primary serialized save for this app.
     */
    public final void recalculateWakes() {
        for (ReminderItem item : reminders.data) {
            item.updateAlertTimes();
        }
    }

    public final void scheduleAllWakes(DateTimeItem timeNow) {
        for (ReminderItem item : reminders.data) {
            item.rescheduleNextWake(timeNow);
        }
    }

    /**
     * This is to fix a bug where duplicated reminders had the same unique name.
     */
    private void validateSaveData() {
        if (reminders.data == null || reminders.data.isEmpty() || reminders.data.size() < 2) {
            return;
        }
        for (int i = 0; i < reminders.data.size(); i++) {
            ReminderItem firstItem = reminders.data.get(i);
            for (int j = i + 1; j < reminders.data.size(); j++) {
                ReminderItem secondItem = reminders.data.get(j);
                if (firstItem.uniqueName.equals(secondItem.uniqueName)) {
                    secondItem.uniqueName = ReminderItem.getUniqueName();
                }
            }
        }
    }

    /**
     * Saves the reminder list to file
     *
     * @return True if successful
     */
    private boolean saveToFile() {
        if(reminders != null && reminders.data != null) {
            byte[] bytes = Serializer.serializeClass(reminders);
            return bytes != null && FileWriter.writeFile(saveName, bytes, false);
        }
        return false;
    }

    /**
     * Loads the reminder list from file
     *
     * @return True if successful
     */
    private boolean loadFromFile() {
        if (!FileWriter.doesFileExist(saveName, false)) {
            reminders = new Reminders();
            return true;
        }
        byte[] bytes = FileWriter.readFile(saveName, false);
        if (bytes == null) {
            return false;
        }
        reminders = Serializer.deserializeClass(bytes, Reminders.class);
        if (reminders != null && reminders.data != null) {
            updateVersion(reminders);
            Log.v("ReminderList", "load save pass");
            return true;
        }
        return false;
    }

    private void updateVersion(@NonNull Reminders reminders) {
        for(ReminderItem item : reminders.data) {
            item.updateVersion();
        }
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

}
