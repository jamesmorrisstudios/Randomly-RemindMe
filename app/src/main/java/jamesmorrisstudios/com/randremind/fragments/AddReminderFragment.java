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
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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

    private EditText titleText;
    private SwitchCompat titleEnable;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;
    private View startTimeTop, endTimeTop;

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
        titleText = (EditText) view.findViewById(R.id.titleText);
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
                if(currentReminder == null) {
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
                if(currentReminder == null) {
                    return;
                }
                mListener.createTimePickerDialog(timeEndListener, currentReminder.endTime.hour,
                        currentReminder.endTime.minute, currentReminder.endTime.is24Hour());
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
            if (currentReminder == null) {
                return;
            }
            currentReminder.startTime.hour = hourOfDay;
            currentReminder.startTime.minute = minute;
            Utils.setTime(startHour, startMinute, startAM, startPM, currentReminder.startTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeEndListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
            if (currentReminder == null) {
                return;
            }
            currentReminder.endTime.hour = hourOfDay;
            currentReminder.endTime.minute = minute;
            Utils.setTime(endHour, endMinute, endAM, endPM, currentReminder.endTime);
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
