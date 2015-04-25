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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.fragments.AddReminderFragment;
import jamesmorrisstudios.com.randremind.fragments.HelpFragment;
import jamesmorrisstudios.com.randremind.fragments.LicenseFragment;
import jamesmorrisstudios.com.randremind.fragments.MainListFragment;
import jamesmorrisstudios.com.randremind.fragments.TutorialFragment;

/**
 * Base activity with helper functions to assist in navigation and utility to the MainActivity
 *
 * Created by James on 4/20/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        MainListFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        AddReminderFragment.OnFragmentInteractionListener {

    /**
     * @return True if the tutorial fragment is in the fragment manager
     */
    protected final boolean existsTutorialFragment() {
        return getSupportFragmentManager().findFragmentByTag(TutorialFragment.TAG) != null;
    }

    /**
     * Gets the tutorial fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     * @return The fragment
     */
    @NonNull
    protected final TutorialFragment getTutorialFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TutorialFragment fragment = (TutorialFragment) fragmentManager.findFragmentByTag(TutorialFragment.TAG);
        if (fragment == null) {
            fragment = new TutorialFragment();
        }
        return fragment;
    }

    /**
     * Loads the tutorial fragment into the main view
     */
    protected final void loadTutorialFragment() {
        TutorialFragment fragment = getTutorialFragment();
        loadFragment(fragment, TutorialFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * @return True if the license fragment is in the fragment manager
     */
    protected final boolean existsLicenseFragment() {
        return getSupportFragmentManager().findFragmentByTag(LicenseFragment.TAG) != null;
    }

    /**
     * Gets the license fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     * @return The fragment
     */
    @NonNull
    protected final LicenseFragment getLicenseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LicenseFragment fragment = (LicenseFragment) fragmentManager.findFragmentByTag(LicenseFragment.TAG);
        if (fragment == null) {
            fragment = new LicenseFragment();
        }
        return fragment;
    }

    /**
     * Loads the lisense fragment into the main view
     */
    protected final void loadLicenseFragment() {
        LicenseFragment fragment = getLicenseFragment();
        loadFragment(fragment, LicenseFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * @return True if the help fragment is in the fragment manager
     */
    protected final boolean existsHelpFragment() {
        return getSupportFragmentManager().findFragmentByTag(HelpFragment.TAG) != null;
    }

    /**
     * Gets the help fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     * @return The fragment
     */
    @NonNull
    protected final HelpFragment getHelpFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HelpFragment fragment = (HelpFragment) fragmentManager.findFragmentByTag(HelpFragment.TAG);
        if (fragment == null) {
            fragment = new HelpFragment();
        }
        return fragment;
    }

    /**
     * Loads the help fragment into the main view
     */
    protected final void loadHelpFragment() {
        HelpFragment fragment = getHelpFragment();
        loadFragment(fragment, HelpFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * @return True if the add reminder fragment is in the fragment manager
     */
    protected final boolean existsAddReminderFragment() {
        return getSupportFragmentManager().findFragmentByTag(AddReminderFragment.TAG) != null;
    }

    /**
     * Gets the add reminder fragment from the fragment manager.
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
     * Loads the add reminder fragment into the main view
     */
    protected final void loadAddReminderFragment() {
        AddReminderFragment fragment = getAddReminderFragment();
        loadFragment(fragment, AddReminderFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * @return True if the main list fragment is in the fragment manager
     */
    protected final boolean existsMainListFragment() {
        return getSupportFragmentManager().findFragmentByTag(MainListFragment.TAG) != null;
    }

    /**
     * Gets the main list fragment from the fragment manager.
     * Creates the fragment if it does not exist yet.
     * @return The fragment
     */
    @NonNull
    protected final MainListFragment getMainListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainListFragment fragment = (MainListFragment) fragmentManager.findFragmentByTag(MainListFragment.TAG);
        if (fragment == null) {
            fragment = new MainListFragment();
        }
        return fragment;
    }

    /**
     * Loads the main list fragment into the main view
     */
    protected final void loadMainListFragment() {
        MainListFragment fragment = getMainListFragment();
        loadFragment(fragment, MainListFragment.TAG, false);
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Loads the given fragment into the container view.
     * @param fragment Fragment to add
     * @param tag Tag to give the fragment
     * @param addBackStack True to add to backstack for back and up navigation
     */
    private void loadFragment(@NonNull Fragment fragment, @NonNull String tag, boolean addBackStack) {
        if (!isFragmentUIActive(fragment)) {
            if(addBackStack) {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(tag)
                        .replace(R.id.container, fragment, tag)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.container, fragment, tag)
                        .commit();
            }
        }
    }

    /**
     * Checks if the UI of a fragment is active
     * @param fragment Fragment to check
     * @return True if fragment is added and visible
     */
    private boolean isFragmentUIActive(@NonNull Fragment fragment) {
        return fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving();
    }

    /**
     * Checks if any fragment is currently visible
     * @return True if a fragment is currently being displayed
     */
    protected final boolean isFragmentDisplayed() {
        return (existsHelpFragment() && isFragmentUIActive(getHelpFragment()))
                || (existsAddReminderFragment() && isFragmentUIActive(getAddReminderFragment()))
                || (existsMainListFragment() && isFragmentUIActive(getMainListFragment()))
                || (existsTutorialFragment() && isFragmentUIActive(getTutorialFragment()))
                || (existsLicenseFragment() && isFragmentUIActive(getLicenseFragment()));
    }

    /**
     * Call this when back was pressed
     */
    protected final void backPressed() {
        if(isFragmentUIActive(getAddReminderFragment())) {
            getAddReminderFragment().onBack();
        }
    }

    /**
     * Signal to fragments that need it that the use clicked back and is leaving them
     */
    @Override
    public void onBackPressed() {
        backPressed();
        super.onBackPressed();
    }

    /**
     * Hides the keyboards if visible
     */
    @Override
    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Detect if button press on the toolbar
     * @param item Menu item clicked
     * @return True if consumed.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a time picker dialog
     * @param listener Return listener
     * @param hour Start hour
     * @param minute Start minute
     * @param is24Hour True if 24 hour mode
     */
    @Override
    public void createTimePickerDialog(@NonNull TimePickerDialog.OnTimeSetListener listener, int hour, int minute, boolean is24Hour) {
        TimePickerDialog time = new TimePickerDialog();
        time.initialize(listener, hour, minute, is24Hour);
        time.show(getFragmentManager(), "TimePickerDialog");
    }

    public void createPromptDialog(@NonNull String title, @NonNull String content, MaterialDialog.ButtonCallback callback) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .callback(callback)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

}
