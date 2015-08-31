package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 8/17/2015.
 */
public class EditReminderSnooze {
    private RelativeLayout snoozeContainer, autoSnoozeContainer;
    private TextView snooze, autoSnooze;

    public EditReminderSnooze(View parent) {
        snoozeContainer = (RelativeLayout) parent.findViewById(R.id.manual_snooze_container);
        autoSnoozeContainer = (RelativeLayout) parent.findViewById(R.id.auto_snooze_container);
        snooze = (TextView) parent.findViewById(R.id.manual_snooze);
        autoSnooze = (TextView) parent.findViewById(R.id.auto_snooze);
    }

    public final void bindItem(EditReminderItem item) {
        final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }
        snooze.setText(remind.getSnooze().name);
        autoSnooze.setText(remind.getAutoSnooze().name);

        snoozeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppUtil.getContext().getString(R.string.snooze);
                String[] items = new String[ReminderItem.SnoozeOptions.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItem.SnoozeOptions.values()[i].name;
                }
                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setSnooze(ReminderItem.SnoozeOptions.values()[which]);
                        snooze.setText(remind.getSnooze().name);
                    }
                }, null));
            }
        });

        autoSnoozeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppUtil.getContext().getString(R.string.auto_snooze);
                String[] items = new String[ReminderItem.SnoozeOptions.values().length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = ReminderItem.SnoozeOptions.values()[i].name;
                }
                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setAutoSnooze(ReminderItem.SnoozeOptions.values()[which]);
                        autoSnooze.setText(remind.getAutoSnooze().name);
                    }
                }, null));
            }
        });


    }



}
