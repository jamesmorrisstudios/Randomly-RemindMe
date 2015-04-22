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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.utilities.Bus;
import jamesmorrisstudios.com.randremind.utilities.FileWriter;

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
    @SerializedName("data")
    private ArrayList<ReminderItem> data = new ArrayList<>();
    //The currently selected reminder as a copy
    private int currentIndex = -1;
    private ReminderItem currentItem;

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
     * Loads the reminder list from disk. If already loaded it posts instantly
     * subscribe to Event.DATA_LOAD_PASS and Event.DATA_LOAD_FAIL for callbacks
     * @param forceRefresh True to force reload from disk
     */
    public final void loadData(boolean forceRefresh) {
        if(!forceRefresh && hasReminders()) {
            Bus.postEvent(Bus.Event.DATA_LOAD_PASS);
        } else {
            AsyncTask<Void, Void, Boolean> taskLoad = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return loadFromFile();
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if(value) {
                        Bus.postEvent(Bus.Event.DATA_LOAD_PASS);
                    } else {
                        Bus.postEvent(Bus.Event.DATA_LOAD_FAIL);
                    }
                }
            };
            taskLoad.execute();
        }
    }

    /**
     *
     */
    public final void saveData() {
        if(hasReminders()) {
            AsyncTask<Void, Void, Boolean> taskSave = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return saveToFile();
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if(value) {
                        Bus.postEvent(Bus.Event.DATA_SAVE_PASS);
                    } else {
                        Bus.postEvent(Bus.Event.DATA_SAVE_FAIL);
                    }
                }
            };
            taskSave.execute();
        } else {
            Bus.postEvent(Bus.Event.DATA_SAVE_PASS);
        }
    }

    public final boolean hasReminders() {
        return !data.isEmpty();
    }

    @NonNull
    public final ArrayList<ReminderItem> getData() {
        return data;
    }

    public final void setCurrentReminder(int currentIndex) {
        this.currentIndex = currentIndex;
        this.currentItem = data.get(currentIndex).copy();
    }

    public final void clearCurrentReminder() {
        this.currentIndex = -1;
        this.currentItem = null;
    }

    public final void deleteCurrentReminder() {
        if(currentIndex != -1) {
            data.remove(currentIndex);
        }
        clearCurrentReminder();
    }

    public final void createNewReminder() {
        currentIndex = -1;
        currentItem = new ReminderItem();
    }

    public final void saveCurrentReminder() {
        if(currentIndex == -1) {
            //New Item so add to end
            data.add(currentItem);
        } else {
            //Existing item so copy over the original
            data.set(currentIndex, currentItem.copy());
        }
        saveToFile();
    }

    public final boolean hasCurrentReminder() {
        return currentItem != null;
    }

    @Nullable
    public final ReminderItem getCurrentReminder() {
        return currentItem;
    }

    private boolean saveToFile() {
        byte[] bytes = serializeSave();
        return bytes != null && FileWriter.writeFile(saveName, bytes, false);
    }

    private boolean loadFromFile() {
        if(!FileWriter.doesFileExist(saveName, false)) {
            return true;
        }
        byte[] bytes = FileWriter.readFile(saveName, false);
        return bytes != null && deserializeSave(bytes);
    }

    @Nullable
    private byte[] serializeSave() {
        JSONObject retVal1 = new JSONObject();
        try {
            retVal1.put(ReminderList.TAG, new Gson().toJsonTree(data));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return retVal1.toString().getBytes(Charset.forName(stringType));
    }

    private boolean deserializeSave(@NonNull byte[] bytes) {
        String st;
        try {
            st = new String(bytes, stringType);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return false;
        }
        try {
            JSONObject obj = new JSONObject(st);
            data = new Gson().fromJson(obj.get(ReminderList.TAG).toString(), new TypeToken<ArrayList<ReminderItem>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
