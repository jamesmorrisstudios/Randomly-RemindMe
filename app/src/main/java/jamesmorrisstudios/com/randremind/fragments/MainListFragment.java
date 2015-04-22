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
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     *
     * @param item
     * @return
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
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bus.register(this);
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        ButtonFloat addNewButton = (ButtonFloat) view.findViewById(R.id.buttonAddNew);
        addNewButton.setBackgroundColor(getResources().getColor(R.color.primaryColorAccent));
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddNewClicked();
            }
        });
        noDataText = (TextView) view.findViewById(R.id.empty_view);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    //Force a refresh on reminder data
                    isRefreshing = true;
                    ReminderList.getInstance().loadData(true);
                }
            }
        });
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
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bus.unregister(this);
    }

    /**
     *
     * @param event
     */
    @Subscribe
    public final void onEvent(Bus.Event event) {
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
     *
     */
    private void showNoDataText() {
        noDataText.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    private void hideNoDataText() {
        noDataText.setVisibility(View.GONE);
    }

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     * @param item
     */
    @Override
    public void itemClicked(ReminderContainer item) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

        /**
         *
         */
        void onAddNewClicked();

        /**
         *
         */
        void onHelpClicked();
    }

    /**
     * View holder class
     */
    private static class ViewHolder {
        private final RecyclerView mRecyclerView;

        /**
         *
         * @param view
         */
        public ViewHolder(View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        }

        /**
         *
         * @param lm
         */
        public void initViews(LayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);
        }

        /**
         *
         * @param adapter
         */
        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            mRecyclerView.setAdapter(adapter);
        }
    }

}
