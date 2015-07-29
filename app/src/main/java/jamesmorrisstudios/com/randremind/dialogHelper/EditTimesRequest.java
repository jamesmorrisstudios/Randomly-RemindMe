package jamesmorrisstudios.com.randremind.dialogHelper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.fragments.EditTimesDialog;

/**
 * Created by James on 6/30/2015.
 */
public class EditTimesRequest {
    public final ArrayList<TimeItem> times;
    public final EditTimesDialog.EditTimesListener onPositive;
    public final View.OnClickListener onNegative;
    public final boolean allowEdit;

    public EditTimesRequest(@NonNull ArrayList<TimeItem> times, @NonNull EditTimesDialog.EditTimesListener onPositive, @Nullable View.OnClickListener onNegative, boolean allowEdit) {
        this.times = times;
        this.onPositive = onPositive;
        this.onNegative = onNegative;
        this.allowEdit = allowEdit;
    }
}
