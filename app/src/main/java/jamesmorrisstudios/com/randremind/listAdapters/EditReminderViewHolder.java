package jamesmorrisstudios.com.randremind.listAdapters;

import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderViewHolder;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderViewHolder extends BaseRecycleNoHeaderViewHolder {

    public enum EditReminderPage {
        GENERAL, MESSAGE, TIMING, REPEAT, NOTIFICATION_ALARM
    }

    private TextView title;
    private ViewFlipper viewFlipper;


    public EditReminderViewHolder(View view, cardClickListener mListener) {
        super(view, mListener);
    }

    @Override
    protected void initItem(View view) {
        title = (TextView)view.findViewById(R.id.title);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);
    }

    @Override
    protected void bindItem(BaseRecycleItem baseRecycleItem, boolean b) {
        EditReminderItem item = (EditReminderItem)baseRecycleItem;
        viewFlipper.setDisplayedChild(item.page.ordinal());
        switch(item.page) {
            case GENERAL:
                title.setText("General");
                break;
            case MESSAGE:
                title.setText("Message");
                break;
            case TIMING:
                title.setText("Timing");
                break;
            case REPEAT:
                title.setText("Repeat");
                break;
            case NOTIFICATION_ALARM:
                title.setText("Notification");
                break;
        }

    }

}
