package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;

import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderLogDay;
import jamesmorrisstudios.com.randremind.reminder.ReminderLogItem;

/**
 * Created by James on 9/15/2015.
 */
public class ReminderLogDialog extends DialogFragment {
    private ListView list;
    private Button btnClose;
    private ReminderLogDay reminderLogDay = null;
    private ListAdapter adapter = null;

    public ReminderLogDialog() {
        // Empty constructor required for DialogFragment
    }

    public void onPause() {
        dismiss();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_log_dialog, container);
        list = (ListView) view.findViewById(R.id.list);
        btnClose = (Button) view.findViewById(R.id.btn_close);
        if(reminderLogDay != null) {
            adapter = new ListAdapter(getActivity(), R.layout.edit_times_item, reminderLogDay.getItemList());

            // Assign adapter to ListView
            list.setAdapter(adapter);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });
        return view;
    }

    public void setData(@NonNull ReminderLogDay reminderLogDay) {
        this.reminderLogDay = reminderLogDay;
    }

    class ListAdapter extends ArrayAdapter<ReminderLogItem> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<ReminderLogItem> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ReminderLogItem item = getItem(position);

            TextView text1 = null, text2 = null;
            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.reminder_log_item, null);
            }
            text1 = (TextView) view.findViewById(R.id.text1);
            text2 = (TextView) view.findViewById(R.id.text2);

            if (item != null) {
                text1.setText(item.type.getName());
                text2.setText(Utils.getFormattedDateTime(UtilsTime.getTimeMillis(item.dateTime)));
            }
            return view;
        }

    }
}
