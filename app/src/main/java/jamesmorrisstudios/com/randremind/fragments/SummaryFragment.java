package jamesmorrisstudios.com.randremind.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.FileWriter;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.FileBrowserRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.PromptDialogRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseRecycleListFragment;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleContainer;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.ReminderLogRequest;
import jamesmorrisstudios.com.randremind.listAdapters.SummaryAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.SummaryContainer;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.ReminderLogDay;

/**
 *
 */
public class SummaryFragment extends BaseRecycleListFragment {
    public static final String TAG = "SummaryFragment";
    private OnSummaryListener mListener;

    /**
     * Required empty public constructor
     */
    public SummaryFragment() {
    }

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
        inflater.inflate(R.menu.menu_summary, menu);
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
            case R.id.action_delete:
                Bus.postObject(new PromptDialogRequest(getString(R.string.delete_reminder_prompt_title), getString(R.string.delete_reminder_prompt_content), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.toastShort(getString(R.string.reminder_delete));
                        ReminderList.getInstance().deleteCurrentReminder();
                        ReminderList.getInstance().saveData();
                        utilListener.goBackFromFragment();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }));
                break;
            case R.id.action_preview:
                ReminderList.getInstance().previewCurrent();
                break;
            case R.id.action_duplicate:
                Bus.postObject(new PromptDialogRequest(getString(R.string.duplicate_reminder_prompt_title), getString(R.string.duplicate_reminder_prompt_content), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.toastShort(getString(R.string.reminder_duplicate));
                        ReminderList.getInstance().duplicateReminder();
                        ReminderList.getInstance().saveData();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }));
                break;
            case R.id.action_export:

                String title = getString(R.string.export_location);
                String[] items = new String[] {getString(R.string.share), getString(R.string.file)};

                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                            if(remind == null) {
                                return;
                            }
                            //Share
                            FileWriter.writeFile("RandomlyRemindMe" + "_Log.csv", remind.getReminderLogCsv(), FileWriter.FileLocation.CACHE);
                            Uri uri = FileWriter.getFileUri("RandomlyRemindMe"+"_Log.csv", FileWriter.FileLocation.CACHE);
                            if(uri != null) {
                                Utils.shareStream(getString(R.string.share), uri, "text/csv");
                            }
                        } else {
                            //FIle
                            Bus.postObject(new FileBrowserRequest(FileBrowserRequest.DirType.DIRECTORY, true, null, new FileBrowserRequest.FileBrowserRequestListener() {
                                @Override
                                public void path(@Nullable Uri uri) {
                                    ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                                    if(remind == null) {
                                        return;
                                    }
                                    if(uri != null) {
                                        String path = uri.getPath();
                                        String name = remind.getTitle().replaceAll(" ", "_").replaceAll("\\W+", "");
                                        FileWriter.writeFile(path + File.separator + name + "_Log.csv", remind.getReminderLogCsv(), FileWriter.FileLocation.PATH);
                                        Log.v("FileBrowser", path);
                                        Utils.toastShort(getString(R.string.export_log));
                                    }
                                }
                            }));
                        }
                    }
                }, null));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param inflater           Inflater
     * @param container          Root container
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bus.register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * On view being destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bus.unregister(this);
    }

    @Override
    protected BaseRecycleAdapter getAdapter(@NonNull BaseRecycleAdapter.OnItemClickListener onItemClickListener) {
        return new SummaryAdapter(onItemClickListener);
    }

    @Override
    protected void startDataLoad(boolean forceRefresh) {
        if(!ReminderList.getInstance().hasCurrentReminder()) {
            utilListener.goBackFromFragment();
            return;
        }

        applyItems();
        ReminderItem item = ReminderList.getInstance().getCurrentReminder();
        if (item != null) {
            item.loadData(forceRefresh);
        }
    }

    @Override
    protected void startMoreDataLoad() {

    }

    @Override
    protected void itemClick(@NonNull BaseRecycleContainer baseRecycleContainer) {
        if(baseRecycleContainer.isHeader) {
            mListener.onEditClicked();
        } else {
            ReminderLogDay day = (ReminderLogDay) baseRecycleContainer.getItem();
            if(!day.lifetime) {
                Bus.postObject(new ReminderLogRequest(day));
            }
        }
    }

    @Override
    protected void afterViewCreated() {
        setEnablePullToRefresh(true);
    }

    /**
     * Apply reminder items to this view
     */
    private void applyItems() {
        ReminderItem item = ReminderList.getInstance().getCurrentReminder();
        if (item == null) {
            applyData(null);
        } else {
            ArrayList<BaseRecycleContainer> summaries = new ArrayList<>();
            //Header
            summaries.add(new SummaryContainer(item));
            //Items
            if (item.hasReminderLog()) {
                //Lifetime stats
                ReminderLogDay dayLifetime = new ReminderLogDay(UtilsTime.getDateNow());
                dayLifetime.lifetime = true;
                dayLifetime.timesClickedLifetime = item.getReminderLog().lifetimeClicked;
                dayLifetime.timesShownLifetime = item.getReminderLog().lifetimeShown;
                dayLifetime.timesShownAgainLifetime = item.getReminderLog().lifetimeShownAgain;
                dayLifetime.timesSnoozedLifetime = item.getReminderLog().lifetimeSnoozed;
                summaries.add(new SummaryContainer(dayLifetime));

                for (ReminderLogDay day : item.getReminderLog().days) {
                    summaries.add(new SummaryContainer(day));
                }

            }
            applyData(summaries);
        }
    }

    /**
     * @param event Event listener
     */
    @Subscribe
    public final void onReminderItemEvent(@NonNull ReminderItem.ReminderItemEvent event) {
        switch (event) {
            case DATA_LOAD_PASS:
                applyItems();
                break;
            case DATA_LOAD_FAIL:
                Utils.toastLong(getResources().getString(R.string.data_load_fail));
                applyItems();
                break;
        }
    }

    /**
     * Attach to the activity
     *
     * @param activity Activity to attach to
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSummaryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onSummaryListener");
        }
    }

    /**
     * Detach from activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onBack() {

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

    /**
     *
     */
    public interface OnSummaryListener {

        /**
         * Edit clicked
         */
        void onEditClicked();
    }

}
