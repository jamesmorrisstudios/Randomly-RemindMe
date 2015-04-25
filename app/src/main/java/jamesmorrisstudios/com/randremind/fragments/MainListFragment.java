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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.jamesmorrisstudios.materialdesign.views.ButtonFloat;
import com.squareup.otto.Subscribe;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.listAdapters.ReminderAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.ReminderContainer;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.utilities.Bus;
import jamesmorrisstudios.com.randremind.utilities.Utils;

/**
 * The primary list fragment showing all existing reminders
 */
public final class MainListFragment extends Fragment implements ReminderAdapter.ReminderItemClickListener {
    public static final String TAG = "MainListFragment";
    private boolean isRefreshing = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ReminderAdapter mAdapter = null;
    private TextView noDataText;
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty public constructor
     */
    public MainListFragment() {}

    /**
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * @param menu Menu
     * @param inflater Inflate
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * @param item Selected item
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                mListener.onHelpClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param inflater Inflater
     * @param container Root container
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bus.register(this);
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        final ButtonFloat addNewButton = (ButtonFloat) view.findViewById(R.id.buttonAddNew);
        addNewButton.setBackgroundColor(getResources().getColor(R.color.primaryColorAccent));
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                mListener.onAddNewClicked();
            }
        });
        noDataText = (TextView) view.findViewById(R.id.empty_view);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    addNewButton.hide();
                } else if(dy <0) {
                    addNewButton.show();
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColorAccent);
        mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Utils.removeGlobalLayoutListener(mSwipeRefreshLayout, this);
                        if (isRefreshing) {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    }
                });
        mSwipeRefreshLayout.setEnabled(false);
        isRefreshing = true;
        return view;
    }

    /**
     * View creation done
     * @param view This fragments main view
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
        boolean mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
        ViewHolder mViews = new ViewHolder(view);
        mViews.initViews(new LayoutManager(getActivity()));
        mAdapter = new ReminderAdapter(mHeaderDisplay, this);
        mAdapter.setMarginsFixed(mAreMarginsFixed);
        mAdapter.setHeaderDisplay(mHeaderDisplay);
        mViews.setAdapter(mAdapter);
        ReminderList.getInstance().loadData(false);
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
    public final void onEvent(@NonNull Bus.Event event) {
        switch(event) {
            case DATA_LOAD_PASS:
                applyItems();
                endRefresh();
                break;
            case DATA_LOAD_FAIL:
                Utils.toastLong(getResources().getString(R.string.data_load_fail));
                showNoDataText();
                endRefresh();
                break;
        }
    }

    /**
     * Apply reminder items to this view
     */
    private void applyItems() {
        if(mAdapter != null) {
            ArrayList<ReminderItem> data = ReminderList.getInstance().getData();
            if(data.isEmpty()) {
                showNoDataText();
            } else {
                ArrayList<ReminderContainer> reminders = new ArrayList<>();
                for(ReminderItem item : data) {
                    reminders.add(new ReminderContainer(item));
                }
                mAdapter.setItems(reminders);
                hideNoDataText();
            }
        } else {
            showNoDataText();
        }
    }

    /**
     * Finish up a refresh and hide the spinner
     */
    private void endRefresh () {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                isRefreshing = false;
            }
        }, 500);
    }

    /**
     * Show the no data text display
     */
    private void showNoDataText() {
        noDataText.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the no data text display
     */
    private void hideNoDataText() {
        noDataText.setVisibility(View.GONE);
    }

    /**
     * Attach to the activity
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
     * @param item Clicked reminder item
     */
    @Override
    public void itemClicked(@NonNull ReminderContainer item) {
        ReminderList.getInstance().setCurrentReminder(item.item);
        mListener.onEditClicked();
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
        void onEditClicked();

        /**
         * Add new clicker
         */
        void onAddNewClicked();

        /**
         * Help clicked
         */
        void onHelpClicked();
    }

    /**
     * View holder class
     */
    private static class ViewHolder {
        private final RecyclerView mRecyclerView;

        /**
         * Constructor
         * @param view View to set
         */
        public ViewHolder(@NonNull View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        }

        /**
         * @param lm Set with layout manager
         */
        public void initViews(@NonNull LayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);
        }

        /**
         * Set the adapter
         * @param adapter Adapter
         */
        public void setAdapter(@NonNull RecyclerView.Adapter<?> adapter) {
            mRecyclerView.setAdapter(adapter);
        }
    }

}
