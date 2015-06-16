package jamesmorrisstudios.com.randremind.listAdapters;

import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderViewHolder;
import com.jamesmorrisstudios.utilitieslibrary.animator.AnimatorControl;
import com.jamesmorrisstudios.utilitieslibrary.controls.TintedImageView;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderGeneral;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderMessage;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderNotificationAlarm;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderRepeat;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderTiming;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderViews;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderViewHolder extends BaseRecycleNoHeaderViewHolder {

    public enum EditReminderPage {
        GENERAL, MESSAGE, TIMING, REPEAT, NOTIFICATION_ALARM
    }

    private TextView title;
    private TintedImageView toggleExpand;
    private EditReminderViews views;
    private EditReminderGeneral general;
    private EditReminderMessage message;
    private EditReminderTiming timing;
    private EditReminderRepeat repeat;
    private EditReminderNotificationAlarm notificationAlarm;

    public EditReminderViewHolder(View view, cardClickListener mListener) {
        super(view, mListener);
    }

    @Override
    protected void initItem(View view) {
        views = new EditReminderViews();
        general = new EditReminderGeneral();
        message = new EditReminderMessage();
        timing = new EditReminderTiming();
        repeat = new EditReminderRepeat();
        notificationAlarm = new EditReminderNotificationAlarm();

        title = (TextView)view.findViewById(R.id.title);
        toggleExpand = (TintedImageView) view.findViewById(R.id.toggle_expand);
        views.initItem(view);
        general.initItem(views);
        message.initItem(views);
        timing.initItem(views);
        repeat.initItem(views);
        notificationAlarm.initItem(views);
    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean b) {
        final EditReminderItem item = (EditReminderItem)baseRecycleItem;
        views.currentPage = item.page;
        switch(item.page) {
            case GENERAL:
                title.setText("General");
                general.bindItem(item);
                break;
            case MESSAGE:
                title.setText("Message");
                message.bindItem(item);
                break;
            case TIMING:
                title.setText("Timing");
                timing.bindItem(item);
                break;
            case REPEAT:
                title.setText("Repeat");
                repeat.bindItem(item);
                break;
            case NOTIFICATION_ALARM:
                title.setText("Notification");
                notificationAlarm.bindItem(item);
                break;
        }
        if(item.visible) {
            views.showPage(item.page);
        } else {
            views.hideAllPages();
        }
        toggleExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.visible) {
                    item.visible = false;
                    views.hideAllPages();
                    AnimatorControl.rotationAutoStart(toggleExpand, 0, 180, 100, 0, null);
                } else {
                    item.visible = true;
                    views.showPage(views.currentPage);
                    AnimatorControl.rotationAutoStart(toggleExpand, 180, 0, 100, 0, null);
                }
            }
        });

    }

}
