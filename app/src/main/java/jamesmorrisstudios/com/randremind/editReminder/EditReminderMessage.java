package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.EditTextListRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.EditTextListDialog;
import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderMessage {
    private View orderContainer, messageContainer;
    private TextView order;

    public EditReminderMessage(View parent) {
        orderContainer = parent.findViewById(R.id.orderContainer);
        messageContainer = parent.findViewById(R.id.messageContainer);
        order = (TextView) parent.findViewById(R.id.order);
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
        if(reminderItem == null) {
            return;
        }
        if(reminderItem.isMessageInOrder()) {
            order.setText(AppBase.getContext().getString(R.string.in_order));
        } else {
            order.setText(AppBase.getContext().getString(R.string.random));
        }
        orderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppBase.getContext().getString(R.string.ordering);
                String[] items = new String[]{AppBase.getContext().getString(R.string.in_order), AppBase.getContext().getString(R.string.random)};

                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                        if (reminderItem == null) {
                            return;
                        }
                        if (which == 0) {
                            reminderItem.setMessageInOrder(true);
                            order.setText(AppBase.getContext().getString(R.string.in_order));
                        } else {
                            reminderItem.setMessageInOrder(false);
                            order.setText(AppBase.getContext().getString(R.string.random));
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing on negative
                    }
                }));
            }
        });
        messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                if(reminderItem == null) {
                    return;
                }
                Bus.postObject(new EditTextListRequest(reminderItem.getMessageList(), new EditTextListDialog.EditMessageListener() {
                    @Override
                    public void onPositive(ArrayList<String> messages) {
                        ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                        if(reminderItem == null) {
                            return;
                        }
                        reminderItem.setMessageList(messages);
                        reminderItem.setCurMessage(-1);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
            }
        });
    }

}
