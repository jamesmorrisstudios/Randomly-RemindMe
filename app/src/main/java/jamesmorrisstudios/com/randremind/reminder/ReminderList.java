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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.utilities.Bus;

/**
 * Created by James on 4/20/2015.
 */
public final class ReminderList {
    private static ReminderList instance = null;
    private ArrayList<ReminderItem> data = new ArrayList<>();
    private int currentIndex = -1;
    private ReminderItem currentItem;

    private ReminderList() {}

    public static ReminderList getInstance() {
        if(instance == null) {
            instance = new ReminderList();
        }
        return instance;
    }

    public final void loadData(boolean forceRefresh) {
        Bus.postEvent(Bus.Event.DATA_LOAD_PASS);
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
            currentIndex = data.size();
            data.add(currentItem);
        } else {
            //Existing item so copy over the original
            data.set(currentIndex, currentItem.copy());
        }
    }

    public final boolean hasCurrentReminder() {
        return currentItem != null;
    }

    @Nullable
    public final ReminderItem getCurrentReminder() {
        return currentItem;
    }

    private void saveToFile() {

    }

    private void loadFromFile() {

    }


    /*
    //Load the JSON byte array back into gamestate
    public final boolean loadGameDataBytes(byte[] bytes, Enum callback, boolean isUpdate) {
        gameState = getGameState(bytes);
        if (gameState == null) {
            return false;
        }
        return true;
    }

    //Helper function to load JSON byte array
    private GameState getGameState(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        String st = null;
        try {
            st = new String(bytes, Constants.STRING_TYPE);
            v("Save Game String: " + st);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        GameState gameState = null;
        try {
            //st = ApplicationTop.getContext().getResources().getString(R.string.force_match);
            JSONObject obj = new JSONObject(st);
            gameState = new Gson().fromJson(obj.get(GameState.TAG).toString(), new TypeToken<GameState>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameState;
    }
     */

}
