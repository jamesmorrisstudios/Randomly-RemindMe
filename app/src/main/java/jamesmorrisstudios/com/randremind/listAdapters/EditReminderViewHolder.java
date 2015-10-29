package jamesmorrisstudios.com.randremind.listAdapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.animator.AnimatorControl;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.controls.TintedImageView;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleViewHolder;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderGeneral;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderMessage;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderNotification;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderRepeat;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderSnooze;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderTiming;

/**
 *
 * Created by James on 6/8/2015.
 */
public class EditReminderViewHolder extends BaseRecycleViewHolder {

    public enum EditReminderPage {
        GENERAL, MESSAGE, TIMING, REPEAT, NOTIFICATION, SNOOZE
    }

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

        switch(item.page) {
            case GENERAL:
                title.setText(AppBase.getContext().getString(R.string.general));
                EditReminderGeneral general = new EditReminderGeneral(view);
                general.bindItem(item);
                break;
            case MESSAGE:
                title.setText(AppBase.getContext().getString(R.string.edit_message));
                EditReminderMessage message = new EditReminderMessage(view);
                message.bindItem(item);
                break;
            case TIMING:
                title.setText(AppBase.getContext().getString(R.string.timing));
                EditReminderTiming timing = new EditReminderTiming(view);
                timing.bindItem(item);
                break;
            case REPEAT:
                title.setText(AppBase.getContext().getString(R.string.repeat));
                EditReminderRepeat repeat = new EditReminderRepeat(view);
                repeat.bindItem(item);
                break;
            case NOTIFICATION:
                title.setText(AppBase.getContext().getString(R.string.notification));
                EditReminderNotification notification = new EditReminderNotification(view);
                notification.bindItem(item);
                break;
            case SNOOZE:
                title.setText(AppBase.getContext().getString(R.string.snooze));
                EditReminderSnooze snooze = new EditReminderSnooze(view);
                snooze.bindItem(item);
                break;
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
