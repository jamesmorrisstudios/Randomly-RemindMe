package jamesmorrisstudios.com.randremind.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderViewHolder;
import com.jamesmorrisstudios.utilitieslibrary.animator.AnimatorControl;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.controls.TintedImageView;
import com.nineoldandroids.view.ViewHelper;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderAlarm;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderGeneral;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderMessage;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderNotification;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderRepeat;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderTiming;

/**
 *
 * Created by James on 6/8/2015.
 */
public class EditReminderViewHolder extends BaseRecycleNoHeaderViewHolder {

    public enum EditReminderPage {
        GENERAL, MESSAGE, TIMING, REPEAT, NOTIFICATION, ALARM
    }

    private TextView title;
    private TintedImageView toggleExpand;
    private FrameLayout container;

    public EditReminderViewHolder(View view, cardClickListener mListener) {
        super(view, mListener);
    }

    @Override
    protected void initItem(View view) {
        container = (FrameLayout) view.findViewById(R.id.container);
        title = (TextView)view.findViewById(R.id.title);
        toggleExpand = (TintedImageView) view.findViewById(R.id.toggle_expand);
    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean expandedUnused) {
        final EditReminderItem item = (EditReminderItem)baseRecycleItem;
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        RelativeLayout view = null;

        switch(item.page) {
            case GENERAL:
                title.setText(AppUtil.getContext().getString(R.string.edit_general));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_general, null );
                EditReminderGeneral general = new EditReminderGeneral(view);
                general.bindItem(item);
                break;
            case MESSAGE:
                title.setText(AppUtil.getContext().getString(R.string.edit_message));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_message, null );
                EditReminderMessage message = new EditReminderMessage(view);
                message.bindItem(item);
                break;
            case TIMING:
                title.setText(AppUtil.getContext().getString(R.string.edit_timing));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_timing, null );
                EditReminderTiming timing = new EditReminderTiming(view);
                timing.bindItem(item);
                break;
            case REPEAT:
                title.setText(AppUtil.getContext().getString(R.string.edit_repeat));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_repeat, null );
                EditReminderRepeat repeat = new EditReminderRepeat(view);
                repeat.bindItem(item);
                break;
            case NOTIFICATION:
                title.setText(AppUtil.getContext().getString(R.string.edit_notification));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_notification, null );
                EditReminderNotification notification = new EditReminderNotification(view);
                notification.bindItem(item);
                break;
            case ALARM:
                title.setText(AppUtil.getContext().getString(R.string.edit_alarm));
                view = (RelativeLayout) inflater.inflate(R.layout.edit_reminder_alarm, null );
                EditReminderAlarm alarm = new EditReminderAlarm(view);
                alarm.bindItem(item);
                break;
        }
        if(view != null) {
            container.addView(view);
        }
        if(item.visible) {
            container.setVisibility(View.VISIBLE);
            ViewHelper.setRotation(toggleExpand, 0);
        } else {
            container.setVisibility(View.GONE);
            ViewHelper.setRotation(toggleExpand, 180);
        }
        toggleExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.visible) {
                    item.visible = false;
                    container.setVisibility(View.GONE);
                    AnimatorControl.rotationAutoStart(toggleExpand, 0, 180, 100, 0, null);
                } else {
                    item.visible = true;
                    container.setVisibility(View.VISIBLE);
                    AnimatorControl.rotationAutoStart(toggleExpand, 180, 0, 100, 0, null);
                }
            }
        });
    }

}
