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

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleContainer;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleHeaderItem;

/**
 * Container for reminder reminder that abstracts it for use in the recyclerView
 *
 * Created by James on 3/31/2015.
 */
public final class ReminderContainer extends BaseRecycleContainer {
    private final ReminderListItem item;
    private final BaseRecycleHeaderItem headerItem;

    public ReminderContainer(@NonNull BaseRecycleHeaderItem headerItem) {
        super(true);
        this.item = null;
        this.headerItem = headerItem;
    }

    public ReminderContainer(@NonNull ReminderListItem item) {
        super(false);
        this.item = item;
        this.headerItem = null;
    }

    @Override
    public BaseRecycleHeaderItem getHeaderItem() {
        return headerItem;
    }

    @Override
    public ReminderListItem getItem() {
        return item;
    }
}
