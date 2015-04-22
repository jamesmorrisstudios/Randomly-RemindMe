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

import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * Time item for scheduling. This assumes 24 hour time. Adjust to AM, PM as needed
 *
 * Created by James on 4/21/2015.
 */
public final class TimeItem {
    public int hour;
    public int minute;

    /**
     * Constructor
     * @param hour Starting hour
     * @param minute Starting minute
     */
    public TimeItem(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * @return A copy of this object
     */
    @NonNull
    public final TimeItem copy() {
        return new TimeItem(this.hour, this.minute);
    }

    public final boolean is24Hour() {
        return Utils.is24HourTime();
    }

    public final boolean isAM() {
        return Utils.isAM(hour);
    }

    public final int getHourInTimeFormat() {
        return Utils.getHourInTimeFormat(hour);
    }

    public final String getHourInTimeFormatString() {
        return Integer.toString(Utils.getHourInTimeFormat(hour));
    }

    public final String getMinuteString() {
        return String.format("%02d", minute);
    }

    @Override
    public final boolean equals (Object obj){
        if(obj != null && obj instanceof TimeItem) {
            TimeItem item = (TimeItem) obj;
            return this.hour == item.hour && this.minute == item.minute;
        } else {
            return false;
        }
    }

}
