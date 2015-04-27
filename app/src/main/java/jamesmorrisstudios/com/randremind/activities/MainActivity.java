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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;

/**
 * Primary activity.
 * Handles navigation and lifecycle control.
 *
 * Created by James on 4/20/2015.
 */
public final class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    /**
     * Create this activity
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
        if(!isFragmentDisplayed()) {
            loadMainListFragment();
        }
    }

    /**
     * Activity start
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.v("Main Activity", "On Start");
        //Ensure that the repeating alarm is active.
        Scheduler.getInstance().cancelMidnightAlarm();
        Scheduler.getInstance().scheduleRepeatingMidnight();
    }

    /**
     * Activity resume
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Activity pause
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Activity stop
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.v("Main Activity", "On Stop");
        ReminderList.getInstance().saveData();
        Scheduler.getInstance().cancelNextWake();
        Scheduler.getInstance().scheduleNextWake();
    }

    /**
     * Fragment backstack changed.
     */
    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    /**
     * Check if we are at the top page and show the up button as needed
     */
    public void shouldDisplayHomeUp(){
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(canback);
        }
    }

    /**
     * The up button was pressed so pop one off the backstack
     * @return Always true
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        backPressed();
        return true;
    }

    /**
     * Edit button clicked
     */
    @Override
    public void onEditClicked() {
        loadAddReminderFragment();
    }

    /**
     * Add new button clicked
     */
    @Override
    public void onAddNewClicked() {
        loadAddReminderFragment();
    }

    /**
     * Help button clicked
     */
    @Override
    public void onHelpClicked() {
        loadHelpFragment();
    }

    /**
     * License button clicked
     */
    @Override
    public void onLicenseClicked() {
        loadLicenseFragment();
    }

    /**
     * Tutorial button clicked
     */
    @Override
    public void onTutorialClicked() {
        loadTutorialFragment();
    }

    /**
     * Back from new reminder
     */
    @Override
    public void goBackFromNewReminder() {
        getSupportFragmentManager().popBackStack();
        loadMainListFragment();
    }

}
