package jamesmorrisstudios.com.randremind.reminder;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Serialized reminder storage object
 * <p/>
 * Created by James on 4/28/2015.
 */
public class ReminderListData {
    @SerializedName("data")
    public ArrayList<ReminderItemData> reminderItemList = new ArrayList<>();
}
