package jamesmorrisstudios.com.randremind.editReminder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderRepeat {
    private ButtonCircleFlat[] dayButtons = new ButtonCircleFlat[7];

    public EditReminderRepeat(View parent) {
        dayButtons[0] = (ButtonCircleFlat) parent.findViewById(R.id.day0);
        dayButtons[1] = (ButtonCircleFlat) parent.findViewById(R.id.day1);
        dayButtons[2] = (ButtonCircleFlat) parent.findViewById(R.id.day2);
        dayButtons[3] = (ButtonCircleFlat) parent.findViewById(R.id.day3);
        dayButtons[4] = (ButtonCircleFlat) parent.findViewById(R.id.day4);
        dayButtons[5] = (ButtonCircleFlat) parent.findViewById(R.id.day5);
        dayButtons[6] = (ButtonCircleFlat) parent.findViewById(R.id.day6);
        initDaysOfWeek();
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        for (int i = 0; i < remind.daysToRun.length; i++) {
            setDayOfWeek(i, remind.daysToRun[i]);
        }
        repeatDaysListener();
    }

    /**
     * Configure the day of week views with proper text
     */
    private void initDaysOfWeek() {
        String[] week = UtilsTime.getWeekStringFirstLetterArray();
        for (int i = 0; i < week.length; i++) {
            TextView text = dayButtons[i].getTextView();
            if (text != null) {
                text.setText(week[i]);
            }
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
        TextView text = dayButton.getTextView();
        if(text == null) {
            return;
        }
        if (active) {
            text.setTextColor(AppUtil.getContext().getResources().getColor(R.color.textLightMain));
        } else {
            text.setTextColor(AppUtil.getContext().getResources().getColor(R.color.textDarkMain));
        }
    }

    /**
     * Add repeat category listeners
     */
    private void repeatDaysListener() {
        //Day of week selector
        for (int i = 0; i < dayButtons.length; i++) {
            final int index = i;
            dayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View v) {
                    ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                    if (remind == null) {
                        return;
                    }
                    remind.daysToRun[index] = !dayButtons[index].getActive();
                    setDayOfWeek(index, !dayButtons[index].getActive());
                }
            });
        }
    }
}
