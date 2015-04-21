/*
 * Copyright (c) 2015.  James Morris Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jamesmorrisstudios.com.randremind.listAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * Achievements adapter class to manage the recyclerView
 */
public final class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder> {
    public static final String TAG = "ReminderAdapter";

    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;
    private ReminderItemClickListener mListener;
    private final ArrayList<LineItem> mItems;
    private int mHeaderDisplay;
    private boolean mMarginsFixed;

    /**
     * Constructor
     * @param headerMode Header mode to use
     * @param mListener Item Click listener
     */
    public ReminderAdapter(int headerMode, @NonNull ReminderItemClickListener mListener) {
        this.mListener = mListener;
        mHeaderDisplay = headerMode;
        mItems = new ArrayList<>();
    }

    /**
     * Sets the items to this list
     * This will override anything previously set
     * @param items List of items to set.
     */
    public final void setItems(@NonNull ArrayList<ReminderContainer> items) {
        while(!mItems.isEmpty()) {
            mItems.remove(0);
        }
        for(int i=0; i<items.size(); i++) {
            mItems.add(new LineItem(0, 0, items.get(i)));
        }
        notifyDataSetChanged();
    }

    /**
     * Creates a view holder for one of the item views
     * @param parent Parent view
     * @param viewType Type of view
     * @return The achievement view holder
     */
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        boolean isHeader;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_header_item, parent, false);
            isHeader = true;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_line_item, parent, false);
            isHeader = false;
        }
        return new ReminderViewHolder(view, isHeader, new ReminderViewHolder.cardClickListener() {
            @Override
            public void cardClicked(@NonNull View caller, int position) {
                mListener.itemClicked(mItems.get(position).reminder);
            }
        });
    }

    /**
     * Achievement click interface
     */
    public interface ReminderItemClickListener {
         void itemClicked(ReminderContainer item);
    }

    /**
     * Binds a view holder data set in the given position
     * @param holder Holder to bind
     * @param position Position
     */
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;
        //Bind the data
        holder.bindItem(item.reminder);

        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.reminder.isHeader) {
            lp.headerDisplay = mHeaderDisplay;
            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }
        lp.setSlm(getSectionLayoutManager());
        lp.setNumColumns(getNumberColumns());
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    /**
     * @return Number of columns to show
     */
    private int getNumberColumns() {
        switch (Utils.getOrientation()) {
            case PORTRAIT:
                switch (Utils.getScreenSize()) {
                    case SMALL:
                        return 1;
                    case NORMAL:
                        return 2;
                    case LARGE:
                        return 2;
                    case XLARGE:
                        return 2;
                    case UNDEFINED:
                        return 1;
                    default:
                        return 1;
                }
            case LANDSCAPE:
                switch (Utils.getScreenSize()) {
                    case SMALL:
                        return 1;
                    case NORMAL:
                        return 2;
                    case LARGE:
                        return 2;
                    case XLARGE:
                        return 3;
                    case UNDEFINED:
                        return 2;
                    default:
                        return 2;
                }
        }
        return 1;
    }

    /**
     * @return The section layout manager to use (linear or grid)
     */
    private int getSectionLayoutManager() {
        switch (Utils.getOrientation()) {
            case PORTRAIT:
                switch (Utils.getScreenSize()) {
                    case SMALL:
                        return LinearSLM.ID;
                    case NORMAL:
                        return LinearSLM.ID;
                    case LARGE:
                        return GridSLM.ID;
                    case XLARGE:
                        return GridSLM.ID;
                    case UNDEFINED:
                        return LinearSLM.ID;
                }
                break;
            case LANDSCAPE:
                switch (Utils.getScreenSize()) {
                    case SMALL:
                        return GridSLM.ID;
                    case NORMAL:
                        return GridSLM.ID;
                    case LARGE:
                        return GridSLM.ID;
                    case XLARGE:
                        return GridSLM.ID;
                    case UNDEFINED:
                        return LinearSLM.ID;
                }
                break;
        }
        return GridSLM.ID;
    }

    /**
     * @param position Position of item
     * @return View type (header or content)
     */
    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).reminder.isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    /**
     * @return Number of items in view holder
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * @param headerDisplay Set the header display type
     */
    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    /**
     * @param marginsFixed Set margin fixed state
     */
    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    /**
     * Notify of header update changes
     */
    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            LineItem item = mItems.get(i);
            if (item.reminder.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    /**
     * Individual line item for each item in recyclerView. These are recycled.
     */
    private static class LineItem {
        public int sectionManager;
        public int sectionFirstPosition;
        public ReminderContainer reminder;

        /**
         * Constructor
         * @param sectionManager Section manager
         * @param sectionFirstPosition First position in section
         * @param reminder Achievement line item data
         */
        public LineItem(int sectionManager, int sectionFirstPosition, @NonNull ReminderContainer reminder) {
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
            this.reminder = reminder;
        }
    }
}
