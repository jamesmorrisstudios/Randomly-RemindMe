package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.TimePickerRequest;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.Utils;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 6/30/2015.
 */
public class EditTimesDialogBuilder {
    private AlertDialog.Builder builder;
    private ScrollView mainView;
    private LinearLayout pickerContainer;
    private AlertDialog dialog;
    private ArrayList<TimeItem> times = null;
    private EditTimesListener onEditTimesListener;

    private EditTimesDialogBuilder(Context context) {
        builder = new AlertDialog.Builder(context, R.style.alertDialog);
        mainView = new ScrollView(context);
        pickerContainer = new LinearLayout(context);
        pickerContainer.setOrientation(LinearLayout.VERTICAL);
        pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mainView.addView(pickerContainer);
        builder.setView(mainView);
    }

    public static EditTimesDialogBuilder with(@NonNull Context context) {
        return new EditTimesDialogBuilder(context);
    }

    public EditTimesDialogBuilder setTitle(@NonNull String title) {
        builder.setTitle(title);
        return this;
    }

    public EditTimesDialogBuilder setTimes(@NonNull ArrayList<TimeItem> times) {
        this.times = new ArrayList<>(times);
        return this;
    }

    public EditTimesDialogBuilder setOnPositive(@NonNull String text, @NonNull EditTimesListener onClickListener) {
        this.onEditTimesListener = onClickListener;
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onEditTimesListener.onPositive(times);
            }
        });
        return this;
    }

    public EditTimesDialogBuilder setOnNegative(@NonNull String text, @NonNull DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(text, onClickListener);
        return this;
    }

    public AlertDialog build() {
        Context context = builder.getContext();
        buildIconList(context);
        dialog = builder.create();
        return dialog;
    }

    @NonNull
    public final ArrayList<TimeItem> getTimes() {
        return times;
    }

    private void buildIconList(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < times.size(); i++) {
            RelativeLayout timeDisplay = addTimeDisplay(inflater, times.get(i));
            LinearLayout.LayoutParams paramTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramTop.setMargins(Utils.getDipInt(8), Utils.getDipInt(8), Utils.getDipInt(8), Utils.getDipInt(8));
            timeDisplay.setLayoutParams(paramTop);
            timeDisplay.setGravity(Gravity.CENTER_HORIZONTAL);
            pickerContainer.addView(timeDisplay);
        }
    }

    private RelativeLayout addTimeDisplay(LayoutInflater inflater, final TimeItem timeItem) {
        RelativeLayout timeDisplay = (RelativeLayout) inflater.inflate(R.layout.time_display, null);
        final TextView startHour = (TextView) timeDisplay.findViewById(R.id.time_hour);
        final TextView startMinute = (TextView) timeDisplay.findViewById(R.id.time_minute);
        final TextView startAM = (TextView) timeDisplay.findViewById(R.id.time_am);
        final TextView startPM = (TextView) timeDisplay.findViewById(R.id.time_pm);
        UtilsTime.setTime(startHour, startMinute, startAM, startPM, timeItem);

        final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(@NonNull RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                timeItem.hour = hourOfDay;
                timeItem.minute = minute;
                UtilsTime.setTime(startHour, startMinute, startAM, startPM, timeItem);
            }
        };
        timeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bus.postObject(new TimePickerRequest(timeStartListener, timeItem.hour,
                        timeItem.minute, timeItem.is24Hour()));
            }
        });
        return timeDisplay;
    }

    public interface EditTimesListener {
        void onPositive(ArrayList<TimeItem> times);
    }

}
