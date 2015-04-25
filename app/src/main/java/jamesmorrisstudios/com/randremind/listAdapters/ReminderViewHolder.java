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
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.application.App;
import jamesmorrisstudios.com.randremind.reminder.TimeItem;
import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * Reminder view holder for use in RecyclerView
 */
public final class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //Type of view. header or normal
    private boolean isHeader;
    //Click listener
    public cardClickListener mListener;
    //Header
    private TextView headerTitle;
    //Not Header
    private TextView title, startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private SwitchCompat enabled;

    /**
     * Constructor
     * @param view Parent view
     * @param isHeader True if header item, false for normal
     * @param mListener Click listener. Null if none desired
     */
    public ReminderViewHolder(@NonNull View view, boolean isHeader, @Nullable cardClickListener mListener) {
        super(view);
        this.isHeader = isHeader;
        this.mListener = mListener;

        if(isHeader) {
            headerTitle = (TextView) view.findViewById(R.id.reminder_main_text);
        } else {
            CardView topLayout = (CardView) view.findViewById(R.id.reminder_card);
            title = (TextView) view.findViewById(R.id.reminder_title_text);
            topLayout.setOnClickListener(this);
            enabled = (SwitchCompat) view.findViewById(R.id.reminder_enabled);
            View startTop = view.findViewById(R.id.reminder_time_start);
            startHour = (TextView) startTop.findViewById(R.id.time_hour);
            startMinute = (TextView) startTop.findViewById(R.id.time_minute);
            startAM = (TextView) startTop.findViewById(R.id.time_am);
            startPM = (TextView) startTop.findViewById(R.id.time_pm);
            View endTop = view.findViewById(R.id.reminder_time_end);
            endHour = (TextView) endTop.findViewById(R.id.time_hour);
            endMinute = (TextView) endTop.findViewById(R.id.time_minute);
            endAM = (TextView) endTop.findViewById(R.id.time_am);
            endPM = (TextView) endTop.findViewById(R.id.time_pm);
        }
    }

    /**
     * Binds the given data to this view.
     * @param reminder Data to bind
     */
    public void bindItem(@NonNull final ReminderContainer reminder) {
        if(isHeader) {
            //Header only
            this.headerTitle.setText(reminder.headerTitle);
        } else {
            //Non header
            String title = reminder.item.title;
            if(title == null || title.isEmpty()) {
                title = App.getContext().getString(R.string.default_title);
            }
            this.title.setText(title);
            Utils.setTime(startHour, startMinute, startAM, startPM, reminder.item.startTime);
            Utils.setTime(endHour, endMinute, endAM, endPM, reminder.item.endTime);
            enabled.setOnCheckedChangeListener(null);
            enabled.setChecked(reminder.item.enabled);
            enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    reminder.item.enabled = isChecked;
                }
            });
        }
    }

    /**
     * @param v The view that was clicked
     */
    @Override
    public void onClick(@NonNull View v) {
        if(mListener != null) {
            mListener.cardClicked(v, getLayoutPosition());
        }
    }

    /**
     * Card click listener interface
     */
    public  interface cardClickListener {
        void cardClicked(@NonNull View caller, int position);
    }

}
