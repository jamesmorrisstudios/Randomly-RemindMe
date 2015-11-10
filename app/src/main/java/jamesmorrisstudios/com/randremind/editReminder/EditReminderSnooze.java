package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemData;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 8/17/2015.
 */
public class EditReminderSnooze extends BaseEditReminder {
    private RelativeLayout snoozeContainer, autoSnoozeContainer;
    private TextView snooze, autoSnooze;

    public EditReminderSnooze(View parent) {
        super(parent);
        snoozeContainer = (RelativeLayout) parent.findViewById(R.id.manual_snooze_container);
        autoSnoozeContainer = (RelativeLayout) parent.findViewById(R.id.auto_snooze_container);
        snooze = (TextView) parent.findViewById(R.id.manual_snooze);
        autoSnooze = (TextView) parent.findViewById(R.id.auto_snooze);
    }

    public final void bindItem(EditReminderItem item, boolean showAdvanced) {
        final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        snooze.setText(remind.getSnooze().getName());
        autoSnooze.setText(remind.getAutoSnooze().getName());

        snoozeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.snooze);
                String[] items = new String[ReminderItemData.SnoozeOptions.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItemData.SnoozeOptions.values()[i].getName();
                }
                Bus.postObject(new SingleChoiceRequest(title, items, true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setSnooze(ReminderItemData.SnoozeOptions.values()[which]);
                        snooze.setText(remind.getSnooze().getName());
                    }
                }, null));
            }
        });

        autoSnoozeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.auto_snooze);
                String[] items = new String[ReminderItemData.SnoozeOptions.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItemData.SnoozeOptions.values()[i].getName();
                }
                Bus.postObject(new SingleChoiceRequest(title, items, true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setAutoSnooze(ReminderItemData.SnoozeOptions.values()[which]);
                        autoSnooze.setText(remind.getAutoSnooze().getName());
                    }
                }, null));
            }
        });


    }

}
