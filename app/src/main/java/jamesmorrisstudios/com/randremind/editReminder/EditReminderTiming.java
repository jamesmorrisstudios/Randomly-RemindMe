package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.EditTimesRequest;
import jamesmorrisstudios.com.randremind.fragments.EditTimesDialog;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderTiming {
    private View parent;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private TextView timing;
    private View timingContainer, timesContainer, startTimeContainer, endTimeContainer, numTimesContainer;
    private View specificGroupContainer, rangeGroupContainer;
    private AppCompatSpinner timeSpinner;

    public EditReminderTiming(View parent) {
        this.parent = parent;

        timingContainer = parent.findViewById(R.id.timingContainer);
        specificGroupContainer = parent.findViewById(R.id.specificGroupContainer);
        rangeGroupContainer = parent.findViewById(R.id.rangeGroupContainer);

        timing = (TextView) parent.findViewById(R.id.timing);

        timesContainer = parent.findViewById(R.id.timesContainer);
        numTimesContainer = parent.findViewById(R.id.numTimesContainer);

        timeSpinner = (AppCompatSpinner) parent.findViewById(R.id.numTimesSpinner);


        startTimeContainer = parent.findViewById(R.id.startTimeContainer);
        startHour = (TextView) startTimeContainer.findViewById(R.id.time_hour);
        startMinute = (TextView) startTimeContainer.findViewById(R.id.time_minute);
        startAM = (TextView) startTimeContainer.findViewById(R.id.time_am);
        startPM = (TextView) startTimeContainer.findViewById(R.id.time_pm);

        endTimeContainer = parent.findViewById(R.id.endTimeContainer);
        endHour = (TextView) endTimeContainer.findViewById(R.id.time_hour);
        endMinute = (TextView) endTimeContainer.findViewById(R.id.time_minute);
        endAM = (TextView) endTimeContainer.findViewById(R.id.time_am);
        endPM = (TextView) endTimeContainer.findViewById(R.id.time_pm);

        timeSpinner = (AppCompatSpinner) parent.findViewById(R.id.numTimesSpinner);
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        updateTimingType();
        updateStartTime();
        updateEndTime();
        generateNumberTimePerDay();

        timingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.timing_type);
                String[] items = new String[]{AppBase.getContext().getString(R.string.range), AppBase.getContext().getString(R.string.specific)};
                Bus.postObject(new SingleChoiceRequest(title, items, true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        if (which == 0) {
                            remind.setRangeTiming(true);
                        } else {
                            remind.setRangeTiming(false);
                        }
                        updateTimingType();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //On Negative. Unused
                    }
                }));

            }
        });
        timesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new EditTimesRequest(remind.getSpecificTimeList(), new EditTimesDialog.EditTimesListener() {
                    @Override
                    public void onPositive(ArrayList<TimeItem> times) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setSpecificTimeList(times);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true));
            }
        });
        final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(@NonNull RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                int diffMinutes = (remind.getEndTime().hour * 60 + remind.getEndTime().minute) - (hourOfDay * 60 + minute);
                if (diffMinutes >= 0) {
                    remind.updateStartTime().hour = hourOfDay;
                    remind.updateStartTime().minute = minute;
                    UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.getStartTime());
                    generateNumberTimePerDay();
                } else {
                    Utils.toastShort(AppBase.getContext().getResources().getString(R.string.invalid_time));
                }
            }
        };
        startTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                Bus.postObject(new TimePickerRequest(timeStartListener, currentReminder.getStartTime().hour,
                        currentReminder.getStartTime().minute, currentReminder.getStartTime().is24Hour()));
            }
        });
        final TimePickerDialog.OnTimeSetListener timeEndListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(@NonNull RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                int diffMinutes = (hourOfDay * 60 + minute) - (remind.getStartTime().hour * 60 + remind.getStartTime().minute);
                if (diffMinutes >= 0) {
                    remind.updateEndTime().hour = hourOfDay;
                    remind.updateEndTime().minute = minute;
                    UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.getEndTime());
                    generateNumberTimePerDay();
                } else {
                    Utils.toastShort(AppBase.getContext().getResources().getString(R.string.invalid_time));
                }
            }
        };
        endTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                Bus.postObject(new TimePickerRequest(timeEndListener, currentReminder.getEndTime().hour,
                        currentReminder.getEndTime().minute, currentReminder.getEndTime().is24Hour()));
            }
        });
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.setNumberPerDay(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        numTimesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpinner.performClick();
            }
        });
    }

    private void updateTimingType() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        if(remind.isRangeTiming()) {
            timing.setText(AppBase.getContext().getString(R.string.range));
            specificGroupContainer.setVisibility(View.GONE);
            rangeGroupContainer.setVisibility(View.VISIBLE);
        } else {
            timing.setText(AppBase.getContext().getString(R.string.specific));
            specificGroupContainer.setVisibility(View.VISIBLE);
            rangeGroupContainer.setVisibility(View.GONE);
        }
    }

    private void updateStartTime() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.getStartTime());
    }

    private void updateEndTime() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.getEndTime());
    }

    private void generateNumberTimePerDay() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        int diffMinutes = (remind.getEndTime().hour * 60 + remind.getEndTime().minute) - (remind.getStartTime().hour * 60 + remind.getStartTime().minute);
        int max = Math.max(diffMinutes / 10, 1);
        remind.setNumberPerDay(Math.min(remind.getNumberPerDay(), max));
        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_drop_down_item);
        timeSpinner.setAdapter(spinnerArrayAdapter);
        timeSpinner.setSelection(remind.getNumberPerDay() - 1);
    }


/*

        //Times per day


    }

*/
}
