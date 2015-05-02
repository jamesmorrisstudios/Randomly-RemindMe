package jamesmorrisstudios.com.randremind.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.fragments.BaseRecycleListFragment;
import com.jamesmorrisstudios.materialuilibrary.dialogs.MaterialDialog;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleAdapter;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleContainer;
import com.jamesmorrisstudios.materialuilibrary.listAdapters.BaseRecycleItem;
import com.jamesmorrisstudios.utilitieslibrary.Utils;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.listAdapters.SummaryAdapter;
import jamesmorrisstudios.com.randremind.listAdapters.SummaryContainer;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 *
 */
public class SummaryFragment extends BaseRecycleListFragment {
    public static final String TAG = "SummaryFragment";
    private OnSummaryListener mListener;
    private TextView titleText, contentText;
    private SwitchCompat enable;

    /**
     * Required empty public constructor
     */
    public SummaryFragment() {}

    /**
     * Constructor. Enable menu options
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Setup the toolbar options menu
     * @param menu Menu
     * @param inflater Inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_summary, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle toolbar menu button clicks
     * @param item Selected reminder
     * @return True if action consumed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                dialogListener.createPromptDialog(getString(R.string.delete_prompt_title), getString(R.string.delete_prompt_content), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ReminderList.getInstance().deleteCurrentReminder();
                        ReminderList.getInstance().saveData();
                        utilListener.goBackFromFragment();
                        Utils.toastShort(getString(R.string.reminder_delete));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                });
                break;
            case R.id.action_edit:
                mListener.onEditClicked();
                break;
            case R.id.action_preview:
                ReminderList.getInstance().previewCurrent();
                break;
            case R.id.action_duplicate:
                dialogListener.createPromptDialog(getString(R.string.duplicate_prompt_title), getString(R.string.duplicate_prompt_content), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Utils.toastShort(getString(R.string.reminder_duplicate));
                        ReminderList.getInstance().duplicateReminder();
                        ReminderList.getInstance().saveData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        titleText = (TextView) view.findViewById(R.id.titleText);
        contentText = (TextView) view.findViewById(R.id.contentText);
        enable = (SwitchCompat) view.findViewById(R.id.titleEnabled);
        initData();
        return view;
    }
*/

    @Override
    protected BaseRecycleAdapter getAdapter(int i, @NonNull BaseRecycleAdapter.OnItemClickListener onItemClickListener) {
        return new SummaryAdapter(i, onItemClickListener);
    }

    @Override
    protected void startDataLoad() {
        applyItems();
    }

    @Override
    protected void itemClicked(BaseRecycleItem baseRecycleItem) {
        //TODO
    }

    @Override
    protected void afterViewCreated() {

    }

    /**
     * Apply reminder items to this view
     */
    private void applyItems() {
        ReminderItem item = ReminderList.getInstance().getCurrentReminder();
        if(item == null) {
            applyData(null);
        } else {
            ArrayList<BaseRecycleContainer> summaries = new ArrayList<>();
            summaries.add(new SummaryContainer(item));
            applyData(summaries);
        }
    }

    private void initData() {
        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
        if (remind == null) {
            return;
        }
        enable.setChecked(remind.enabled);
        titleText.setText(remind.title);
        contentText.setText(remind.content);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.enabled = isChecked;
                if(ReminderList.getInstance().hasCurrentReminder()) {
                    ReminderList.getInstance().saveCurrentReminder();
                }
            }
        });
    }

    /**
     * Attach to the activity
     * @param activity Activity to attach to
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSummaryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onSummaryListener");
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

    @Override
    public void onBack() {

    }

    /**
     *
     */
    public interface OnSummaryListener {

        /**
         * Edit clicked
         */
        void onEditClicked();
    }

}
