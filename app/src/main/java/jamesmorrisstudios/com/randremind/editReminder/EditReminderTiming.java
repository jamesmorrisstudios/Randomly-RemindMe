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
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleDatePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.DatePickerMultiDialogBuilder;
import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderTiming extends BaseEditReminder {
    private View startDateContainer, endDateContainer;
    private TextView startDate, endDate;

    public EditReminderTiming(View parent) {
        super(parent);
        startDateContainer = parent.findViewById(R.id.startDateContainer);
        endDateContainer = parent.findViewById(R.id.endDateContainer);
        startDate = (TextView) parent.findViewById(R.id.startDate);
        endDate = (TextView) parent.findViewById(R.id.endDate);
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
        startDate.setText(UtilsTime.getDateFormatted(remind.getStartDate()));
        if(remind.isEndEnable()) {
            endDate.setText(UtilsTime.getDateFormatted(remind.getEndDate()));
        } else {
            endDate.setText(R.string.forever);
        }
    }

    private void addListeners() {
        startDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                DateItem startDate = UtilsTime.getDateNow();
                startDate.year = startDate.year - 1;
                DateItem endDate = UtilsTime.getDateNow();
                endDate.year = endDate.year + 2;
                Bus.postObject(new SingleDatePickerRequest(startDate, endDate, remind.getStartDate(), new DatePickerMultiDialogBuilder.SingleDatePickerListener() {
                    @Override
                    public void onSelection(@NonNull DateItem dateItem) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.updateStartDate().year = dateItem.year;
                        remind.updateStartDate().month = dateItem.month;
                        remind.updateStartDate().dayOfMonth = dateItem.dayOfMonth;
                        setData();
                    }
                    @Override
                    public void onCancel() {

                    }
                }));
            }
        });


        endDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.end_type);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) { //Date picker
                            if(!Utils.isPro()) {
                                Utils.showProPopup();
                                return;
                            }
                            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                            if (remind == null) {
                                return;
                            }
                            DateItem startDate = UtilsTime.getDateNow();
                            startDate.month = startDate.month - 1;
                            DateItem endDate = UtilsTime.getDateNow();
                            endDate.year = endDate.year + 2;
                            Bus.postObject(new SingleDatePickerRequest(startDate, endDate, remind.getEndDate(), new DatePickerMultiDialogBuilder.SingleDatePickerListener() {
                                @Override
                                public void onSelection(@NonNull DateItem dateItem) {
                                    ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                                    if (remind == null) {
                                        return;
                                    }
                                    remind.setEndEnable(true);
                                    remind.updateEndDate().year = dateItem.year;
                                    remind.updateEndDate().month = dateItem.month;
                                    remind.updateEndDate().dayOfMonth = dateItem.dayOfMonth;
                                    setData();
                                }
                                @Override
                                public void onCancel() {

                                }
                            }));
                        } else { //Forever
                            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                            if (remind == null) {
                                return;
                            }
                            remind.setEndEnable(false);
                            setData();
                        }
                    }
                };

                if(Utils.isPro()) {
                    String[] items = new String[] {AppBase.getContext().getString(R.string.end_date), AppBase.getContext().getString(R.string.forever)};
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                } else {
                    IconItem[] items;
                    if(ThemeManager.getAppTheme() == ThemeManager.AppTheme.LIGHT) {
                        items = new IconItem[] {new IconItem(AppBase.getContext().getString(R.string.end_date), R.drawable.pro_icon), new IconItem(AppBase.getContext().getString(R.string.forever), 0)};
                    } else {
                        items = new IconItem[] {new IconItem(AppBase.getContext().getString(R.string.end_date), R.drawable.pro_icon_dark), new IconItem(AppBase.getContext().getString(R.string.forever), 0)};
                    }
                    Bus.postObject(new SingleChoiceRequest(title, items, true, listener, null));
                }
            }
        });
    }

}
