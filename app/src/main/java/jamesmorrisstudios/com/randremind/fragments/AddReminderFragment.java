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

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.PromptDialogRequest;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseRecycleListFragment;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleContainer;

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
public class AddReminderFragment extends BaseRecycleListFragment {
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
                Bus.postObject(new PromptDialogRequest(getString(R.string.cancel_reminder_prompt_title), getString(R.string.cancel_reminder_prompt_content), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ReminderList.getInstance().cancelCurrentReminderChanges();
                                Utils.toastShort(getString(R.string.reminder_cancel));
                                saveOnBack = false;
                                utilListener.goBackFromFragment();
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
            case R.id.action_help:
                Utils.openLink(getResources().getString(com.jamesmorrisstudios.appbaselibrary.R.string.tutorial_link_read));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BaseRecycleAdapter getAdapter(@NonNull BaseRecycleAdapter.OnItemClickListener onItemClickListener) {
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
        View snooze = inflater.inflate(R.layout.edit_reminder_snooze, null);

        ArrayList<BaseRecycleContainer> data = new ArrayList<>();
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.GENERAL, general)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.MESSAGE, message)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.TIMING, timing)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.REPEAT, repeat)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.NOTIFICATION, alert)));
        data.add(new EditReminderContainer(new EditReminderItem("", EditReminderViewHolder.EditReminderPage.SNOOZE, snooze)));
        applyData(data);
    }

    @Override
    protected void startMoreDataLoad() {

    }

    @Override
    protected void itemClick(@NonNull BaseRecycleContainer baseRecycleContainer) {
        //Unused
    }

    protected int getNumberColumns() {
        return getNumberColumnsWide();
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
                Utils.toastShort(getString(R.string.no_changes));
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
                    Utils.toastShort(getString(R.string.no_changes));
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
