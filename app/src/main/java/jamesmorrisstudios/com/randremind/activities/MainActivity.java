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

package jamesmorrisstudios.com.randremind.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.activities.BaseLauncherActivity;
import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceIconRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogs.SingleChoiceIconDialogBuilder;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseMainFragment;
import com.jamesmorrisstudios.appbaselibrary.preferences.Prefs;
import com.jamesmorrisstudios.appbaselibrary.time.TimeItem;
import com.jamesmorrisstudios.appbaselibrary.time.UtilsTime;
import com.squareup.otto.Subscribe;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.EditTimesRequest;
import jamesmorrisstudios.com.randremind.dialogHelper.ExportReminderLogRequest;
import jamesmorrisstudios.com.randremind.dialogHelper.IconPickerRequest;
import jamesmorrisstudios.com.randremind.dialogHelper.ReminderLogRequest;
import jamesmorrisstudios.com.randremind.fragments.AddReminderFragment;
import jamesmorrisstudios.com.randremind.fragments.BackupRestoreFragment;
import jamesmorrisstudios.com.randremind.fragments.EditTimesDialog;
import jamesmorrisstudios.com.randremind.fragments.IconPickerDialogBuilder;
import jamesmorrisstudios.com.randremind.fragments.MainListFragment;
import jamesmorrisstudios.com.randremind.fragments.ReminderLogDialog;
import jamesmorrisstudios.com.randremind.fragments.SummaryFragment;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.ReminderLogDay;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;

/**
 * Primary activity.
 * Handles navigation and lifecycle control.
 * <p/>
 * Created by James on 4/20/2015.
 */
public final class MainActivity extends BaseLauncherActivity implements
        MainListFragment.OnFragmentInteractionListener,
        SummaryFragment.OnSummaryListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        processIntents();
        //Reload the main page if the reminder singleton was GC
        if (!ReminderList.getInstance().hasReminders()) {
            clearForOpen(getIntent());
            loadMainFragment();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        processIntents();
    }

    private void processIntents() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        String type = intent.getType();
        //If we are opening from a reminder notification click go straight to that reminder page
        if (intent.hasExtra("REMINDER") && intent.hasExtra("NAME")) {
            if(loadReminderListSync(intent)) {
                Log.v("Main Activity", "Intent received to go to reminder");
                ReminderList.getInstance().setCurrentReminder(intent.getStringExtra("NAME"));
                clearForOpen(intent);
                loadSummaryFragment();
            }
        } else if((Intent.ACTION_SEND.equals(action) || Intent.ACTION_VIEW.equals(action)) && type != null) {
            Log.v("MainActivity", "Shared text "+type);
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if(uri == null) {
                uri = intent.getData();
            }
            if(uri != null) {
                clearForOpen(intent);
                loadBackupRestoreFragment(uri);
            }
        }
    }

    private boolean loadReminderListSync(Intent intent) {
        //Load the reminder list if not already open
        if (!ReminderList.getInstance().hasReminders()) {
            ReminderList.getInstance().loadDataSync();
            if (!ReminderList.getInstance().hasReminders()) {
                clearForOpen(intent);
                loadMainFragment();
                return false;
            }
        }
        return true;
    }

    private void clearForOpen(Intent intent) {
        clearBackStack();
        if(intent != null) {
            intent.removeExtra("REMINDER");
            intent.removeExtra("NAME");
        }
    }

    /**
     * Activity start
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.v("Main Activity", "On Start");
        Bus.register(this);
        //Ensure that the repeating alarm is active.
        Scheduler.getInstance().cancelMidnightAlarm();
        Scheduler.getInstance().scheduleRepeatingMidnight();
        if(isFirstLaunch()) {
            promptNotificationTheme();
        }
    }

    /**
     * Activity stop
     */
    @Override
    public void onStop() {
        ReminderList.getInstance().waitOnTasks();
        super.onStop();
        Log.v("Main Activity", "On Stop");
        Bus.unregister(this);
        //Save the reminder list back to storage
        ReminderList.getInstance().saveDataSync();
    }

    /**
     * Called on settings change event
     */
    @Override
    public void onSettingsChanged() {
        super.onSettingsChanged();
        int firstDay = Prefs.getInt(getString(R.string.settings_pref), getString(R.string.pref_notification_first_day), 0);
        switch(firstDay) {
            case 0:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.AUTOMATIC);
                break;
            case 1:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.SUNDAY);
                break;
            case 2:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.MONDAY);
                break;
            case 3:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.TUESDAY);
                break;
            case 4:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.WEDNESDAY);
                break;
            case 5:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.THURSDAY);
                break;
            case 6:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.FRIDAY);
                break;
            case 7:
                UtilsTime.setFirstDayOfWeek(UtilsTime.DayOfWeek.SATURDAY);
                break;
        }
    }

    /**
     * The fragment is changing. This is called right after the fragment is notified
     */
    @Override
    protected void onFragmentChangeStart() {

    }

    /**
     * The fragment was just changed
     */
    @Override
    protected void onFragmentChangeEnd() {

    }

    @Override
    protected void onBackToHome() {

    }

    private void promptNotificationTheme() {
        String title = getString(R.string.notification_theme_title);
        int[] items = new int[] {R.drawable.light_back, R.drawable.dark_back};
        Bus.postObject(new SingleChoiceIconRequest(title, items, new SingleChoiceIconDialogBuilder.OptionPickerListener() {
            @Override
            public void onClick(int which) {
                String pref = AppBase.getContext().getString(R.string.settings_pref);
                String keytheme = AppBase.getContext().getString(R.string.pref_notification_theme);
                if(which == 0) {
                    Prefs.putBoolean(pref, keytheme, true);
                } else {
                    Prefs.putBoolean(pref, keytheme, false);
                }
                setFirstLaunchComplete();
            }
        }));
    }

    private boolean isFirstLaunch() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String keyFirstLaunch = AppBase.getContext().getString(R.string.pref_first_launch);
        return Prefs.getBoolean(pref, keyFirstLaunch, true);
    }

    private void setFirstLaunchComplete() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String keyFirstLaunch = AppBase.getContext().getString(R.string.pref_first_launch);
        Prefs.putBoolean(pref, keyFirstLaunch, false);
    }

    /**
     * Reminder item clicked
     */
    @Override
    public void onReminderItemClicked() {
        loadSummaryFragment();
    }

    /**
     * Add new button clicked
     */
    @Override
    public void onAddNewClicked() {
        ReminderList.getInstance().createNewReminder();
        loadSummaryFragment();
        loadAddReminderFragment();
    }

    @Override
    public void onBackupRestoreClicked() {
        loadBackupRestoreFragment(null);
    }

    /**
     * Edit button clicked
     */
    @Override
    public void onEditClicked() {
        loadAddReminderFragment();
    }

    @Override
    @NonNull
    protected BaseFragment getMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainListFragment fragment = (MainListFragment) fragmentManager.findFragmentByTag(BaseMainFragment.TAG);
        if (fragment == null) {
            fragment = new MainListFragment();
        }
        return fragment;
    }

    /**
     * Gets the add reminder fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     *
     * @return The fragment
     */
    @NonNull
    protected final AddReminderFragment getAddReminderFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddReminderFragment fragment = (AddReminderFragment) fragmentManager.findFragmentByTag(AddReminderFragment.TAG);
        if (fragment == null) {
            fragment = new AddReminderFragment();
        }
        return fragment;
    }

    /**
     * Loads the add reminder fragment into the main view
     */
    protected final void loadAddReminderFragment() {
        AddReminderFragment fragment = getAddReminderFragment();
        loadFragment(fragment, AddReminderFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Gets the summary fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     *
     * @return The fragment
     */
    @NonNull
    protected final SummaryFragment getSummaryFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SummaryFragment fragment = (SummaryFragment) fragmentManager.findFragmentByTag(SummaryFragment.TAG);
        if (fragment == null) {
            fragment = new SummaryFragment();
        }
        return fragment;
    }

    /**
     * Loads the summary fragment into the main view
     */
    protected final void loadSummaryFragment() {
        SummaryFragment fragment = getSummaryFragment();
        loadFragment(fragment, SummaryFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     *
     * Creates the fragment if it does not exist yet.
     *
     * @return The fragment
     */
    @NonNull
    protected final BackupRestoreFragment getBackupRestoreFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        BackupRestoreFragment fragment = (BackupRestoreFragment) fragmentManager.findFragmentByTag(BackupRestoreFragment.TAG);
        if (fragment == null) {
            fragment = new BackupRestoreFragment();
        }
        return fragment;
    }

    /**
     *
     */
    protected final void loadBackupRestoreFragment(@Nullable Uri path) {
        BackupRestoreFragment fragment = getBackupRestoreFragment();
        fragment.setPath(path);
        loadFragment(fragment, BackupRestoreFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    @Subscribe
    public void onIconPickerRequest(@NonNull IconPickerRequest request) {
        showIconPickerDialog(request.iconPickerListener, request.accentColor);
    }

    @Subscribe
    public void onReminderLogRequest(@NonNull ReminderLogRequest request) {
        showReminderLogDialog(request.reminderLogDay);
    }

    public void showReminderLogDialog(ReminderLogDay reminderLogDay) {
        FragmentManager fm = getSupportFragmentManager();
        ReminderLogDialog reminderLogDialog = new ReminderLogDialog();
        reminderLogDialog.setData(reminderLogDay);
        reminderLogDialog.show(fm, "fragment_reminder_log");
    }

    public void showIconPickerDialog(@NonNull IconPickerDialogBuilder.IconPickerListener iconPickerListener, int accentColor) {
        IconPickerDialogBuilder.with(this)
                .setTitle(getResources().getString(R.string.choose_icon))
                .setAccentColor(accentColor)
                .setOnIconPicked(iconPickerListener)
                .build()
                .show();
    }

    @Subscribe
    public void onEditTimesRequest(@NonNull EditTimesRequest request) {
        showEditTimesDialog(request.times, request.onPositive, request.onNegative, request.allowEdit);
    }

    public void showEditTimesDialog(@NonNull ArrayList<TimeItem> times, @NonNull EditTimesDialog.EditTimesListener onPositive, @Nullable View.OnClickListener onNegative, boolean allowEdit) {
        FragmentManager fm = getSupportFragmentManager();
        EditTimesDialog editTimesDialog = new EditTimesDialog();
        editTimesDialog.setData(times, onPositive, onNegative, allowEdit);
        editTimesDialog.show(fm, "fragment_edit_times");
    }

}
