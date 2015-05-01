package jamesmorrisstudios.com.randremind.reminder;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by James on 4/30/2015.
 */
public class ReminderLog {
    @SerializedName("days")
    public ArrayList<ReminderLogDay> days = new ArrayList<>();
}
