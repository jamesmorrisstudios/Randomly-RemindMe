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
import android.support.v7.widget.AppCompatCheckBox;
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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.jamesmorrisstudios.materialuilibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.materialuilibrary.dialogs.ColorSelector;
import com.jamesmorrisstudios.materialuilibrary.dialogs.MaterialDialog;
import com.jamesmorrisstudios.materialuilibrary.dialogs.time.RadialPickerLayout;
import com.jamesmorrisstudios.materialuilibrary.dialogs.time.TimePickerDialog;
import com.jamesmorrisstudios.utilitieslibrary.Utils;
import com.jamesmorrisstudios.utilitieslibrary.animator.AnimatorControl;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Add and edit reminder fragment.
 * Controls the views and controls needed to modify an already created reminder reminder.
 */
public final class AddReminderFragment extends BaseFragment {
    public static final String TAG = "AddReminderFragment";
    private static final int NOTIFICATION_RESULT = 5;
    //Views
    private AppCompatEditText titleText, contentText;
    private SwitchCompat titleEnable;
    private AppCompatCheckBox notificationVibrateEnable, ledEnable, highPriorityEnable;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM, notificationSound,
            singleHour, singleMinute, singleAM, singlePM;
    private View startTimeTop, endTimeTop, singleTimeTop, ledColor;
    private AppCompatSpinner timeSpinner, distributionSpinner;
    private ButtonCircleFlat[] dayButtons = new ButtonCircleFlat[7];
    private LinearLayout daysContainer, notificationContainer;
    private ScrollView scrollPane;
    private int scrollPosition = 0;
    private ButtonCircleFlat timingSpecific, timingRange;
    private LinearLayout timingTimes, timingTimesPerDay, timingDistribution, timingSingleTime;
    //Listeners
    private TextWatcher titleTextWatcher, contentTextWatcher;
    private RelativeLayout timingParent;

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
     * @param item Selected reminder
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                dialogListener.createPromptDialog(getString(R.string.cancel_prompt_title), getString(R.string.cancel_prompt_content), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        destroyListeners();
                        ReminderList.getInstance().clearCurrentReminder();
                        utilListener.goBackFromFragment();
                        Utils.toastShort(getString(R.string.reminder_cancel));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                });
                break;
            case R.id.action_preview:
                ReminderList.getInstance().previewCurrent();
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
        contentText = (AppCompatEditText) view.findViewById(R.id.contentText);
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
        singleTimeTop = view.findViewById(R.id.timing_single);
        singleHour = (TextView) singleTimeTop.findViewById(R.id.time_hour);
        singleMinute = (TextView) singleTimeTop.findViewById(R.id.time_minute);
        singleAM = (TextView) singleTimeTop.findViewById(R.id.time_am);
        singlePM = (TextView) singleTimeTop.findViewById(R.id.time_pm);
        timeSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_times_spinner);
        distributionSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_distribution_spinner);
        daysContainer = (LinearLayout) view.findViewById(R.id.daysContainer);
        notificationContainer = (LinearLayout) view.findViewById(R.id.notificationContainer);
        notificationSound = (TextView) view.findViewById(R.id.notificationSound);
        notificationVibrateEnable = (AppCompatCheckBox) view.findViewById(R.id.notification_vibrate_enabled);
        ledEnable = (AppCompatCheckBox) view.findViewById(R.id.notification_led_enabled);
        ledColor = view.findViewById(R.id.notification_led_color);
        highPriorityEnable = (AppCompatCheckBox) view.findViewById(R.id.notification_high_priority_enabled);
        scrollPane = (ScrollView) view.findViewById(R.id.scrollPane);
        scrollPosition = scrollPane.getScrollY();
        dayButtons[0] = (ButtonCircleFlat) view.findViewById(R.id.daySun);
        dayButtons[1] = (ButtonCircleFlat) view.findViewById(R.id.dayMon);
        dayButtons[2] = (ButtonCircleFlat) view.findViewById(R.id.dayTue);
        dayButtons[3] = (ButtonCircleFlat) view.findViewById(R.id.dayWed);
        dayButtons[4] = (ButtonCircleFlat) view.findViewById(R.id.dayThu);
        dayButtons[5] = (ButtonCircleFlat) view.findViewById(R.id.dayFri);
        dayButtons[6] = (ButtonCircleFlat) view.findViewById(R.id.daySat);
        initDaysOfWeek();
        timingSpecific = (ButtonCircleFlat) view.findViewById(R.id.timing_specific);
        timingRange = (ButtonCircleFlat) view.findViewById(R.id.timing_range);
        timingTimes = (LinearLayout) view.findViewById(R.id.timing_times);
        timingTimesPerDay = (LinearLayout) view.findViewById(R.id.timing_times_per_day);
        timingDistribution = (LinearLayout) view.findViewById(R.id.timing_distribution);
        timingSingleTime = (LinearLayout) view.findViewById(R.id.timing_times_specific);
        timingParent = (RelativeLayout) view.findViewById(R.id.timingParent);
        //Now setup everything with actual data
        setupViewWithReminder();
        return view;
    }

    @Override
    protected void afterViewCreated() {
        setFabEnable(true);
        setFabIcon(R.drawable.ic_save_white_24dp);
    }

    /**
     * Init this fragments views with the actual data for the current reminder reminder.
     * if no reminder reminder exists it creates one with default values
     */
    private void setupViewWithReminder() {
        //Populate the views with reminder data
        populateData();
        //Add listeners
        addTimingTypeListener();
        addTitleListeners();
        addContentListeners();
        addTimeSetListeners();
        repeatDaysListener();
        notificationListeners();
        scrollPaneListener();
    }

    private void addTimingTypeListener() {
        timingSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                timingSpecific.setActivated(true);
                timingRange.setActivated(false);
                remind.rangeTiming = false;
                setTimingType();
            }
        });
        timingRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                timingSpecific.setActivated(false);
                timingRange.setActivated(true);
                remind.rangeTiming = true;
                setTimingType();
            }
        });
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        timingSpecific.setActivated(!remind.rangeTiming);
        timingRange.setActivated(remind.rangeTiming);
        setTimingType();
    }

    private void setTimingType() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        if(remind.rangeTiming) {
            //Show all of the range timing views
            timingTimes.setVisibility(View.VISIBLE);
            timingTimesPerDay.setVisibility(View.VISIBLE);
            timingDistribution.setVisibility(View.VISIBLE);
            timingSingleTime.setVisibility(View.GONE);
        } else {
            //Hide all of the range timing
            timingSingleTime.setVisibility(View.VISIBLE);
            timingTimes.setVisibility(View.GONE);
            timingTimesPerDay.setVisibility(View.GONE);
            timingDistribution.setVisibility(View.GONE);
        }
        if(remind.rangeTiming) {
            timingSpecific.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
            timingRange.getTextView().setTextColor(getResources().getColor(R.color.primaryDark));
        } else {
            timingSpecific.getTextView().setTextColor(getResources().getColor(R.color.primaryDark));
            timingRange.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
        }

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

    private void addContentListeners() {
        //Content text change
        contentTextWatcher = new TextWatcher() {
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
                currentReminder.content = s.toString();
            }
        };
        contentText.addTextChangedListener(contentTextWatcher);
    }

    /**
     * Add timing category listeners
     */
    private void addTimeSetListeners() {
        //Time single
        final TimePickerDialog.OnTimeSetListener timeSingleListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(@NonNull RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.singleTime.hour = hourOfDay;
                remind.singleTime.minute = minute;
                UtilsTime.setTime(singleHour, singleMinute, singleAM, singlePM, remind.singleTime);
            }
        };
        singleTimeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                dialogListener.createTimePickerDialog(timeSingleListener, currentReminder.singleTime.hour,
                        currentReminder.singleTime.minute, currentReminder.singleTime.is24Hour());
            }
        });
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
                    UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
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
                dialogListener.createTimePickerDialog(timeStartListener, currentReminder.startTime.hour,
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
                    UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
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
                dialogListener.createTimePickerDialog(timeEndListener, currentReminder.endTime.hour,
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
        notificationSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if(remind.notificationTone != null) {
                    defaultUri = Uri.parse(remind.notificationTone);
                }
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, defaultUri);
                startActivityForResult(intent, NOTIFICATION_RESULT);
            }
        });
        notificationVibrateEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationVibrate = isChecked;
            }
        });
        highPriorityEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationHighPriority = isChecked;
            }
        });
        ledEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationLED = isChecked;
            }
        });
        ledColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                dialogListener.createColorPickerDialog(remind.notificationLEDColor, new ColorSelector.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationLEDColor = color;
                        ledColor.setBackgroundColor(remind.notificationLEDColor);
                    }
                });
            }
        });
    }

    private void scrollPaneListener() {
        scrollPane.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollPane.getScrollY();
                if (Math.abs(scrollY - scrollPosition) > 75) {
                    if (scrollY < scrollPosition) {
                        showFab();
                    } else {
                        hideFab();
                    }
                    scrollPosition = scrollY;
                }
            }
        });
    }

    @Override
    protected void fabClicked() {
        utilListener.goBackFromFragment();
    }

    /**
     * remove all listeners that may fire after we leave
     */
    private void destroyListeners() {
        titleEnable.setOnCheckedChangeListener(null);
        titleText.removeTextChangedListener(titleTextWatcher);
        contentText.removeTextChangedListener(contentTextWatcher);
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
        contentText.setText(remind.content);
        titleEnable.setChecked(remind.enabled);
        timingSpecific.setText(getString(R.string.timing_specific));
        timingRange.setText(getString(R.string.timing_range));
        ledColor.setBackgroundColor(remind.notificationLEDColor);
        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
        UtilsTime.setTime(singleHour, singleMinute, singleAM, singlePM, remind.singleTime);
        generateNumberTimePerDay();
        setupDistributionSpinner();
        setDaysOfWeek();
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
        //if (remind.notification) {
            showView(notificationContainer);
        //} else {
        //    hideView(notificationContainer);
       // }
        notificationSound.setText(remind.notificationToneName);
        notificationVibrateEnable.setChecked(remind.notificationVibrate);
        highPriorityEnable.setChecked(remind.notificationHighPriority);
        ledEnable.setChecked(remind.notificationLED);
    }

    /**
     * Set the days of week category data
     */
    private void setDaysOfWeek() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        //if(remind.repeat) {
            showView(daysContainer);
        //} else {
        //    hideView(daysContainer);
        //}
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
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        distributionSpinner.setAdapter(spinnerArrayAdapter);
        distributionSpinner.setSelection(Math.min(remind.distribution.ordinal(), 2)); //Restrict to the now less options
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
        //int max = Math.max(diffMinutes / 10, 1);
        int max = diffMinutes; //TODO
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
     * Save the reminder reminder and prepare to leave the fragment
     */
    @Override
    public final void onBack() {
        if(ReminderList.getInstance().hasCurrentReminder()) {
            Utils.toastShort(getString(R.string.reminder_save));
            destroyListeners();
            ReminderList.getInstance().saveCurrentReminder();
            ReminderList.getInstance().saveData();
        }
        utilListener.hideKeyboard();
    }

    /**
     * Activity callback result for popup actions.
     * TODO eventually replace all called activities with native designs
     * @param requestCode Request code
     * @param resultCode Result code status
     * @param intent Result intent
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
                remind.notificationTone = uri.toString();
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
                remind.notificationToneName = ringtone.getTitle(getActivity());
                notificationSound.setText(remind.notificationToneName);
            } else {
                remind.notificationTone = null;
                remind.notificationToneName = AppUtil.getContext().getString(R.string.sound_none);
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
     * Set the active state of the day of week reminder
     * @param dayIndex Index for the day
     * @param active True to enable
     */
    private void setDayOfWeek(int dayIndex, boolean active) {
        final ButtonCircleFlat dayButton = dayButtons[dayIndex];
        dayButton.setActivated(active);
        if(active) {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.primary));
        } else {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
        }
    }

    /**
     * Animates a view to hidden state
     * @param view The view to hide
     */
    private void hideView(final View view) {
        ObjectAnimator anim = AnimatorControl.alpha(view, 1.0f, 0.0f, 250, 0);
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

    /**
     * Animates a view to shown state
     * @param view The view to show
     */
    private void showView(final View view) {
        ObjectAnimator anim = AnimatorControl.alpha(view, 0.0f, 1.0f, 250, 0);
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

}
