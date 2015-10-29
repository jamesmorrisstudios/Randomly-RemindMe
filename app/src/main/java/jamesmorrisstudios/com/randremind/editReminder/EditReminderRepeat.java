package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.MultiChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemData;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderRepeat {
    private View weeksContainer, daysContainer;
    private TextView weeks;
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
        weeksContainer = parent.findViewById(R.id.weeksContainer);
        daysContainer = parent.findViewById(R.id.daysContainer);
        weeks = (TextView) parent.findViewById(R.id.weeks);
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        for (int i = 0; i < remind.getDaysToRun().length; i++) {
            setDayOfWeek(i, remind.getDaysToRun()[i]);
        }
        repeatDaysListener();
        weeksListener();
        updateWeeksToRun();
        updateDaysOfWeek();
    }

    private void weeksListener() {
        weeksContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.weeks);
                String[] options = new String[ReminderItemData.WeekOptions.values().length];
                for (int i = 0; i < options.length; i++) {
                    options[i] = ReminderItemData.WeekOptions.values()[i].name;
                }
                final boolean[] checked = remind.getWeeksToRun().clone();

                Bus.postObject(new MultiChoiceRequest(title, options, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Each change
                        checked[which] = isChecked;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Confirm
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setWeeksToRun(checked);
                        updateWeeksToRun();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                }));
            }
        });
    }

    private void updateWeeksToRun() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        boolean anySelected = false;
        String text = "";
        if(remind.getWeeksToRun()[0]) {
            text = AppBase.getContext().getString(R.string.every_week);
            anySelected = true;
        } else {
            boolean firstSelect = true;
            for (int i = 1; i < remind.getWeeksToRun().length; i++) {
                if (remind.getWeeksToRun()[i]) {
                    if(firstSelect) {
                        text += ReminderItemData.WeekOptions.values()[i].name;
                        firstSelect = false;
                    } else {
                        text += ", " + ReminderItemData.WeekOptions.values()[i].name;
                    }
                    anySelected = true;
                }
            }
        }
        if(!anySelected) {
            weeks.setText(AppBase.getContext().getString(R.string.none));
        } else {
            weeks.setText(text);
        }
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
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
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
    }

    /**
     * Add repeat category listeners
     */
    private void repeatDaysListener() {
        daysContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.days_of_week);
                String[] week = UtilsTime.getWeekStringArray();
                final boolean[] checked = remind.getDaysToRun().clone();
                Bus.postObject(new MultiChoiceRequest(title, week, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setDaysToRun(checked);
                        updateDaysOfWeek();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //On Negative. Unused
                    }
                }));
            }
        });
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
                    remind.updateDaysToRun()[index] = !dayButtons[index].getActive();
                    setDayOfWeek(index, !dayButtons[index].getActive());
                }
            });
        }
    }

    private void updateDaysOfWeek() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        for (int i = 0; i < dayButtons.length; i++) {
            setDayOfWeek(i, remind.getDaysToRun()[i]);
        }
    }

}
