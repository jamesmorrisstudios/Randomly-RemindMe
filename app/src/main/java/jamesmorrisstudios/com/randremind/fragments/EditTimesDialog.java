package jamesmorrisstudios.com.randremind.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 7/9/2015.
 */
public class EditTimesDialog extends DialogFragment {

    private ListView list;
    private Button btnCancel, btnOk, btnAdd;
    private ArrayList<TimeItem> times = null;
    private ListAdapter adapter = null;
    private EditTimesListener onPositive;
    private View.OnClickListener onNegative;
    private boolean allowEdit = true;

    public EditTimesDialog() {
        // Empty constructor required for DialogFragment
    }

    public void onPause() {
        dismiss();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_times_dialog, container);
        list = (ListView) view.findViewById(R.id.list);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnAdd = (Button) view.findViewById(com.jamesmorrisstudios.appbaselibrary.R.id.btn_add);

        if(times != null) {
            adapter = new ListAdapter(getActivity(), R.layout.edit_times_item, times);

            // Assign adapter to ListView
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.v("EditTimesDialog", "Item Clicked: " + position);
                }
            });
            list.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.v("EditTimesDialog", "Key Press");
                    return false;
                }
            });
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNegative.onClick(v);
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPositive != null && adapter != null) {
                    onPositive.onPositive(adapter.getItems());
                }
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(new TimeItem(9, 0));
            }
        });
        return view;
    }

    public void setData(@NonNull ArrayList<TimeItem> times, @NonNull EditTimesListener onPositive, @Nullable View.OnClickListener onNegative, boolean allowEdit) {
        this.times = new ArrayList<>(times);
        this.onPositive = onPositive;
        this.onNegative = onNegative;
        this.allowEdit = allowEdit;
    }

    public interface EditTimesListener {
        void onPositive(ArrayList<TimeItem> times);
    }

    class ListAdapter extends ArrayAdapter<TimeItem> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<TimeItem> items) {
            super(context, resource, items);
        }

        public ArrayList<TimeItem> getItems() {
            ArrayList<TimeItem> list = new ArrayList<>();
            for(int i=0; i<getCount(); i++) {
                list.add(getItem(i));
            }
            return list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TimeItem item = getItem(position);

            TextView hour = null, minute = null, AM = null, PM = null;
            ImageView delete;
            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.edit_times_item, null);
            }
            hour = (TextView) view.findViewById(R.id.time_hour);
            minute = (TextView) view.findViewById(R.id.time_minute);
            AM = (TextView) view.findViewById(R.id.time_am);
            PM = (TextView) view.findViewById(R.id.time_pm);
            delete = (ImageView) view.findViewById(com.jamesmorrisstudios.appbaselibrary.R.id.delete1);

            if(allowEdit && item != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bus.postObject(new TimePickerRequest(item.hour, item.minute, item.is24Hour(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                item.hour = hour;
                                item.minute = minute;
                                notifyDataSetChanged();
                            }
                        }));
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove(item);
                    }
                });
            }

            if (item != null) {
                UtilsTime.setTime(hour, minute, AM, PM, item);
            }
            return view;
        }

    }

}
