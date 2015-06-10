package jamesmorrisstudios.com.randremind.fragments;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.jamesmorrisstudios.appbaselibrary.fragments.BaseRecycleListNoHeaderFragment;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderContainer;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderContainer;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderViewHolder;

/**
 * Created by James on 6/8/2015.
 */
public class AddReminderFragment extends BaseRecycleListNoHeaderFragment {
    public static final String TAG = "AddReminderFragment";
    private OnAddReminderFragmentListener reminderFragmentListener;

    /**
     * @param activity Activity to attach to
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            reminderFragmentListener = (OnAddReminderFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddReminderFragmentListener");
        }
    }

    /**
     * Detach from activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        reminderFragmentListener = null;
    }

    @Override
    protected BaseRecycleNoHeaderAdapter getAdapter(BaseRecycleNoHeaderAdapter.OnItemClickListener onItemClickListener) {
        return new EditReminderAdapter(onItemClickListener);
    }

    @Override
    protected void startDataLoad(boolean b) {
        ArrayList<BaseRecycleNoHeaderContainer> data = new ArrayList<>();

        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.GENERAL)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.MESSAGE)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.TIMING)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.REPEAT)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.NOTIFICATION_ALARM)));

        applyData(data);
    }

    @Override
    protected void itemClick(BaseRecycleNoHeaderContainer baseRecycleContainer) {

    }

    @Override
    public void onBack() {

    }

    @Override
    public boolean showToolbarTitle() {
        return true;
    }

    @Override
    protected void afterViewCreated() {

    }

    /**
     *
     */
    public interface OnAddReminderFragmentListener {

        /**
         *
         */
        void showIconPickerDialog(IconPickerDialogBuilder.IconPickerListener iconPickerListener, int accentColor);

    }

}
