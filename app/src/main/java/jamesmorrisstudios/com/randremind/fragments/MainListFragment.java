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

package jamesmorrisstudios.com.randremind.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseMainRecycleListFragment;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleAdapter;
import com.jamesmorrisstudios.appbaselibrary.listAdapters.BaseRecycleContainer;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.listAdapters.ReminderAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.ReminderContainer;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemSummary;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * The primary list fragment showing all existing reminders
 */
public final class MainListFragment extends BaseMainRecycleListFragment {
    public static final String TAG = "MainListFragment";
    private OnFragmentInteractionListener mListener;
    private boolean loadAfterSave = false;

    /**
     * Required empty public constructor
     */
    public MainListFragment() {
    }

    /**
     * @param item Selected item
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_backup_restore) {
            mListener.onBackupRestoreClicked();
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected BaseRecycleAdapter getAdapter(@NonNull BaseRecycleAdapter.OnItemClickListener onItemClickListener) {
        return new ReminderAdapter(onItemClickListener);
    }

    @Override
    protected void startDataLoad(boolean forceRefresh) {
        ReminderList.getInstance().clearCurrentReminder();
        if (!ReminderList.getInstance().isSaveInProgress()) {
            ReminderList.getInstance().loadData(forceRefresh);
        } else {
            loadAfterSave = true;
        }
    }

    @Override
    protected void startMoreDataLoad() {

    }

    @Override
    protected void itemClick(@NonNull BaseRecycleContainer baseRecycleContainer) {
        ReminderList.getInstance().setCurrentReminder(((ReminderItemSummary) baseRecycleContainer.getItem()).uniqueName);
        mListener.onReminderItemClicked();
    }

    @Override
    protected void afterViewCreated() {
        setFabEnable(true);
        setFabIcon(R.drawable.ic_add_white_24dp);
        setNoDataText(getString(R.string.no_reminders_yet));
        setFabAutoHide(false);
        setDummyItem(true);
    }

    @Override
    protected void fabClicked() {
        mListener.onAddNewClicked();
    }

    /**
     * On view being destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bus.unregister(this);
    }

    /**
     * @param event Event listener
     */
    @Subscribe
    public final void onReminderListEvent(@NonNull ReminderList.ReminderListEvent event) {
        switch (event) {
            case DATA_LOAD_PASS:
                applyItems();
                break;
            case DATA_LOAD_FAIL:
                Utils.toastLong(getResources().getString(R.string.data_load_fail));
                applyItems();
                break;
            case DATA_SAVE_PASS:
                if (loadAfterSave) {
                    ReminderList.getInstance().loadData(false);
                    loadAfterSave = false;
                }
                break;
            case DATA_SAVE_FAIL:
                if (loadAfterSave) {
                    ReminderList.getInstance().loadData(false);
                    loadAfterSave = false;
                }
                break;
        }
    }

    /**
     * Apply reminder items to this view
     */
    private void applyItems() {
        ArrayList<ReminderItemSummary> data = ReminderList.getInstance().getReminderSummaryList();
        if (data.isEmpty()) {
            applyData(null);
        } else {
            ArrayList<BaseRecycleContainer> reminders = new ArrayList<>();
            for (ReminderItemSummary item : data) {
                reminders.add(new ReminderContainer(item));
            }
            applyData(reminders);
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
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

        /**
         * Edit clicked
         */
        void onReminderItemClicked();

        /**
         * Add new clicker
         */
        void onAddNewClicked();

        /**
         *
         */
        void onBackupRestoreClicked();
    }

}
