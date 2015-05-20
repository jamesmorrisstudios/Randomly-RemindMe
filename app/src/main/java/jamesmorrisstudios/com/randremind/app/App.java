package jamesmorrisstudios.com.randremind.app;

import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;

import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 5/20/2015.
 */
public class App extends AppUtil {
    private ReminderList data;

    /**
     * Initial create for the application.
     * Sets application level context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        data = ReminderList.getInstance();
    }
}
