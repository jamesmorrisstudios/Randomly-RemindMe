package jamesmorrisstudios.com.randremind.reminder;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Serialized reminder storage object
 * <p/>
 * Created by James on 4/28/2015.
 */
public class Reminders {
    @SerializedName("data")
    public ArrayList<ReminderItem> data = new ArrayList<>();
}
