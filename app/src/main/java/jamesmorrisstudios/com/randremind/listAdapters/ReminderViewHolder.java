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
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jamesmorrisstudios.materialuilibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleViewHolder;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.TintImageView;
import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Reminder view holder for use in RecyclerView
 */
public final class ReminderViewHolder extends BaseRecycleViewHolder {
    //Not Header
    private CardView topLayout;
    private TextView title, startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private SwitchCompat enabled;
    private View dash, endTop;
    private ButtonCircleFlat[] dayButtons;
    private TintImageView timingRandom;
    private TextView timingTimes;

    /**
     * Constructor
     * @param view Parent view
     * @param isHeader True if header reminder, false for normal
     * @param mListener Click listener. Null if none desired
     */
    public ReminderViewHolder(@NonNull View view, boolean isHeader, @Nullable cardClickListener mListener) {
        super(view, isHeader, mListener);
    }

    @Override
    protected void initHeader(View view) {

    }

    @Override
    protected void initItem(View view) {
        topLayout = (CardView) view.findViewById(R.id.reminder_card);
        title = (TextView) view.findViewById(R.id.reminder_title_text);
        topLayout.setOnClickListener(this);
        enabled = (SwitchCompat) view.findViewById(R.id.reminder_enabled);
        View startTop = view.findViewById(R.id.reminder_time_start);
        startHour = (TextView) startTop.findViewById(R.id.time_hour);
        startMinute = (TextView) startTop.findViewById(R.id.time_minute);
        startAM = (TextView) startTop.findViewById(R.id.time_am);
        startPM = (TextView) startTop.findViewById(R.id.time_pm);
        endTop = view.findViewById(R.id.reminder_time_end);
        endHour = (TextView) endTop.findViewById(R.id.time_hour);
        endMinute = (TextView) endTop.findViewById(R.id.time_minute);
        endAM = (TextView) endTop.findViewById(R.id.time_am);
        endPM = (TextView) endTop.findViewById(R.id.time_pm);
        dash = view.findViewById(R.id.timing_dash);
        dayButtons = new ButtonCircleFlat[7];
        dayButtons[0] = (ButtonCircleFlat) view.findViewById(R.id.daySun);
        dayButtons[1] = (ButtonCircleFlat) view.findViewById(R.id.dayMon);
        dayButtons[2] = (ButtonCircleFlat) view.findViewById(R.id.dayTue);
        dayButtons[3] = (ButtonCircleFlat) view.findViewById(R.id.dayWed);
        dayButtons[4] = (ButtonCircleFlat) view.findViewById(R.id.dayThu);
        dayButtons[5] = (ButtonCircleFlat) view.findViewById(R.id.dayFri);
        dayButtons[6] = (ButtonCircleFlat) view.findViewById(R.id.daySat);
        dayButtons[0].getTextView().setText("S");
        dayButtons[1].getTextView().setText("M");
        dayButtons[2].getTextView().setText("T");
        dayButtons[3].getTextView().setText("W");
        dayButtons[4].getTextView().setText("T");
        dayButtons[5].getTextView().setText("F");
        dayButtons[6].getTextView().setText("S");
        timingRandom = (TintImageView) view.findViewById(R.id.timing_random);
        timingTimes = (TextView) view.findViewById(R.id.timing_times);
    }

    @Override
    protected void bindHeader(BaseRecycleItem baseRecycleItem, boolean expanded) {

    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean expanded) {
        final ReminderItem reminder = (ReminderItem) baseRecycleItem;

        String title = reminder.title;
        if(title == null || title.isEmpty()) {
            title = AppUtil.getContext().getString(R.string.default_title);
        }
        this.title.setText(title);
        if(reminder.rangeTiming) {
            UtilsTime.setTime(startHour, startMinute, startAM, startPM, reminder.startTime);
            UtilsTime.setTime(endHour, endMinute, endAM, endPM, reminder.endTime);
            endTop.setVisibility(View.VISIBLE);
            dash.setVisibility(View.VISIBLE);
        } else {
            UtilsTime.setTime(startHour, startMinute, startAM, startPM, reminder.singleTime);
            endTop.setVisibility(View.INVISIBLE);
            dash.setVisibility(View.INVISIBLE);
        }
        enabled.setOnCheckedChangeListener(null);
        enabled.setChecked(reminder.enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderList.getInstance().setEnableReminder(reminder.uniqueName, isChecked);
            }
        });
        for(int i=0; i<reminder.daysToRun.length; i++) {
            setDayOfWeek(i, reminder.daysToRun[i]);
        }
        if(reminder.rangeTiming) {
            timingTimes.setText(Integer.toString(reminder.numberPerDay));
            timingRandom.setVisibility(View.VISIBLE);
            if(reminder.randomDistribution) {
                timingRandom.setAlpha(1.0f);
            } else {
                timingRandom.setAlpha(0.12f);
            }
        } else {
            timingTimes.setVisibility(View.INVISIBLE);
            timingRandom.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set the active state of the day of week reminder
     * @param dayIndex Index for the day
     * @param active True to enable
     */
    private void setDayOfWeek(int dayIndex, boolean active) {
        final ButtonCircleFlat dayButton = dayButtons[dayIndex];
        dayButton.setActivated(active);
        if(active) {
            dayButton.getTextView().setTextColor(AppUtil.getContext().getResources().getColor(R.color.textLightMain));
        } else {
            dayButton.getTextView().setTextColor(AppUtil.getContext().getResources().getColor(R.color.textDarkMain));
        }
    }

}
