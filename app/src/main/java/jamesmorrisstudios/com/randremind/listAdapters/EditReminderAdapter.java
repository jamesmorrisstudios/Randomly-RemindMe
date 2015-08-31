package jamesmorrisstudios.com.randremind.listAdapters;

import android.support.annotation.NonNull;
import android.view.View;

import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleViewHolder;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 6/8/2015.
 */
public class EditReminderAdapter extends BaseRecycleAdapter {

    public EditReminderAdapter(OnItemClickListener mListener) {
        super(mListener);
    }

    @Override
    protected BaseRecycleViewHolder getViewHolder(@NonNull View view, boolean isHeader, boolean isDummyItem, BaseRecycleViewHolder.cardClickListener cardClickListener) {
        return new EditReminderViewHolder(view, isHeader, isDummyItem, cardClickListener);
    }

    @Override
    protected int getItemResId() {
        return R.layout.edit_reminder_line_item;
    }

    @Override
    protected int getHeaderResId() {
        return 0;
    }

}
