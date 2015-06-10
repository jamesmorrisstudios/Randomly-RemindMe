package jamesmorrisstudios.com.randremind.listAdapters;

import android.view.View;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderViewHolder;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderAdapter extends BaseRecycleNoHeaderAdapter {

    public EditReminderAdapter(OnItemClickListener mListener) {
        super(mListener);
    }

    @Override
    protected BaseRecycleNoHeaderViewHolder getViewHolder(View view, BaseRecycleNoHeaderViewHolder.cardClickListener cardClickListener) {
        return new EditReminderViewHolder(view, cardClickListener);
    }

    @Override
    protected int getItemResId() {
        return R.layout.edit_reminder_line_item;
    }

}
