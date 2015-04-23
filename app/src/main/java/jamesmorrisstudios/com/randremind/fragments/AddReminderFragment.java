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

package jamesmorrisstudios.com.randremind.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jamesmorrisstudios.materialdesign.views.Slider;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public final class AddReminderFragment extends Fragment {
    public static final String TAG = "AddReminderFragment";
    private OnFragmentInteractionListener mListener;

    private AppCompatEditText titleText;
    private SwitchCompat titleEnable;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private View startTimeTop, endTimeTop;
    private AppCompatSpinner timeSpinner, distributionSpinner;

    public AddReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                ReminderList.getInstance().deleteCurrentReminder();
                mListener.goBackFromNewReminder();
                break;
            case R.id.action_cancel:
                ReminderList.getInstance().clearCurrentReminder();
                mListener.goBackFromNewReminder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);
        //Get all the views
        titleText = (AppCompatEditText) view.findViewById(R.id.titleText);
        titleEnable = (SwitchCompat) view.findViewById(R.id.titleEnabled);
        startTimeTop = view.findViewById(R.id.timing_start);
        startHour = (TextView) startTimeTop.findViewById(R.id.time_hour);
        startMinute = (TextView) startTimeTop.findViewById(R.id.time_minute);
        startAM = (TextView) startTimeTop.findViewById(R.id.time_am);
        startPM = (TextView) startTimeTop.findViewById(R.id.time_pm);
        endTimeTop = view.findViewById(R.id.timing_end);
        endHour = (TextView) endTimeTop.findViewById(R.id.time_hour);
        endMinute = (TextView) endTimeTop.findViewById(R.id.time_minute);
        endAM = (TextView) endTimeTop.findViewById(R.id.time_am);
        endPM = (TextView) endTimeTop.findViewById(R.id.time_pm);
        timeSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_times_spinner);
        distributionSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_distribution_spinner);
        //Create a reminder if one isn't already set
        if(!ReminderList.getInstance().hasCurrentReminder()) {
            ReminderList.getInstance().createNewReminder();
        }
        //Populate the views with reminder data
        populateData();
        //Add button listeners
        addListeners();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void addListeners() {
        titleEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.enabled = isChecked;
            }
        });
        titleText.addTextChangedListener(titleTextListener);
        startTimeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                mListener.createTimePickerDialog(timeStartListener, currentReminder.startTime.hour,
                        currentReminder.startTime.minute, currentReminder.startTime.is24Hour());
            }
        });
        endTimeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                mListener.createTimePickerDialog(timeEndListener, currentReminder.endTime.hour,
                        currentReminder.endTime.minute, currentReminder.endTime.is24Hour());
            }
        });
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
    }

    private TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
            if (remind == null) {
                return;
            }
            int diffMinutes = (remind.endTime.hour * 60 + remind.endTime.minute) - (hourOfDay * 60 + minute);
            if(diffMinutes >= 0) {
                remind.startTime.hour = hourOfDay;
                remind.startTime.minute = minute;
                Utils.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
                generateNumberTimePerDay();
            } else {
                Utils.toastShort(getResources().getString(R.string.error_time_difference_start));
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timeEndListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
            if (remind == null) {
                return;
            }
            int diffMinutes = (hourOfDay * 60 + minute) - (remind.startTime.hour * 60 + remind.startTime.minute);
            if(diffMinutes >= 0) {
                remind.endTime.hour = hourOfDay;
                remind.endTime.minute = minute;
                Utils.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
                generateNumberTimePerDay();
            } else {
                Utils.toastShort(getResources().getString(R.string.error_time_difference_end));
            }
        }
    };

    private TextWatcher titleTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
            if(currentReminder == null) {
                return;
            }
            currentReminder.title = s.toString();
        }
    };

    private void destroyListeners() {
        titleEnable.setOnCheckedChangeListener(null);
        titleText.removeTextChangedListener(titleTextListener);
        timeSpinner.setOnItemSelectedListener(null);
    }

    private void populateData() {
        ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
        if(currentReminder == null) {
            return;
        }
        titleText.setText(currentReminder.title);
        titleEnable.setChecked(currentReminder.enabled);
        Utils.setTime(startHour, startMinute, startAM, startPM, currentReminder.startTime);
        Utils.setTime(endHour, endMinute, endAM, endPM, currentReminder.endTime);
        generateNumberTimePerDay();
        setupDistributionSpinner();
    }

    private void setupDistributionSpinner() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.distribution_even));
        list.add(getString(R.string.distribution_part_random));
        list.add(getString(R.string.distribution_most_random));
        list.add(getString(R.string.distribution_full_random));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        distributionSpinner.setAdapter(spinnerArrayAdapter);
        distributionSpinner.setSelection(remind.distribution.ordinal());
    }

    private void generateNumberTimePerDay() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        int diffMinutes = (remind.endTime.hour * 60 + remind.endTime.minute) - (remind.startTime.hour * 60 + remind.startTime.minute);
        int max = Math.max(diffMinutes / 30, 1);
        remind.numberPerDay = Math.min(remind.numberPerDay, max);
        List<String> perDayList = new ArrayList<>();
        for(int i=0; i<max; i++) {
            perDayList.add(Integer.toString(i+1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        timeSpinner.setAdapter(spinnerArrayAdapter);
        timeSpinner.setSelection(remind.numberPerDay-1);
    }

    public final void onBack() {
        destroyListeners();
        ReminderList.getInstance().saveCurrentReminder();
        ReminderList.getInstance().clearCurrentReminder();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void goBackFromNewReminder();
        void hideKeyboard();
        void createTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int hour, int minute, boolean is24Hour);
    }

}
