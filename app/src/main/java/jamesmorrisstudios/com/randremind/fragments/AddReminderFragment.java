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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.materialdesign.views.ButtonCircleFlat;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.utilities.AnimatorControl;
import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * Add and edit reminder fragment.
 * Controls the views and controls needed to modify an already created reminder item.
 * If no reminder has been set this fragment creates a new one and modifies it.
 */
public final class AddReminderFragment extends Fragment {
    public static final String TAG = "AddReminderFragment";
    private static final int NOTIFICATION_RESULT = 5;
    private OnFragmentInteractionListener mListener;
    //Views
    private AppCompatEditText titleText;
    private SwitchCompat titleEnable, notificationEnable, alarmEnable, repeatEnable;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM, notificationSound;
    private View startTimeTop, endTimeTop;
    private AppCompatSpinner timeSpinner, distributionSpinner;
    private ButtonCircleFlat[] dayButtons = new ButtonCircleFlat[7];
    private LinearLayout daysContainer, notificationContainer;
    //Listeners
    private TextWatcher titleTextWatcher;

    /**
     * Required empty public constructor
     */
    public AddReminderFragment() {}

    /**
     * Constructor. Enable menu options
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * @param activity Activity to attach to
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Detach from activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Setup the toolbar options menu
     * @param menu Menu
     * @param inflater Inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle toolbar menu button clicks
     * @param item Selected item
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    /**
     * Create the view and add references to all the items in the xml.
     * Then init with our reminder
     * @param inflater Inflater
     * @param container Container view
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        notificationEnable = (SwitchCompat) view.findViewById(R.id.notification_enabled);
        repeatEnable = (SwitchCompat) view.findViewById(R.id.repeat_enabled);
        daysContainer = (LinearLayout) view.findViewById(R.id.daysContainer);
        notificationContainer = (LinearLayout) view.findViewById(R.id.notificationContainer);
        notificationSound = (TextView) view.findViewById(R.id.notificationSound);
        //alarmEnable = (SwitchCompat) view.findViewById(R.id.alarm_enabled); //TODO
        dayButtons[0] = (ButtonCircleFlat) view.findViewById(R.id.daySun);
        dayButtons[1] = (ButtonCircleFlat) view.findViewById(R.id.dayMon);
        dayButtons[2] = (ButtonCircleFlat) view.findViewById(R.id.dayTue);
        dayButtons[3] = (ButtonCircleFlat) view.findViewById(R.id.dayWed);
        dayButtons[4] = (ButtonCircleFlat) view.findViewById(R.id.dayThu);
        dayButtons[5] = (ButtonCircleFlat) view.findViewById(R.id.dayFri);
        dayButtons[6] = (ButtonCircleFlat) view.findViewById(R.id.daySat);
        initDaysOfWeek();
        //Now setup everything with actual data
        setupViewWithReminder();
        return view;
    }

    /**
     * Init this fragments views with the actual data for the current reminder item.
     * if no reminder item exists it creates one with default values
     */
    private void setupViewWithReminder() {
        //Create a reminder if one isn't already set
        if(!ReminderList.getInstance().hasCurrentReminder()) {
            ReminderList.getInstance().createNewReminder();
        }
        //Populate the views with reminder data
        populateData();
        //Add listeners
        addTitleListeners();
        addTimeSetListeners();
        repeatDaysListener();
        notificationListeners();
    }

    /**
     * Add title category listeners
     */
    private void addTitleListeners() {
        //Enable
        titleEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.enabled = isChecked;
            }
        });
        //Title text change
        titleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.title = s.toString();
            }
        };
        titleText.addTextChangedListener(titleTextWatcher);
    }

    /**
     * Add timing category listeners
     */
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
        startTimeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                mListener.createTimePickerDialog(timeStartListener, currentReminder.startTime.hour,
                        currentReminder.startTime.minute, currentReminder.startTime.is24Hour());
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
        endTimeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                mListener.createTimePickerDialog(timeEndListener, currentReminder.endTime.hour,
                        currentReminder.endTime.minute, currentReminder.endTime.is24Hour());
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
        //Distribution
        distributionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.distribution = ReminderItem.Distribution.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Add repeat category listeners
     */
    private void repeatDaysListener() {
        //Repeat Enable
        repeatEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.repeat = isChecked;
                if (isChecked) {
                    daysContainer.setVisibility(View.VISIBLE);
                } else {
                    daysContainer.setVisibility(View.GONE);
                }
            }
        });
        //Day of week selector
        for(int i=0; i<dayButtons.length; i++) {
            final int index = i;
            dayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View v) {
                    ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                    if(remind == null) {
                        return;
                    }
                    remind.daysToRun[index] = !dayButtons[index].isActivated();
                    setDayOfWeek(index, !dayButtons[index].isActivated());
                }
            });
        }
    }

    /**
     * Add notification category listeners
     */
    private void notificationListeners() {
        notificationEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notification = isChecked;
                if (isChecked) {
                    notificationContainer.setVisibility(View.VISIBLE);
                } else {
                    notificationContainer.setVisibility(View.GONE);
                }
            }
        });
        notificationSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification");
                //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, defaultUri);
                startActivityForResult(intent, NOTIFICATION_RESULT);
            }
        });
    }

    /**
     * remove all listeners that may fire after we leave
     */
    private void destroyListeners() {
        titleEnable.setOnCheckedChangeListener(null);
        titleText.removeTextChangedListener(titleTextWatcher);
        timeSpinner.setOnItemSelectedListener(null);
    }

    /**
     * Loads the reminder data into the views
     */
    private void populateData() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        titleText.setText(remind.title);
        titleEnable.setChecked(remind.enabled);
        Utils.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
        Utils.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
        generateNumberTimePerDay();
        setupDistributionSpinner();
        repeatEnable.setChecked(remind.repeat);
        setDaysOfWeek();
        notificationEnable.setChecked(remind.notification);
        setNotification();
    }

    /**
     * Set the notification category data
     */
    private void setNotification() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        if (remind.notification) {
            showView(notificationContainer);
            //notificationContainer.setVisibility(View.VISIBLE);
        } else {
            hideView(notificationContainer);
            //notificationContainer.setVisibility(View.GONE);
        }
        notificationSound.setText(remind.notificationToneName);
    }

    /**
     * Set the days of week category data
     */
    private void setDaysOfWeek() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        if(remind.repeat) {
            showView(daysContainer);
            //daysContainer.setVisibility(View.VISIBLE);
        } else {
            hideView(daysContainer);
            //daysContainer.setVisibility(View.GONE);
        }
        for(int i=0; i<remind.daysToRun.length; i++) {
            setDayOfWeek(i, remind.daysToRun[i]);
        }
    }

    /**
     * Set the distribution spinner
     */
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
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        distributionSpinner.setAdapter(spinnerArrayAdapter);
        distributionSpinner.setSelection(remind.distribution.ordinal());
    }

    /**
     * Generates how many per day are allowed given the start and end times
     */
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
        timeSpinner.setSelection(remind.numberPerDay - 1);
    }

    /**
     * Save the reminder item and prepare to leave the fragment
     */
    public final void onBack() {
        destroyListeners();
        ReminderList.getInstance().saveCurrentReminder();
        ReminderList.getInstance().clearCurrentReminder();
    }

    /**
     * Activity callback result for popup actions.
     * TODO eventually replace all called activities with native designs
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @NonNull final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
            if(remind == null) {
                return;
            }
            if (uri != null) {
                remind.notificationTone = uri;
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
                remind.notificationToneName = ringtone.getTitle(getActivity());
                notificationSound.setText(remind.notificationToneName);
            }
        }
    }

    /**
     * Configure the day of week views with proper text
     */
    private void initDaysOfWeek() {
        dayButtons[0].getTextView().setText("S");
        dayButtons[1].getTextView().setText("M");
        dayButtons[2].getTextView().setText("T");
        dayButtons[3].getTextView().setText("W");
        dayButtons[4].getTextView().setText("T");
        dayButtons[5].getTextView().setText("F");
        dayButtons[6].getTextView().setText("S");
    }

    /**
     * Set the active state of the day of week item
     * @param dayIndex Index for the day
     * @param active True to enable
     */
    private void setDayOfWeek(int dayIndex, boolean active) {
        final ButtonCircleFlat dayButton = dayButtons[dayIndex];
        dayButton.setActivated(active);
        if(active) {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.primaryColorDark));
        } else {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.white80));
        }
    }

    private void hideView(final View view) {
        ObjectAnimator anim = AnimatorControl.alpha(view, 1.0f, 0.0f, 500, 0);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void showView(final View view) {
        ObjectAnimator anim = AnimatorControl.alpha(view, 0.0f, 1.0f, 500, 0);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

        /**
         * Go back from this reminder
         */
        void goBackFromNewReminder();

        /**
         * Hides the keyboard
         */
        void hideKeyboard();

        /**
         * Build a new time picker dialog
         * @param listener Return listener
         * @param hour Start hour
         * @param minute Start minute
         * @param is24Hour True if 24 hour mode
         */
        void createTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int hour, int minute, boolean is24Hour);
    }

}
