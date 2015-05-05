package jamesmorrisstudios.com.randremind.reminder;

import com.google.gson.annotations.SerializedName;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.utilitieslibrary.time.DateItem;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;

import java.util.ArrayList;

/**
 * Created by James on 4/30/2015.
 */
public class ReminderLogDay extends BaseRecycleItem {
    @SerializedName("date")
    public DateItem date;
    @SerializedName("timesShown")
    public ArrayList<TimeItem> timesShown = new ArrayList<>();
    @SerializedName("timesClicked")
    public ArrayList<TimeItem> timesClicked = new ArrayList<>();

    public ReminderLogDay(DateItem dateItem) {
        this.date = dateItem;
    }

}
