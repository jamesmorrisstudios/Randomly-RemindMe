package jamesmorrisstudios.com.randremind.editReminder;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.Utils;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;
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
    private RadioButton timingSpecific, timingRange;
    private LinearLayout timingTimes, timingTimesPerDay, timingSingleTime;
    private View startTimeTop, endTimeTop;
    private AppCompatSpinner timeSpinner, timeSpecificSpinner;
    private Button editSpecificTimes;

    public EditReminderTiming(View parent) {
        this.parent = parent;
        startTimeTop = parent.findViewById(R.id.timing_start);
        startHour = (TextView) startTimeTop.findViewById(R.id.time_hour);
        startMinute = (TextView) startTimeTop.findViewById(R.id.time_minute);
        startAM = (TextView) startTimeTop.findViewById(R.id.time_am);
        startPM = (TextView) startTimeTop.findViewById(R.id.time_pm);
        endTimeTop = parent.findViewById(R.id.timing_end);
        endHour = (TextView) endTimeTop.findViewById(R.id.time_hour);
        endMinute = (TextView) endTimeTop.findViewById(R.id.time_minute);
        endAM = (TextView) endTimeTop.findViewById(R.id.time_am);
        endPM = (TextView) endTimeTop.findViewById(R.id.time_pm);

        timeSpinner = (AppCompatSpinner) parent.findViewById(R.id.timing_times_spinner);
        timeSpecificSpinner = (AppCompatSpinner) parent.findViewById(R.id.timing_times_specific_spinner);

        timingSpecific = (RadioButton) parent.findViewById(R.id.radio_specific);
        timingRange = (RadioButton) parent.findViewById(R.id.radio_range);
        timingTimes = (LinearLayout) parent.findViewById(R.id.timing_times);
        timingTimesPerDay = (LinearLayout) parent.findViewById(R.id.timing_times_per_day);
        timingSingleTime = (LinearLayout) parent.findViewById(R.id.timing_times_specific);

        editSpecificTimes = (Button) parent.findViewById(R.id.btn_edit_times);
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        timingSpecific.setChecked(!remind.isRangeTiming());
        timingRange.setChecked(remind.isRangeTiming());

        generateNumberTimePerDay();
        generateNumberSpecificTimePerDay();

        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.getStartTime());
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.getEndTime());

        setTimingType();
        addTimingTypeListener();
        addTimeSetListeners();

        editSpecificTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new EditTimesRequest(remind.getSpecificTimeList(), new EditTimesDialog.EditTimesListener() {
                    @Override
                    public void onPositive(ArrayList<TimeItem> times) {
                        final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
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
    }

    private void addTimingTypeListener() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        timingSpecific.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.setRangeTiming(!isChecked);
                setTimingType();
            }
        });
    }

    private void setTimingType() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        if (remind.isRangeTiming()) {
            //Show all of the range timing views
            timingTimes.setVisibility(View.VISIBLE);
            timingTimesPerDay.setVisibility(View.VISIBLE);
            timingSingleTime.setVisibility(View.GONE);
        } else {
            //Hide all of the range timing
            timingSingleTime.setVisibility(View.VISIBLE);
            timingTimes.setVisibility(View.GONE);
            timingTimesPerDay.setVisibility(View.GONE);
        }
    }

    private void addTimeSetListeners() {
        //Time start
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
                    Utils.toastShort(AppUtil.getContext().getResources().getString(R.string.error_time_difference));
                }
            }
        };
        startTimeTop.setOnClickListener(new View.OnClickListener() {
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
        //Time End
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
                    Utils.toastShort(AppUtil.getContext().getResources().getString(R.string.error_time_difference));
                }
            }
        };
        endTimeTop.setOnClickListener(new View.OnClickListener() {
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
        //Times per day
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
        //Times specific per day
        timeSpecificSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                int size = position + 1;
                //If we are making the list bigger
                while (size > remind.getSpecificTimeList().size()) {
                    remind.updateSpecificTimeList().add(new TimeItem(9, 0));
                }
                //If we are making the list smaller
                while (size < remind.getSpecificTimeList().size()) {
                    remind.updateSpecificTimeList().remove(remind.getSpecificTimeList().size() - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void generateNumberSpecificTimePerDay() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_drop_down_item);
        timeSpecificSpinner.setAdapter(spinnerArrayAdapter);
        timeSpecificSpinner.setSelection(remind.getSpecificTimeList().size() - 1);
    }

}
