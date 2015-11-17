package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.IconItem;
import com.jamesmorrisstudios.appbaselibrary.ThemeManager;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.DualSpinnerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.MultiChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.MultiDatePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleDatePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.DatePickerMultiDialogBuilder;
import com.jamesmorrisstudios.appbaselibrary.dialogs.DualSpinnerDialogBuilder;
import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemData;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderCriteria extends BaseEditReminder {
    private View advancedContainer, filterTypeNormalContainer, filterTypeManualContainer;

    private View startTimeContainer, endTimeContainer;
    private TextView startHour, startMinute, startAM, startPM, endHour, endMinute, endAM, endPM;

    private View daysOfWeekContainer, filterTypeContainer, daysOfYearContainer, daysOfMonthContainer, weeksOfMonthContainer, monthsOfYearContainer, repeatEveryContainer;
    private TextView daysOfWeek, filterType, daysOfYear, daysOfMonth, weeksOfMonth, monthsOfYear, repeatEvery;

    public EditReminderCriteria(View parent) {
        super(parent);
        advancedContainer = parent.findViewById(R.id.advancedContainer);
        filterTypeNormalContainer = parent.findViewById(R.id.filterTypeNormalContainer);
        filterTypeManualContainer = parent.findViewById(R.id.filterTypeManualContainer);

        startTimeContainer = parent.findViewById(R.id.startTimeContainer);
        endTimeContainer = parent.findViewById(R.id.endTimeContainer);
        View startTop = parent.findViewById(R.id.startTime);
        startHour = (TextView) startTop.findViewById(R.id.time_hour);
        startMinute = (TextView) startTop.findViewById(R.id.time_minute);
        startAM = (TextView) startTop.findViewById(R.id.time_am);
        startPM = (TextView) startTop.findViewById(R.id.time_pm);
        View endTop = parent.findViewById(R.id.endTime);
        endHour = (TextView) endTop.findViewById(R.id.time_hour);
        endMinute = (TextView) endTop.findViewById(R.id.time_minute);
        endAM = (TextView) endTop.findViewById(R.id.time_am);
        endPM = (TextView) endTop.findViewById(R.id.time_pm);

        filterTypeContainer = parent.findViewById(R.id.filterTypeContainer);
        filterType = (TextView) parent.findViewById(R.id.filterType);

        daysOfWeekContainer = parent.findViewById(R.id.daysOfWeekContainer);
        daysOfWeek = (TextView) parent.findViewById(R.id.daysOfWeek);

        daysOfYearContainer = parent.findViewById(R.id.daysOfYearContainer);
        daysOfYear = (TextView) parent.findViewById(R.id.daysOfYear);

        daysOfMonthContainer = parent.findViewById(R.id.daysOfMonthContainer);
        daysOfMonth = (TextView) parent.findViewById(R.id.daysOfMonth);

        weeksOfMonthContainer = parent.findViewById(R.id.weeksOfMonthContainer);
        weeksOfMonth = (TextView) parent.findViewById(R.id.weeksOfMonth);

        monthsOfYearContainer = parent.findViewById(R.id.monthsOfYearContainer);
        monthsOfYear = (TextView) parent.findViewById(R.id.monthsOfYear);

        repeatEveryContainer = parent.findViewById(R.id.repeatEveryContainer);
        repeatEvery = (TextView) parent.findViewById(R.id.repeatEvery);
    }

    public final void bindItem(EditReminderItem item, boolean showAdvanced) {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        if(showAdvanced) {
            advancedContainer.setVisibility(View.VISIBLE);
        } else {
            advancedContainer.setVisibility(View.GONE);
        }
        setData();
        addListeners();
    }


    private void setData() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        //Start and End Time
        UtilsTime.setTime(startHour, startMinute, startAM, startPM, remind.getStartTime());
        UtilsTime.setTime(endHour, endMinute, endAM, endPM, remind.getEndTime());

        //Filter Type
        filterType.setText(remind.getFilterType().getName());
        if(remind.getFilterType() == ReminderItemData.FilterType.NORMAL) {
            filterTypeNormalContainer.setVisibility(View.VISIBLE);
            filterTypeManualContainer.setVisibility(View.GONE);
        } else {
            filterTypeNormalContainer.setVisibility(View.GONE);
            filterTypeManualContainer.setVisibility(View.VISIBLE);
        }

        //Days of Week
        String daysOfWeekText = "";
        for(int i=0; i<UtilsTime.getWeekArray().length; i++) {
            if(remind.getDaysOfWeek()[i]) {
                if(!daysOfWeekText.isEmpty()) {
                    daysOfWeekText += ", ";
                }
                daysOfWeekText += UtilsTime.getWeekArray()[i].getNameShort();
            }
        }
        if(daysOfWeekText.isEmpty()) {
            daysOfWeek.setText(R.string.none);
        } else {
            daysOfWeek.setText(daysOfWeekText);
        }

        //Days of year
        if(remind.getDaysOfYear().size() == 0) {
            daysOfYear.setText(R.string.none);
        } else {
            daysOfYear.setText(R.string.some);
        }

        //Days of month
        String daysOfMonthText = "";
        for (int i = 0; i < ReminderItemData.DayOfMonth.values().length; i++) {
            if(remind.getDaysOfMonth()[i]) {
                if(!daysOfMonthText.isEmpty()) {
                    daysOfMonthText += ", ";
                }
                daysOfMonthText += ReminderItemData.DayOfMonth.values()[i].getName();
            }
        }
        if(daysOfMonthText.isEmpty()) {
            daysOfMonth.setText(R.string.none);
        } else {
            daysOfMonth.setText(daysOfMonthText);
        }

        //Weeks of month
        String weeksOfMonthText = "";
        for (int i = 0; i < ReminderItemData.WeekOptions.values().length; i++) {
            if(remind.getWeeksOfMonth()[i]) {
                if(!weeksOfMonthText.isEmpty()) {
                    weeksOfMonthText += ", ";
                }
                weeksOfMonthText += ReminderItemData.WeekOptions.values()[i].getName();
            }
        }
        if(weeksOfMonthText.isEmpty()) {
            weeksOfMonth.setText(R.string.none);
        } else {
            weeksOfMonth.setText(weeksOfMonthText);
        }

        //months of year
        String monthsOfYearText = "";
        for (int i = 0; i < UtilsTime.MonthsOfYear.values().length; i++) {
            if(remind.getMonthsOfYear()[i]) {
                if(!monthsOfYearText.isEmpty()) {
                    monthsOfYearText += ", ";
                }
                monthsOfYearText += UtilsTime.MonthsOfYear.values()[i].getNameShort();
            }
        }
        if(monthsOfYearText.isEmpty()) {
            monthsOfYear.setText(R.string.none);
        } else {
            monthsOfYear.setText(monthsOfYearText);
        }

        //Repeat Every
        repeatEvery.setText(remind.getRepeatCount() + " " + remind.getRepeatType().getName());
    }

    private void addListeners() {
        startTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new TimePickerRequest(remind.getStartTime().hour, remind.getStartTime().minute, remind.getStartTime().is24Hour(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.updateStartTime().hour = hourOfDay;
                        remind.updateStartTime().minute = minute;
                        setData();
                    }
                }));
            }
        });

        endTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new TimePickerRequest(remind.getEndTime().hour, remind.getEndTime().minute, remind.getEndTime().is24Hour(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.updateEndTime().hour = hourOfDay;
                        remind.updateEndTime().minute = minute;
                        setData();
                    }
                }));
            }
        });

        filterTypeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.filter_type);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        if (which == 0) {
                            remind.setFilterType(ReminderItemData.FilterType.NORMAL);
                        } else {
                            if(!Utils.isPro()) {
                                Utils.showProPopup();
                            } else {
                                remind.setFilterType(ReminderItemData.FilterType.MANUAL);
                            }
                        }
                        setData();
                    }
                };
                if(Utils.isPro()) {
                    String[] items = new String[] {AppBase.getContext().getString(R.string.normal), AppBase.getContext().getString(R.string.manual)};
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                } else {
                    IconItem[] items;
                    if(ThemeManager.getAppTheme() == ThemeManager.AppTheme.LIGHT) {
                        items = new IconItem[] {new IconItem(AppBase.getContext().getString(R.string.normal), 0), new IconItem(AppBase.getContext().getString(R.string.manual), R.drawable.pro_icon)};
                    } else {
                        items = new IconItem[] {new IconItem(AppBase.getContext().getString(R.string.normal), 0), new IconItem(AppBase.getContext().getString(R.string.manual), R.drawable.pro_icon_dark)};
                    }
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                }
            }
        });

        daysOfWeekContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.days_of_week);
                String[] week = UtilsTime.getWeekStringArray();
                final boolean[] checked = remind.getDaysOfWeek().clone();
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
                        remind.setDaysOfWeek(checked);
                        setData();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //On Negative. Unused
                    }
                }));
            }
        });

        daysOfYearContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                DateItem startDate = UtilsTime.getDateNow();
                startDate.month = startDate.month - 1;
                DateItem endDate = UtilsTime.getDateNow();
                endDate.year = endDate.year + 2;
                Bus.postObject(new MultiDatePickerRequest(startDate, endDate, remind.getDaysOfYear(), new DatePickerMultiDialogBuilder.MultiDatePickerListener() {
                    @Override
                    public void onSelection(@NonNull ArrayList<DateItem> arrayList) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setDaysOfYear(arrayList);
                        setData();
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
            }
        });

        daysOfMonthContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.days_of_month);
                String[] items = new String[ReminderItemData.DayOfMonth.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItemData.DayOfMonth.values()[i].getName();
                }
                final boolean[] checkedItems = remind.getDaysOfMonth().clone();
                Bus.postObject(new MultiChoiceRequest(title, items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Something was selected
                        checkedItems[which] = isChecked;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Commit
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setDaysOfMonth(checkedItems);
                        setData();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                }));
            }
        });

        weeksOfMonthContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.weeks_of_month);
                String[] items = new String[ReminderItemData.WeekOptions.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItemData.WeekOptions.values()[i].getName();
                }
                final boolean[] checkedItems = remind.getWeeksOfMonth().clone();
                Bus.postObject(new MultiChoiceRequest(title, items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Something was selected
                        checkedItems[which] = isChecked;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Commit
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setWeeksOfMonth(checkedItems);
                        setData();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                }));
            }
        });

        monthsOfYearContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppBase.getContext().getString(R.string.months_of_year);
                String[] items = new String[UtilsTime.MonthsOfYear.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = UtilsTime.MonthsOfYear.values()[i].getName();
                }
                final boolean[] checkedItems = remind.getMonthsOfYear().clone();
                Bus.postObject(new MultiChoiceRequest(title, items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Something was selected
                        checkedItems[which] = isChecked;
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Commit
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setMonthsOfYear(checkedItems);
                        setData();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                }));
            }
        });

        repeatEveryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }

                String title = AppBase.getContext().getString(R.string.repeat_every);

                List<String> firstList = new ArrayList<>();
                for (int i = 0; i < 60; i++) {
                    firstList.add(Integer.toString(i + 1));
                }
                int firstSelected = remind.getRepeatCount() -1;

                List<String> secondList = new ArrayList<>();
                for (int i = 0; i < ReminderItemData.RepeatType.values().length; i++) {
                    secondList.add(ReminderItemData.RepeatType.values()[i].getName());
                }
                int secondSelected = remind.getRepeatType().ordinal();

                int[] firstRestrictions = null;
                int[] secondRestrictions = new int[] {60, 52, 12, 1};

                Bus.postObject(new DualSpinnerRequest(title, firstList, firstSelected, firstRestrictions, secondList, secondSelected, secondRestrictions, new DualSpinnerDialogBuilder.DualSpinnerListener() {
                    @Override
                    public void onSelection(int firstSelected, int secondSelected) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setRepeatCount(firstSelected+1);
                        remind.setRepeatType(ReminderItemData.RepeatType.values()[secondSelected]);
                        setData();
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
            }
        });

    }

}
