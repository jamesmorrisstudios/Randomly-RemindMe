package jamesmorrisstudios.com.randremind.dialogHelper;

import android.content.DialogInterface;

import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.fragments.EditTimesDialogBuilder;

/**
 * Created by James on 6/30/2015.
 */
public class EditTimesRequest {
    public final ArrayList<TimeItem> times;
    public final EditTimesDialogBuilder.EditTimesListener onPositive;
    public final DialogInterface.OnClickListener onNegative;

    public EditTimesRequest(ArrayList<TimeItem> times, EditTimesDialogBuilder.EditTimesListener onPositive, DialogInterface.OnClickListener onNegative) {
        this.times = times;
        this.onPositive = onPositive;
        this.onNegative = onNegative;
    }
}
