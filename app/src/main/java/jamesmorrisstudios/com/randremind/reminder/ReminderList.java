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

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.utilities.Bus;

/**
 * Created by James on 4/20/2015.
 */
public final class ReminderList {
    private static ReminderList instance = null;
    private ArrayList<ReminderItem> data = new ArrayList<>();

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

    public final ArrayList<ReminderItem> getData() {
        return data;
    }

}
