package jamesmorrisstudios.com.randremind.listAdapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jamesmorrisstudios.com.randremind.R;

/**
 * Achievement view holder for use in RecyclerView
 */
public final class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //Type of view. header or normal
    private boolean isHeader;
    //Click listener
    public cardClickListener mListener;
    //Header
    private TextView headerTitle;
    //Not Header
    private TextView title;

    /**
     * COnstructor
     * @param view Parent view
     * @param isHeader True if header item, false for normal
     * @param mListener Click listener. Null if none desired
     */
    public ReminderViewHolder(@NonNull View view, boolean isHeader, @Nullable cardClickListener mListener) {
        super(view);
        this.isHeader = isHeader;
        this.mListener = mListener;

        if(isHeader) {
            headerTitle = (TextView) view.findViewById(R.id.reminder_main_text);
        } else {
            CardView topLayout = (CardView) view.findViewById(R.id.reminder_card);
            title = (TextView) view.findViewById(R.id.reminder_title_text);
            topLayout.setOnClickListener(this);
        }
    }

    /**
     * Binds the given data to this view.
     * @param reminder Data to bind
     */
    public void bindItem(@NonNull ReminderContainer reminder) {
        if(isHeader) {
            //Header only
            this.headerTitle.setText(reminder.headerTitle);
        } else {
            //Common non header
            this.title.setText(reminder.item.title);
        }
    }

    /**
     * @param v The view that was clicked
     */
    @Override
    public void onClick(@NonNull View v) {
        if(mListener != null) {
            mListener.cardClicked(v, getLayoutPosition());
        }
    }

    /**
     * Card click listener interface
     */
    public  interface cardClickListener {
        void cardClicked(@NonNull View caller, int position);
    }

}
