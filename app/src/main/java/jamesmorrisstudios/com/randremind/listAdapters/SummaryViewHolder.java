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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleViewHolder;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.utilitieslibrary.controls.CircleProgressDeterminate;
import com.jamesmorrisstudios.utilitieslibrary.controls.TintedImageView;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;
import com.nineoldandroids.view.ViewHelper;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.ReminderLogDay;

/**
 * Reminder view holder for use in RecyclerView
 */
public final class SummaryViewHolder extends BaseRecycleViewHolder {
    //Header
    private TextView title, hour1, minute1, AM1, PM1, hour2, minute2, AM2, PM2, hour3, minute3, AM3, PM3, dash1, dash2;
    private SwitchCompat enabled;
    private View top1, top2, top3;
    private ButtonCircleFlat[] dayButtons;
    //private Toolbar toolbar;

    //Item
    private TextView date, show, acked, percent;
    private CircleProgressDeterminate percentImage;

    /**
     * Constructor
     *
     * @param view      Parent view
     * @param isHeader  True if header reminder, false for normal
     * @param mListener Click listener. Null if none desired
     */
    public SummaryViewHolder(@NonNull View view, boolean isHeader, boolean isDummyItem, @Nullable cardClickListener mListener) {
        super(view, isHeader, isDummyItem, mListener);
    }

    @Override
    protected void initHeader(View view) {
        CardView topLayout = (CardView) view.findViewById(R.id.reminder_card);
        title = (TextView) view.findViewById(R.id.reminder_title_text);
        topLayout.setOnClickListener(this);
        enabled = (SwitchCompat) view.findViewById(R.id.reminder_enabled);

        //toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.menu_add_new);

        top1 = view.findViewById(R.id.reminder_time_1);
        hour1 = (TextView) top1.findViewById(R.id.time_hour);
        minute1 = (TextView) top1.findViewById(R.id.time_minute);
        AM1 = (TextView) top1.findViewById(R.id.time_am);
        PM1 = (TextView) top1.findViewById(R.id.time_pm);
        top2 = view.findViewById(R.id.reminder_time_2);
        hour2 = (TextView) top2.findViewById(R.id.time_hour);
        minute2 = (TextView) top2.findViewById(R.id.time_minute);
        AM2 = (TextView) top2.findViewById(R.id.time_am);
        PM2 = (TextView) top2.findViewById(R.id.time_pm);
        top3 = view.findViewById(R.id.reminder_time_3);
        hour3 = (TextView) top3.findViewById(R.id.time_hour);
        minute3 = (TextView) top3.findViewById(R.id.time_minute);
        AM3 = (TextView) top3.findViewById(R.id.time_am);
        PM3 = (TextView) top3.findViewById(R.id.time_pm);
        dash1 = (TextView) view.findViewById(R.id.timing_dash_1);
        dash2 = (TextView) view.findViewById(R.id.timing_dash_2);

        dayButtons = new ButtonCircleFlat[7];
        dayButtons[0] = (ButtonCircleFlat) view.findViewById(R.id.daySun);
        dayButtons[1] = (ButtonCircleFlat) view.findViewById(R.id.dayMon);
        dayButtons[2] = (ButtonCircleFlat) view.findViewById(R.id.dayTue);
        dayButtons[3] = (ButtonCircleFlat) view.findViewById(R.id.dayWed);
        dayButtons[4] = (ButtonCircleFlat) view.findViewById(R.id.dayThu);
        dayButtons[5] = (ButtonCircleFlat) view.findViewById(R.id.dayFri);
        dayButtons[6] = (ButtonCircleFlat) view.findViewById(R.id.daySat);
        String[] week = UtilsTime.getWeekStringFirstLetterArray();
        for (int i = 0; i < week.length; i++) {
            TextView text = dayButtons[i].getTextView();
            if (text != null) {
                text.setText(week[i]);
            }
        }
    }

    @Override
    protected void initItem(View view) {
        date = (TextView) view.findViewById(R.id.date);
        percent = (TextView) view.findViewById(R.id.percentage);
        percentImage = (CircleProgressDeterminate) view.findViewById(R.id.percentage_image);
        show = (TextView) view.findViewById(R.id.shown);
        acked = (TextView) view.findViewById(R.id.acked);
    }

    @Override
    protected void bindHeader(BaseRecycleItem baseRecycleItem, boolean expanded) {
        final ReminderItem reminder = (ReminderItem) baseRecycleItem;

        String title = reminder.title;
        if (title == null || title.isEmpty()) {
            title = AppUtil.getContext().getString(R.string.default_title);
        }
        this.title.setText(title);
        if (reminder.rangeTiming) {
            UtilsTime.setTime(hour1, minute1, AM1, PM2, reminder.startTime);
            UtilsTime.setTime(hour2, minute2, AM2, PM2, reminder.endTime);
            dash1.setText(AppUtil.getContext().getString(R.string.dash));
            top1.setVisibility(View.VISIBLE);
            top2.setVisibility(View.VISIBLE);
            dash1.setVisibility(View.VISIBLE);
            dash2.setVisibility(View.INVISIBLE);
            top3.setVisibility(View.INVISIBLE);
        } else {
            dash1.setText(AppUtil.getContext().getString(R.string.comma));
            if(reminder.specificTimeList.size() >= 1) {
                UtilsTime.setTime(hour1, minute1, AM1, PM1, reminder.specificTimeList.get(0));
            }
            if(reminder.specificTimeList.size() >= 2) {
                UtilsTime.setTime(hour2, minute2, AM2, PM2, reminder.specificTimeList.get(1));
                dash1.setVisibility(View.VISIBLE);
                top2.setVisibility(View.VISIBLE);
            } else {
                dash1.setVisibility(View.INVISIBLE);
                top2.setVisibility(View.INVISIBLE);
            }
            if(reminder.specificTimeList.size() >= 3) {
                UtilsTime.setTime(hour3, minute3, AM3, PM3, reminder.specificTimeList.get(2));
                dash2.setVisibility(View.VISIBLE);
                top3.setVisibility(View.VISIBLE);
            } else {
                dash2.setVisibility(View.INVISIBLE);
                top3.setVisibility(View.INVISIBLE);
            }
        }
        enabled.setOnCheckedChangeListener(null);
        enabled.setChecked(reminder.enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderList.getInstance().setEnableReminder(reminder.uniqueName, isChecked);
            }
        });
        for (int i = 0; i < reminder.daysToRun.length; i++) {
            setDayOfWeek(i, reminder.daysToRun[i]);
        }
    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean expanded) {
        final ReminderLogDay day = (ReminderLogDay) baseRecycleItem;
        if (day.lifetime) {
            date.setText(AppUtil.getContext().getString(R.string.lifetime));
            float percentage = (100.0f * day.timesClickedLifetime) / day.timesShownLifetime;
            percent.setText(Integer.toString(Math.round(percentage)) + AppUtil.getContext().getResources().getString(R.string.percent_char));
            percentImage.setMax(day.timesShownLifetime);
            percentImage.setProgress(day.timesClickedLifetime);
            show.setText(Integer.toString(day.timesShownLifetime));
            acked.setText(Integer.toString(day.timesClickedLifetime));
        } else {
            if (day.date.equals(UtilsTime.getDateNow())) {
                date.setText(AppUtil.getContext().getResources().getString(R.string.today));
            } else {
                date.setText(UtilsTime.getDateFormatted(day.date));
            }
            float percentage = (100.0f * day.timesClicked.size()) / day.timesShown.size();
            percent.setText(Integer.toString(Math.round(percentage)) + AppUtil.getContext().getResources().getString(R.string.percent_char));
            percentImage.setMax(day.timesShown.size());
            percentImage.setProgress(day.timesClicked.size());
            show.setText(Integer.toString(day.timesShown.size()));
            acked.setText(Integer.toString(day.timesClicked.size()));
        }
    }

    /**
     * Set the active state of the day of week reminder
     *
     * @param dayIndex Index for the day
     * @param active   True to enable
     */
    private void setDayOfWeek(int dayIndex, boolean active) {
        final ButtonCircleFlat dayButton = dayButtons[dayIndex];
        dayButton.setActive(active);
        if (active) {
            dayButton.getTextView().setTextColor(AppUtil.getContext().getResources().getColor(R.color.textLightMain));
        } else {
            dayButton.getTextView().setTextColor(AppUtil.getContext().getResources().getColor(R.color.textDarkMain));
        }
    }

}
