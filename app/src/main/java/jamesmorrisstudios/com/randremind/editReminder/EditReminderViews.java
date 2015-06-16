package jamesmorrisstudios.com.randremind.editReminder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderViewHolder;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderViews {
    public EditReminderViewHolder.EditReminderPage currentPage = EditReminderViewHolder.EditReminderPage.GENERAL;
    public RelativeLayout general, message, timing, repeat, notificationAlarm;

    public final void initItem(View view) {
        general = (RelativeLayout)view.findViewById(R.id.container_general);
        message = (RelativeLayout)view.findViewById(R.id.container_message);
        timing = (RelativeLayout)view.findViewById(R.id.container_timing);
        repeat = (RelativeLayout)view.findViewById(R.id.container_repeat);
        notificationAlarm = (RelativeLayout)view.findViewById(R.id.container_notification_alarm);
    }

    public final void showPage(EditReminderViewHolder.EditReminderPage page) {
        switch(page) {
            case GENERAL:
                general.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                timing.setVisibility(View.GONE);
                repeat.setVisibility(View.GONE);
                notificationAlarm.setVisibility(View.GONE);
                break;
            case MESSAGE:
                general.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                timing.setVisibility(View.GONE);
                repeat.setVisibility(View.GONE);
                notificationAlarm.setVisibility(View.GONE);
                break;
            case TIMING:
                general.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                timing.setVisibility(View.VISIBLE);
                repeat.setVisibility(View.GONE);
                notificationAlarm.setVisibility(View.GONE);
                break;
            case REPEAT:
                general.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                timing.setVisibility(View.GONE);
                repeat.setVisibility(View.VISIBLE);
                notificationAlarm.setVisibility(View.GONE);
                break;
            case NOTIFICATION_ALARM:
                general.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                timing.setVisibility(View.GONE);
                repeat.setVisibility(View.GONE);
                notificationAlarm.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void hideAllPages() {
        general.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        timing.setVisibility(View.GONE);
        repeat.setVisibility(View.GONE);
        notificationAlarm.setVisibility(View.GONE);
    }

}
