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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.jamesmorrisstudios.utilitieslibrary.Utils;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.controls.ButtonCircleFlat;
import com.jamesmorrisstudios.utilitieslibrary.dialogs.colorpicker.builder.ColorPickerClickListener;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
    private OnAddReminderFragmentListener reminderFragmentListener;
    //Views
    private AppCompatEditText titleText, contentText;
    private AppCompatCheckBox notificationVibrateEnable, ledEnable, highPriorityEnable, randomEnable;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM, notificationSound;
    private TextView[] specificHour = new TextView[20], specificMinute = new TextView[20], specificAM = new TextView[20], specificPM = new TextView[20];
    private View[] specificTimeTop = new View[20];
    private View startTimeTop, endTimeTop, ledColor, accentColor;
    private AppCompatSpinner timeSpinner, timeSpecificSpinner;
    private ButtonCircleFlat[] dayButtons = new ButtonCircleFlat[7];
    private LinearLayout daysContainer, notificationContainer;
    private ScrollView scrollPane;
    private int scrollPosition = 0;
    private ButtonCircleFlat timingSpecific, timingRange;
    private LinearLayout timingTimes, timingTimesPerDay, timingDistribution, timingSingleTime;
    private RelativeLayout notificationIconContainer;
    private ImageView notificationIcon;
    //Listeners
    private TextWatcher titleTextWatcher, contentTextWatcher;

    private boolean saveOnBack = true;

    /**
     * Required empty public constructor
     */
    public AddReminderFragment() {}

    /**
     * Constructor. Enable menu options
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Setup the toolbar options menu
     *
     * @param menu     Menu
     * @param inflater Inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle toolbar menu button clicks
     *
     * @param item Selected reminder
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                dialogListener.createPromptDialog(getString(R.string.cancel_prompt_title), getString(R.string.cancel_prompt_content), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destroyListeners();
                        ReminderList.getInstance().cancelCurrentReminderChanges();
                        saveOnBack = false;
                        utilListener.goBackFromFragment();
                        Utils.toastShort(getString(R.string.reminder_cancel));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing on negative
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
     *
     * @param inflater           Inflater
     * @param container          Container view
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);
        //Get all the views
        titleText = (AppCompatEditText) view.findViewById(R.id.titleText);
        contentText = (AppCompatEditText) view.findViewById(R.id.contentText);
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
        specificTimeTop[0] = view.findViewById(R.id.timing_single_00);
        specificTimeTop[1] = view.findViewById(R.id.timing_single_01);
        specificTimeTop[2] = view.findViewById(R.id.timing_single_02);
        specificTimeTop[3] = view.findViewById(R.id.timing_single_03);
        specificTimeTop[4] = view.findViewById(R.id.timing_single_04);
        specificTimeTop[5] = view.findViewById(R.id.timing_single_05);
        specificTimeTop[6] = view.findViewById(R.id.timing_single_06);
        specificTimeTop[7] = view.findViewById(R.id.timing_single_07);
        specificTimeTop[8] = view.findViewById(R.id.timing_single_08);
        specificTimeTop[9] = view.findViewById(R.id.timing_single_09);
        specificTimeTop[10] = view.findViewById(R.id.timing_single_10);
        specificTimeTop[11] = view.findViewById(R.id.timing_single_11);
        specificTimeTop[12] = view.findViewById(R.id.timing_single_12);
        specificTimeTop[13] = view.findViewById(R.id.timing_single_13);
        specificTimeTop[14] = view.findViewById(R.id.timing_single_14);
        specificTimeTop[15] = view.findViewById(R.id.timing_single_15);
        specificTimeTop[16] = view.findViewById(R.id.timing_single_16);
        specificTimeTop[17] = view.findViewById(R.id.timing_single_17);
        specificTimeTop[18] = view.findViewById(R.id.timing_single_18);
        specificTimeTop[19] = view.findViewById(R.id.timing_single_19);
        for(int i=0; i<specificTimeTop.length; i++) {
            specificHour[i] = (TextView) specificTimeTop[i].findViewById(R.id.time_hour);
            specificMinute[i] = (TextView) specificTimeTop[i].findViewById(R.id.time_minute);
            specificAM[i] = (TextView) specificTimeTop[i].findViewById(R.id.time_am);
            specificPM[i] = (TextView) specificTimeTop[i].findViewById(R.id.time_pm);
        }
        timeSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_times_spinner);
        timeSpecificSpinner = (AppCompatSpinner) view.findViewById(R.id.timing_times_specific_spinner);
        randomEnable = (AppCompatCheckBox) view.findViewById(R.id.timing_random_enabled);
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
        accentColor = view.findViewById(R.id.notification_accent_color);
        notificationIconContainer = (RelativeLayout) view.findViewById(R.id.notificationIconTop);
        notificationIcon = (ImageView) view.findViewById(R.id.notificationIcon);
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
     * @param activity Activity to attach to
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            reminderFragmentListener = (OnAddReminderFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddReminderFragmentListener");
        }
    }

    /**
     * Detach from activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        reminderFragmentListener = null;
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
        if (remind.rangeTiming) {
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
        if (remind.rangeTiming) {
            timingSpecific.getTextView().setTextColor(getResources().getColor(R.color.textDarkMain));
            timingRange.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
        } else {
            timingSpecific.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
            timingRange.getTextView().setTextColor(getResources().getColor(R.color.textDarkMain));
        }

    }

    /**
     * Add title category listeners
     */
    private void addTitleListeners() {
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
        for(int i=0; i<specificTimeTop.length; i++) {
            final int index = i;
            final TimePickerDialog.OnTimeSetListener timeSingleListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(@NonNull RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                    ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                    if (remind == null) {
                        return;
                    }
                    remind.specificTimeList.get(index).hour = hourOfDay;
                    remind.specificTimeList.get(index).minute = minute;
                    UtilsTime.setTime(specificHour[index], specificMinute[index], specificAM[index], specificPM[index], remind.specificTimeList.get(index));
                }
            };
            specificTimeTop[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View v) {
                    ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                    if (currentReminder == null) {
                        return;
                    }
                    dialogListener.createTimePickerDialog(timeSingleListener, currentReminder.specificTimeList.get(index).hour,
                            currentReminder.specificTimeList.get(index).minute, currentReminder.specificTimeList.get(index).is24Hour());
                }
            });
        }
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
                if (diffMinutes >= 0) {
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
                while(size > remind.specificTimeList.size()) {
                    remind.specificTimeList.add(new TimeItem(9, 0));
                }
                //If we are making the list smaller
                while(size < remind.specificTimeList.size()) {
                    remind.specificTimeList.remove(remind.specificTimeList.size() -1);
                }
                updateSpecifcTimes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Distribution
        randomEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem currentReminder = ReminderList.getInstance().getCurrentReminder();
                if (currentReminder == null) {
                    return;
                }
                currentReminder.randomDistribution = isChecked;
            }
        });
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
                if (remind.notificationTone != null) {
                    defaultUri = Uri.parse(remind.notificationTone);
                }
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, defaultUri);
                try {
                    startActivityForResult(intent, NOTIFICATION_RESULT);
                }catch (Exception ex) {
                    Utils.toastShort(getString(R.string.help_link_error));
                }
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
                dialogListener.createColorPickerDialog(remind.notificationLEDColor, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationLEDColor = color;
                        ((GradientDrawable) ledColor.getBackground()).setColor(remind.notificationLEDColor);
                    }
                });
            }
        });
        accentColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                dialogListener.createColorPickerDialog(remind.notificationAccentColor, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationAccentColor = color;
                        ((GradientDrawable)accentColor.getBackground()).setColor(remind.notificationAccentColor);
                        ((GradientDrawable)notificationIconContainer.getBackground()).setColor(remind.notificationAccentColor);
                    }
                });
            }
        });
        notificationIconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                reminderFragmentListener.showIconPickerDialog(new IconPickerDialogBuilder.IconPickerListener() {
                    @Override
                    public void onClick(@DrawableRes int iconRes) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationIconRes = iconRes;
                        notificationIcon.setImageResource(iconRes);
                    }
                }, remind.notificationAccentColor);
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
        titleText.removeTextChangedListener(titleTextWatcher);
        contentText.removeTextChangedListener(contentTextWatcher);
        timeSpinner.setOnItemSelectedListener(null);
        timeSpecificSpinner.setOnItemSelectedListener(null);
    }

    /**
     * Loads the reminder data into the views
     */
    private void populateData() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        titleText.setText(remind.title);
        contentText.setText(remind.content);
        timingSpecific.setText(getString(R.string.timing_specific));
        timingRange.setText(getString(R.string.timing_range));
        ((GradientDrawable)ledColor.getBackground()).setColor(remind.notificationLEDColor);
        ((GradientDrawable)accentColor.getBackground()).setColor(remind.notificationAccentColor);
        ((GradientDrawable)notificationIconContainer.getBackground()).setColor(remind.notificationAccentColor);
        notificationIcon.setImageResource(remind.notificationIconRes);
        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.startTime);
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.endTime);
        updateSpecifcTimes();
        generateNumberTimePerDay();
        generateNumberSpecificTimePerDay();
        randomEnable.setChecked(remind.randomDistribution);
        setDaysOfWeek();
        setNotification();
    }

    private void updateSpecifcTimes() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        for(int i=0; i<specificTimeTop.length; i++) {
            if(i < remind.specificTimeList.size()) {
                specificTimeTop[i].setVisibility(View.VISIBLE);
                UtilsTime.setTime(specificHour[i], specificMinute[i], specificAM[i], specificPM[i], remind.specificTimeList.get(i));
            } else {
                specificTimeTop[i].setVisibility(View.GONE);
            }
        }
    }

    /**
     * Set the notification category data
     */
    private void setNotification() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        notificationContainer.setVisibility(View.VISIBLE);
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
        if (remind == null) {
            return;
        }
        daysContainer.setVisibility(View.VISIBLE);
        for (int i = 0; i < remind.daysToRun.length; i++) {
            setDayOfWeek(i, remind.daysToRun[i]);
        }
    }

    /**
     * Generates how many per day are allowed given the start and end times
     */
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
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        timeSpinner.setAdapter(spinnerArrayAdapter);
        timeSpinner.setSelection(remind.numberPerDay - 1);
    }

    private void generateNumberSpecificTimePerDay() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        timeSpecificSpinner.setAdapter(spinnerArrayAdapter);
        timeSpecificSpinner.setSelection(remind.specificTimeList.size() - 1);
    }

    /**
     * On Stop
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.v("Add Reminder Fragment", "On Stop");
        //If any reminders are currently open save them
        ReminderList.getInstance().saveCurrentReminder();
    }

    /**
     * Save the reminder reminder and prepare to leave the fragment
     */
    @Override
    public final void onBack() {
        if (saveOnBack && ReminderList.getInstance().hasCurrentReminder()) {
            Utils.toastShort(getString(R.string.reminder_save));
            destroyListeners();
            ReminderList.getInstance().saveCurrentReminder();
        }
        utilListener.hideKeyboard();
    }

    /**
     * Activity callback result for popup actions.
     * TODO eventually replace all called activities with native designs
     *
     * @param requestCode Request code
     * @param resultCode  Result code status
     * @param intent      Result intent
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @NonNull final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
            if (remind == null) {
                return;
            }
            if (uri != null) {
                remind.notificationTone = uri.toString();
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
                if(ringtone != null) {
                    remind.notificationToneName = ringtone.getTitle(getActivity());
                    notificationSound.setText(remind.notificationToneName);
                    return;
                }
            }
            remind.notificationTone = null;
            remind.notificationToneName = AppUtil.getContext().getString(R.string.sound_none);
            notificationSound.setText(remind.notificationToneName);
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
     *
     * @param dayIndex Index for the day
     * @param active   True to enable
     */
    private void setDayOfWeek(int dayIndex, boolean active) {
        final ButtonCircleFlat dayButton = dayButtons[dayIndex];
        dayButton.setActivated(active);
        if (active) {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.textLightMain));
        } else {
            dayButton.getTextView().setTextColor(getResources().getColor(R.color.textDarkMain));
        }
    }

    /**
     *
     */
    public interface OnAddReminderFragmentListener {

        /**
         *
         */
        void showIconPickerDialog(IconPickerDialogBuilder.IconPickerListener iconPickerListener, int accentColor);

    }

}
