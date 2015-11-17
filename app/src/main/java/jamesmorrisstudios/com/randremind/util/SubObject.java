package jamesmorrisstudios.com.randremind.util;

import com.jamesmorrisstudios.appbaselibrary.time.DateItem;
import com.jamesmorrisstudios.appbaselibrary.time.DateTimeItem;

import java.util.ArrayList;

/**
 * Created by James on 11/17/2015.
 */
public class SubObject {
    //Either a weeks or a months worth of dates

    public ArrayList<DateItem> dates = new ArrayList<>();
    public ArrayList<DateTimeItem> alerts = new ArrayList<>();
    public int subNumber = -1;
    public int minutes;
    public int[] alertTimes = null;

}
