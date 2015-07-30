package jamesmorrisstudios.com.randremind.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.PromptDialogRequest;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseRecycleListNoHeaderFragment;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleNoHeaderContainer;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.Utils;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.editReminder.EditReminderItem;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderContainer;
import jamesmorrisstudios.com.randremind.listAdapters.EditReminderViewHolder;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 6/8/2015.
 */
public class AddReminderFragment extends BaseRecycleListNoHeaderFragment {
    public static final String TAG = "AddReminderFragment";
    private boolean saveOnBack = true;

    /**
     * Constructor. Enable menu options
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Setup the toolbar options menu
     *
     * @param menu     Menu
     * @param inflater Inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle toolbar menu button clicks
     *
     * @param item Selected reminder
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                Bus.postObject(new PromptDialogRequest(getString(R.string.cancel_prompt_title), getString(R.string.cancel_prompt_content), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ReminderList.getInstance().cancelCurrentReminderChanges();
                                saveOnBack = false;
                                utilListener.goBackFromFragment();
                                Utils.toastShort(getString(R.string.reminder_cancel));
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Nothing on negative
                            }
                        }));
                break;
            case R.id.action_preview:
                ReminderList.getInstance().previewCurrent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BaseRecycleNoHeaderAdapter getAdapter(@NonNull BaseRecycleNoHeaderAdapter.OnItemClickListener onItemClickListener) {
        return new EditReminderAdapter(onItemClickListener);
    }

    @Override
    protected void startDataLoad(boolean b) {
        if(!ReminderList.getInstance().hasCurrentReminder()) {
            utilListener.goBackFromFragment();
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View general = inflater.inflate(R.layout.edit_reminder_general, null);
        View message = inflater.inflate(R.layout.edit_reminder_message, null);
        View timing = inflater.inflate(R.layout.edit_reminder_timing, null);
        View repeat = inflater.inflate(R.layout.edit_reminder_repeat, null);
        View alert = inflater.inflate(R.layout.edit_reminder_notification, null);

        ArrayList<BaseRecycleNoHeaderContainer> data = new ArrayList<>();
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.GENERAL, general)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.MESSAGE, message)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.TIMING, timing)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.REPEAT, repeat)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.NOTIFICATION, alert)));
        applyData(data);
    }

    @Override
    protected void itemClick(@NonNull BaseRecycleNoHeaderContainer baseRecycleContainer) {
        //Unused
    }

    /**
     * On Stop
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.v("Add Reminder Fragment", "On Stop");
        //If any reminders are currently open save them
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if(remind != null) {
            if(ReminderList.getInstance().saveCurrentReminder()) {
                Utils.toastShort(getString(R.string.reminder_save));
            } else {
                //Utils.toastShort(getString(R.string.reminder_no_changes));
            }
        }
    }

    @Override
    public void onBack() {
        if(saveOnBack) {
            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
            if(remind != null) {
                if(ReminderList.getInstance().saveCurrentReminder()) {
                    Utils.toastShort(getString(R.string.reminder_save));
                } else {
                    //Utils.toastShort(getString(R.string.reminder_no_changes));
                }
            }
        }
        utilListener.hideKeyboard();
    }

    @Override
    public boolean showToolbarTitle() {
        return true;
    }

    @Override
    protected void saveState(Bundle bundle) {

    }

    @Override
    protected void restoreState(Bundle bundle) {

    }

    @Override
    protected void afterViewCreated() {

    }

}
