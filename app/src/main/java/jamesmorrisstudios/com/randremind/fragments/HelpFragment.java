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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jamesmorrisstudios.materialdesign.views.ButtonFlat;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.utilities.Utils;


/**
 * Help Fragment that displays help and about options for the app.
 * Several sub pages with listener callbacks to the main activity.
 */
public final class HelpFragment extends Fragment {
    public static final String TAG = "HelpFragment";
    private OnFragmentInteractionListener mListener;

    /**
     * Required empty constructor
     */
    public HelpFragment() {}

    /**
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Create the options menu
     * @param menu Menu
     * @param inflater Inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_help, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * No items in the menu so ignore any clicks
     * @param item Item clicked
     * @return True if consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create the view
     * @param inflater Inflater object
     * @param container Container view
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButtonFlat readHow = (ButtonFlat) view.findViewById(R.id.howToUseRead);
        ButtonFlat watchHow = (ButtonFlat) view.findViewById(R.id.howToUseWatch);
        ButtonFlat license = (ButtonFlat) view.findViewById(R.id.helpLicense);
        TextView version = (TextView) view.findViewById(R.id.versionName);
        ImageButton btnTwitter = (ImageButton) view.findViewById(R.id.btn_twitter);
        ImageButton btnFB = (ImageButton) view.findViewById(R.id.btn_fb);
        ImageButton btnGPlus = (ImageButton) view.findViewById(R.id.btn_gplus);
        readHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTutorialClicked();
            }
        });
        watchHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Utils.toastShort(getResources().getString(R.string.todo));
            }
        });
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLicenseClicked();
            }
        });
        version.setText(Utils.getVersionName());
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.help_twitter))));
            }
        });
        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.help_fb))));
            }
        });
        btnGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.help_gPlus))));
            }
        });
        return view;
    }

    /**
     * Attach to the activity
     * @param activity Activity to attach
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
         * License fragment button clicked
         */
        void onLicenseClicked();

        /**
         * Tutorial fragment button clicked
         */
        void onTutorialClicked();
    }
}
