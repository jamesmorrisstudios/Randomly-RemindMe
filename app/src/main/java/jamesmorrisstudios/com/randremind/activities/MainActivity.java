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

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.jamesmorrisstudios.appbaselibrary.activities.BaseLauncherActivity;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseMainFragment;

import jamesmorrisstudios.com.randremind.fragments.AddReminderFragment;
import jamesmorrisstudios.com.randremind.fragments.MainListFragment;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.reminder.Scheduler;

/**
 * Primary activity.
 * Handles navigation and lifecycle control.
 *
 * Created by James on 4/20/2015.
 */
public final class MainActivity extends BaseLauncherActivity implements
        MainListFragment.OnFragmentInteractionListener {

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
     * Gets the tutorial fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
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
     * Loads the tutorial fragment into the main view
     */
    protected final void loadAddReminderFragment() {
        AddReminderFragment fragment = getAddReminderFragment();
        loadFragment(fragment, AddReminderFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

}
