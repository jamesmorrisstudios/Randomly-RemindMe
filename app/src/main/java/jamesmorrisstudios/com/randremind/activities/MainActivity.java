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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.jamesmorrisstudios.appbaselibrary.activities.BaseLauncherActivity;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseMainFragment;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.preferences.Prefs;
import com.jamesmorrisstudios.utilitieslibrary.time.TimeItem;
import com.jamesmorrisstudios.utilitieslibrary.time.UtilsTime;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.EditTimesRequest;
import jamesmorrisstudios.com.randremind.dialogHelper.IconPickerRequest;
import jamesmorrisstudios.com.randremind.fragments.AddReminderFragment;
import jamesmorrisstudios.com.randremind.fragments.EditTimesDialog;
import jamesmorrisstudios.com.randremind.fragments.IconPickerDialogBuilder;
import jamesmorrisstudios.com.randremind.fragments.MainListFragment;
import jamesmorrisstudios.com.randremind.fragments.SummaryFragment;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
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
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        if (extras.containsKey("REMINDER") && extras.containsKey("NAME")) {
            if (!ReminderList.getInstance().hasReminders()) {
                ReminderList.getInstance().loadDataSync();
                if (!ReminderList.getInstance().hasReminders()) {
                    clearBackStack();
                    loadMainFragment();
                    getIntent().removeExtra("REMINDER");
                    getIntent().removeExtra("NAME");
                    return;
                }
            }
            Log.v("Main Activity", "Intent received to go to reminder");
            ReminderList.getInstance().setCurrentReminder(extras.getString("NAME"));
            clearBackStack();
            loadSummaryFragment();
            getIntent().removeExtra("REMINDER");
            getIntent().removeExtra("NAME");
        }

        if (!ReminderList.getInstance().hasReminders()) {
            clearBackStack();
            loadMainFragment();
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
    }

    /**
     * Activity stop
     */
    @Override
    public void onStop() {
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

    @Subscribe
    public void onIconPickerRequest(@NonNull IconPickerRequest request) {
        showIconPickerDialog(request.iconPickerListener, request.accentColor);
    }

    public void showIconPickerDialog(@NonNull IconPickerDialogBuilder.IconPickerListener iconPickerListener, int accentColor) {
        IconPickerDialogBuilder.with(this)
                .setTitle(getResources().getString(R.string.chooseIcon))
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
