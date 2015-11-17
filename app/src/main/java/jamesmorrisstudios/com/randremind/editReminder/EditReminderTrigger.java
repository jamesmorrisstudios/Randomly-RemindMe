package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.IconItem;
import com.jamesmorrisstudios.appbaselibrary.ThemeManager;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.DualSpinnerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.DualSpinnerDialogBuilder;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.EditTimesRequest;
import jamesmorrisstudios.com.randremind.fragments.EditTimesDialog;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemData;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 * Created by James on 11/3/2015.
 */
public class EditReminderTrigger extends BaseEditReminder {
    private View countContainer, periodContainer, specificContainer, triggerTypeContainer, countPeriodContainer, intervalContainer;
    private TextView period, specific, triggerType, interval;
    private AppCompatSpinner countSpinner;

    public EditReminderTrigger(View parent) {
        super(parent);

        countContainer = parent.findViewById(R.id.countContainer);
        periodContainer = parent.findViewById(R.id.periodContainer);
        specificContainer = parent.findViewById(R.id.specificContainer);
        triggerTypeContainer = parent.findViewById(R.id.triggerTypeContainer);
        countPeriodContainer = parent.findViewById(R.id.countPeriodContainer);
        intervalContainer = parent.findViewById(R.id.intervalContainer);

        countSpinner = (AppCompatSpinner) parent.findViewById(R.id.countSpinner);
        period = (TextView) parent.findViewById(R.id.period);
        specific = (TextView) parent.findViewById(R.id.specific);
        triggerType = (TextView) parent.findViewById(R.id.triggerType);
        interval = (TextView) parent.findViewById(R.id.interval);
    }

    public final void bindItem(EditReminderItem item, boolean showAdvanced) {
        setData();
        addListeners();
    }

    private void setData() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }

        //Trigger mode
        if(remind.getTriggerMode() == ReminderItemData.TriggerMode.RANDOM) {
            triggerType.setText(ReminderItemData.TriggerMode.RANDOM.getName());
            countPeriodContainer.setVisibility(View.VISIBLE);
            specificContainer.setVisibility(View.GONE);
            intervalContainer.setVisibility(View.GONE);
        } else if(remind.getTriggerMode() == ReminderItemData.TriggerMode.LESS_RANDOM) {
            triggerType.setText(ReminderItemData.TriggerMode.LESS_RANDOM.getName());
            countPeriodContainer.setVisibility(View.VISIBLE);
            specificContainer.setVisibility(View.GONE);
            intervalContainer.setVisibility(View.GONE);
        } else if(remind.getTriggerMode() == ReminderItemData.TriggerMode.EVEN) {
            triggerType.setText(ReminderItemData.TriggerMode.EVEN.getName());
            countPeriodContainer.setVisibility(View.VISIBLE);
            specificContainer.setVisibility(View.GONE);
            intervalContainer.setVisibility(View.GONE);
        } else if(remind.getTriggerMode() == ReminderItemData.TriggerMode.INTERVAL) {
            triggerType.setText(ReminderItemData.TriggerMode.INTERVAL.getName());
            countPeriodContainer.setVisibility(View.GONE);
            specificContainer.setVisibility(View.GONE);
            intervalContainer.setVisibility(View.VISIBLE);
        } else {
            triggerType.setText(ReminderItemData.TriggerMode.SPECIFIC.getName());
            countPeriodContainer.setVisibility(View.GONE);
            specificContainer.setVisibility(View.VISIBLE);
            intervalContainer.setVisibility(View.GONE);
        }

        //Time period
        generateCount();
        period.setText(remind.getTriggerPeriod().getName());

        //Interval
        interval.setText(remind.getIntervalCount() + " " + remind.getIntervalPeriod().getName());
    }

    private void addListeners() {

        triggerTypeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.trigger_type);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }

                        ReminderItemData.TriggerMode mode = ReminderItemData.TriggerMode.values()[which];
                        if(mode == ReminderItemData.TriggerMode.EVEN || mode == ReminderItemData.TriggerMode.LESS_RANDOM) {
                            if(!Utils.isPro()) {
                                Utils.showProPopup();
                            } else {
                                remind.setTriggerMode(mode);
                            }
                        } else {
                            remind.setTriggerMode(mode);
                        }
                        setData();
                    }
                };
                if(Utils.isPro()) {
                    String[] items = new String[ReminderItemData.TriggerMode.values().length];
                    for (int i = 0; i < items.length; i++) {
                        items[i] = ReminderItemData.TriggerMode.values()[i].getName();
                    }
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                } else {
                    IconItem[] items = new IconItem[ReminderItemData.TriggerMode.values().length];
                    for (int i = 0; i < items.length; i++) {
                        if(i==1 || i==2) {
                            if(ThemeManager.getAppTheme() == ThemeManager.AppTheme.LIGHT) {
                                items[i] = new IconItem(ReminderItemData.TriggerMode.values()[i].getName(), R.drawable.pro_icon);
                            } else {
                                items[i] = new IconItem(ReminderItemData.TriggerMode.values()[i].getName(), R.drawable.pro_icon_dark);
                            }
                        } else {
                            items[i] = new IconItem(ReminderItemData.TriggerMode.values()[i].getName(), 0);
                        }
                    }
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                }
            }
        });

        periodContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.time_period);
                String[] items = new String[ReminderItemData.TimePeriod.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItemData.TimePeriod.values()[i].getName();
                }
                Bus.postObject(new SingleChoiceRequest(title, items, true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setTriggerPeriod(ReminderItemData.TimePeriod.values()[which]);
                        setData();
                    }
                }, null));
            }
        });

        countSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.setTriggerCount(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        countContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countSpinner.performClick();
            }
        });


        intervalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }

                String title = AppBase.getContext().getString(R.string.every);

                List<String> firstList = new ArrayList<>();
                for (int i = 0; i < 180; i++) {
                    firstList.add(Integer.toString(i + 1));
                }
                int firstSelected = remind.getIntervalCount() - 1;

                List<String> secondList = new ArrayList<>();
                for (int i = 0; i < ReminderItemData.RepeatTypeShort.values().length; i++) {
                    secondList.add(ReminderItemData.RepeatTypeShort.values()[i].getName());
                }
                int secondSelected = remind.getIntervalPeriod().ordinal();

                Bus.postObject(new DualSpinnerRequest(title, firstList, firstSelected, secondList, secondSelected, new DualSpinnerDialogBuilder.DualSpinnerListener() {
                    @Override
                    public void onSelection(int firstSelected, int secondSelected) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setIntervalCount(firstSelected + 1);
                        remind.setIntervalPeriod(ReminderItemData.RepeatTypeShort.values()[secondSelected]);
                        setData();
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
            }
        });

        specificContainer.setOnClickListener(new View.OnClickListener() {
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

    }

    private void generateCount() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        countSpinner.setAdapter(spinnerArrayAdapter);
        countSpinner.setSelection(remind.getTriggerCount() - 1);
    }

}
