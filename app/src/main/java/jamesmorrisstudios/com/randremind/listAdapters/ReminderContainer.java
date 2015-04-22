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

package jamesmorrisstudios.com.randremind.listAdapters;

import android.support.annotation.NonNull;

import jamesmorrisstudios.com.randremind.reminder.ReminderItem;

/**
 * Container for reminder item that abstracts it for use in the recyclerView
 *
 * Created by James on 3/31/2015.
 */
public final class ReminderContainer {
    //General
    public final boolean isHeader;

    //If Header
    public final String headerTitle;

    //Not header data
    public final ReminderItem item;

    /**
     * Constructor for header
     * @param headerTitle Title
     */
    public ReminderContainer(@NonNull String headerTitle) {
        this.isHeader = true;
        this.headerTitle = headerTitle;
        this.item = null;
    }

    /**
     * Constructor for normal item
     * @param item achievement item
     */
    public ReminderContainer(@NonNull ReminderItem item) {
        this.isHeader = false;
        this.item = item;
        this.headerTitle = null;
    }

}
