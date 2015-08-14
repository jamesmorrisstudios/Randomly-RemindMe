package jamesmorrisstudios.com.randremind.editReminder;

import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.EditTextListRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.EditTextListDialog;
import com.jamesmorrisstudios.utilitieslibrary.Bus;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderMessage {
    private AppCompatSpinner numMessages;
    private RadioButton inOrder, random;
    private Button editMessages;

    public EditReminderMessage(View parent) {
        numMessages = (AppCompatSpinner) parent.findViewById(R.id.message_count);
        inOrder = (RadioButton) parent.findViewById(R.id.radio_in_order);
        random = (RadioButton) parent.findViewById(R.id.radio_random);
        editMessages = (Button) parent.findViewById(R.id.btn_edit_messages);

        List<String> perDayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            perDayList.add(Integer.toString(i + 1));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(parent.getContext(), R.layout.support_simple_spinner_dropdown_item, perDayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_drop_down_item);
        numMessages.setAdapter(spinnerArrayAdapter);
        numMessages.setSelection(0);
    }

    public final void bindItem(EditReminderItem item) {
        ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
        if(reminderItem == null) {
            return;
        }
        if(reminderItem.isMessageInOrder()) {
            inOrder.setChecked(true);
            random.setChecked(false);
        } else {
            inOrder.setChecked(false);
            random.setChecked(true);
        }
        inOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                    if(reminderItem == null) {
                        return;
                    }
                    reminderItem.setMessageInOrder(true);
                    reminderItem.setCurMessage(-1);
                }
            }
        });
        random.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                    if(reminderItem == null) {
                        return;
                    }
                    reminderItem.setMessageInOrder(false);
                    reminderItem.setCurMessage(-1);
                }
            }
        });
        editMessages.setOnClickListener(new View.OnClickListener() {
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
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
            }
        });
        numMessages.setSelection(reminderItem.getMessageList().size()-1);
        numMessages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
                if(reminderItem == null) {
                    return;
                }
                int size = position + 1;
                //If we are making the list bigger
                while (size > reminderItem.getMessageList().size()) {
                    reminderItem.updateMessageList().add("");
                    reminderItem.setCurMessage(-1);
                }
                //If we are making the list smaller
                while (size < reminderItem.getMessageList().size()) {
                    reminderItem.updateMessageList().remove(reminderItem.getMessageList().size() - 1);
                    reminderItem.setCurMessage(-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
