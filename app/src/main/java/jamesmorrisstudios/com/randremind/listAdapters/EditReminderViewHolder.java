package jamesmorrisstudios.com.randremind.listAdapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleViewHolder;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.BaseEditReminder;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderGeneral;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderMessage;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderNotification;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderCriteria;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderSnooze;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderTiming;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderTrigger;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.util.RemindUtils;

/**
 *
 * Created by James on 6/8/2015.
 */
public class EditReminderViewHolder extends BaseRecycleViewHolder {

    public enum EditReminderPage {
        GENERAL, MESSAGE, TIMING, CRITERIA, TRIGGER, NOTIFICATION, SNOOZE
    }

    private BaseEditReminder baseView = null;

    private TextView title;
    private FrameLayout container;

    public EditReminderViewHolder(View view, boolean isHeader, boolean isDummyItem, cardClickListener mListener) {
        super(view, isHeader, isDummyItem, mListener);
    }

    @Override
    protected void initHeader(View view) {

    }

    @Override
    protected void initItem(View view) {
        container = (FrameLayout) view.findViewById(R.id.container);
        title = (TextView)view.findViewById(R.id.title);
    }

    @Override
    protected void bindHeader(BaseRecycleItem baseRecycleItem, boolean b) {

    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean expandedUnused) {
        final EditReminderItem item = (EditReminderItem)baseRecycleItem;
        container.removeAllViews();
        View view = item.pageView;

        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind == null) {
            return;
        }

        switch(item.page) {
            case GENERAL:
                title.setText(AppBase.getContext().getString(R.string.general));
                baseView = new EditReminderGeneral(view);
                break;
            case MESSAGE:
                title.setText(AppBase.getContext().getString(R.string.edit_message));
                baseView = new EditReminderMessage(view);
                break;
            case TIMING:
                title.setText(AppBase.getContext().getString(R.string.timing));
                baseView = new EditReminderTiming(view);
                break;
            case CRITERIA:
                title.setText(AppBase.getContext().getString(R.string.criteria));
                baseView = new EditReminderCriteria(view);
                break;
            case TRIGGER:
                title.setText(AppBase.getContext().getString(R.string.triggers));
                baseView = new EditReminderTrigger(view);
                break;
            case NOTIFICATION:
                title.setText(AppBase.getContext().getString(R.string.notification));
                baseView = new EditReminderNotification(view);
                break;
            case SNOOZE:
                title.setText(AppBase.getContext().getString(R.string.snooze));
                baseView = new EditReminderSnooze(view);
                break;
        }
        if(RemindUtils.alwaysShowAdvanced()) {
            baseView.bindItem(item, true);
        } else {
            baseView.bindItem(item, remind.isShowAdvanced());
        }
        if(view != null) {
            if(view.getParent() != null && view.getParent() instanceof ViewGroup) {
                Log.v("EditViewHolder", "View has parent. Removing");
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            container.addView(view);
        }
    }

}
