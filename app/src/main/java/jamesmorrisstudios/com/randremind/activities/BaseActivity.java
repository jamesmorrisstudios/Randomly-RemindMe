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

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

    protected final TutorialFragment getTutorialFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TutorialFragment fragment = (TutorialFragment) fragmentManager.findFragmentByTag(TutorialFragment.TAG);
        if (fragment == null) {
            fragment = new TutorialFragment();
        }
        return fragment;
    }

    protected final void loadTutorialFragment() {
        TutorialFragment fragment = getTutorialFragment();
        loadFragment(fragment, TutorialFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    protected final LicenseFragment getLicenseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LicenseFragment fragment = (LicenseFragment) fragmentManager.findFragmentByTag(LicenseFragment.TAG);
        if (fragment == null) {
            fragment = new LicenseFragment();
        }
        return fragment;
    }

    protected final void loadLicenseFragment() {
        LicenseFragment fragment = getLicenseFragment();
        loadFragment(fragment, LicenseFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    protected final HelpFragment getHelpFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HelpFragment fragment = (HelpFragment) fragmentManager.findFragmentByTag(HelpFragment.TAG);
        if (fragment == null) {
            fragment = new HelpFragment();
        }
        return fragment;
    }

    protected final void loadHelpFragment() {
        HelpFragment fragment = getHelpFragment();
        loadFragment(fragment, HelpFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    protected final AddReminderFragment getAddReminderFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddReminderFragment fragment = (AddReminderFragment) fragmentManager.findFragmentByTag(AddReminderFragment.TAG);
        if (fragment == null) {
            fragment = new AddReminderFragment();
        }
        return fragment;
    }

    protected final void loadAddReminderFragment() {
        AddReminderFragment fragment = getAddReminderFragment();
        loadFragment(fragment, AddReminderFragment.TAG, true);
        getSupportFragmentManager().executePendingTransactions();
    }

    protected final MainListFragment getMainListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainListFragment fragment = (MainListFragment) fragmentManager.findFragmentByTag(MainListFragment.TAG);
        if (fragment == null) {
            fragment = new MainListFragment();
        }
        return fragment;
    }

    protected final void loadMainListFragment() {
        MainListFragment fragment = getMainListFragment();
        loadFragment(fragment, MainListFragment.TAG, false);
        getSupportFragmentManager().executePendingTransactions();
    }

    private void loadFragment(Fragment fragment, String tag, boolean addBackStack) {
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

    private boolean isFragmentUIActive(Fragment fragment) {
        return fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving();
    }

    protected final boolean isFragmentDisplayed() {
        return isFragmentUIActive(getHelpFragment())
                || isFragmentUIActive(getAddReminderFragment())
                || isFragmentUIActive(getMainListFragment())
                || isFragmentUIActive(getTutorialFragment())
                || isFragmentUIActive(getLicenseFragment());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
