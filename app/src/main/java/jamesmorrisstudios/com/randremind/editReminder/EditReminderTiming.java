package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import jamesmorrisstudios.com.randremind.fragments.EditTimesDialogBuilder;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderTiming {
    private RelativeLayout parent;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private RadioButton timingSpecific, timingRange;
    private LinearLayout timingTimes, timingTimesPerDay, timingSingleTime;
    private View startTimeTop, endTimeTop;
    private AppCompatSpinner timeSpinner, timeSpecificSpinner;
    private Button editSpecificTimes;

    public EditReminderTiming(RelativeLayout parent) {
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
        final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        timingSpecific.setChecked(!remind.rangeTiming);
        timingRange.setChecked(remind.rangeTiming);

        generateNumberTimePerDay();
        generateNumberSpecificTimePerDay();

        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.endTime);

        setTimingType();
        addTimingTypeListener();
        addTimeSetListeners();

        editSpecificTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bus.postObject(new EditTimesRequest(remind.specificTimeList, new EditTimesDialogBuilder.EditTimesListener() {
                    @Override
                    public void onPositive(ArrayList<TimeItem> times) {
                        remind.specificTimeList = times;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }));
            }
        });
    }

    private void addTimingTypeListener() {
        final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        timingSpecific.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.rangeTiming = !isChecked;
                setTimingType();
            }
        });
    }

    private void setTimingType() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        if (remind.rangeTiming) {
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
                int diffMinutes = (remind.endTime.hour * 60 + remind.endTime.minute) - (hourOfDay * 60 + minute);
                if (diffMinutes >= 0) {
                    remind.startTime.hour = hourOfDay;
                    remind.startTime.minute = minute;
                    UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
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
                Bus.postObject(new TimePickerRequest(timeStartListener, currentReminder.startTime.hour,
                        currentReminder.startTime.minute, currentReminder.startTime.is24Hour()));
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
                int diffMinutes = (hourOfDay * 60 + minute) - (remind.startTime.hour * 60 + remind.startTime.minute);
                if (diffMinutes >= 0) {
                    remind.endTime.hour = hourOfDay;
                    remind.endTime.minute = minute;
                    UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
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
                Bus.postObject(new TimePickerRequest(timeEndListener, currentReminder.endTime.hour,
                        currentReminder.endTime.minute, currentReminder.endTime.is24Hour()));
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
                currentReminder.numberPerDay = position + 1;
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
                while (size > remind.specificTimeList.size()) {
                    remind.specificTimeList.add(new TimeItem(9, 0));
                }
                //If we are making the list smaller
                while (size < remind.specificTimeList.size()) {
                    remind.specificTimeList.remove(remind.specificTimeList.size() - 1);
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
        int diffMinutes = (remind.endTime.hour * 60 + remind.endTime.minute) - (remind.startTime.hour * 60 + remind.startTime.minute);
        int max = Math.max(diffMinutes / 10, 1);
        remind.numberPerDay = Math.min(remind.numberPerDay, max);
        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_drop_down_item);
        timeSpinner.setAdapter(spinnerArrayAdapter);
        timeSpinner.setSelection(remind.numberPerDay - 1);
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
        timeSpecificSpinner.setSelection(remind.specificTimeList.size() - 1);
    }

}
