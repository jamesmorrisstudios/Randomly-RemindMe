package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.Bus;
import com.jamesmorrisstudios.appbaselibrary.Utils;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.FileBrowserRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.appbaselibrary.filewriting.WriteFileAsync;
import com.jamesmorrisstudios.appbaselibrary.fragments.BaseFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.reminder.BackupAsyncTask;
import jamesmorrisstudios.com.randremind.reminder.ReminderItemBackupRestore;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;

/**
 * Created by James on 10/3/2015.
 */
public class BackupRestoreFragment extends BaseFragment {
    public static final String TAG = "BackupRestoreFragment";

    private Button selectAll, selectNone, next, backup, restore;
    private ListView list;
    private CheckBox includeLog;

    private ListAdapter adapter = null;

    private Uri path = null;

    private View pageOption, pageBackupRestore;

    private Page page = Page.OPTION;

    public enum Page {
        OPTION, BACKUP, RESTORE
    }

    /**
     * On view being destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bus.unregister(this);
    }

    public void setPath(@Nullable Uri path) {
        this.path = path;
    }

    /**
     * @param inflater           Inflater
     * @param container          Root container
     * @param savedInstanceState Saved instance state
     * @return This fragments top view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.backup_restore_fragment, container, false);
        pageOption = view.findViewById(R.id.page_1);
        pageBackupRestore = view.findViewById(R.id.page_2);
        backup = (Button) view.findViewById(R.id.backup);
        restore = (Button) view.findViewById(R.id.restore);
        selectAll = (Button) view.findViewById(R.id.select_all);
        selectNone = (Button) view.findViewById(R.id.select_none);
        next = (Button) view.findViewById(R.id.next);
        list = (ListView) view.findViewById(R.id.list);
        includeLog = (CheckBox) view.findViewById(R.id.include_log);
        buttonListeners();
        Bus.register(this);
        return view;
    }

    @Override
    public boolean goBackInternal() {
        if(page == Page.OPTION) {
            return false;
        } else {
            page = Page.OPTION;
            setPageVisibility();
            return true;
        }
    }

    private void setPageVisibility() {
        if(page == Page.OPTION) {
            pageBackupRestore.setVisibility(View.GONE);
            pageOption.setVisibility(View.VISIBLE);
        } else {
            if(page == Page.BACKUP) {
                adapter = new ListAdapter(getActivity(), R.layout.backup_restore_item, ReminderList.getInstance().getBackupData());
                list.setAdapter(adapter);
            } else if(page == Page.RESTORE) {
                adapter = new ListAdapter(getActivity(), R.layout.backup_restore_item, ReminderList.getInstance().getRestoreData());
                list.setAdapter(adapter);
            }
            pageBackupRestore.setVisibility(View.VISIBLE);
            pageOption.setVisibility(View.GONE);
        }
    }

    private void buttonListeners() {
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = Page.BACKUP;
                ReminderList.getInstance().setBackupData();
                setPageVisibility();
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bus.postObject(new FileBrowserRequest(FileBrowserRequest.DirType.FILE, true, ".json", new FileBrowserRequest.FileBrowserRequestListener() {
                    @Override
                    public void path(@Nullable Uri uri) {
                        if(uri != null) {
                            ReminderList.getInstance().loadRestoreFile(uri);
                        }
                    }
                }));
            }
        });
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page == Page.BACKUP) {
                    for (ReminderItemBackupRestore item : ReminderList.getInstance().getBackupData()) {
                        item.selected = true;
                    }
                } else if(page == Page.RESTORE) {
                    for (ReminderItemBackupRestore item : ReminderList.getInstance().getRestoreData()) {
                        item.selected = true;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        selectNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page == Page.BACKUP) {
                    for (ReminderItemBackupRestore item : ReminderList.getInstance().getBackupData()) {
                        item.selected = false;
                    }
                } else if(page == Page.RESTORE) {
                    for (ReminderItemBackupRestore item : ReminderList.getInstance().getRestoreData()) {
                        item.selected = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page == Page.BACKUP) {
                    backupOk();
                } else if(page == Page.RESTORE) {
                    restoreOk();
                }
            }
        });
    }

    @Subscribe
    public void onReminderListEvent(ReminderList.ReminderListEvent event) {
        switch(event) {
            case RESTORE_LOAD_PASS:
                page = Page.RESTORE;
                setPageVisibility();
                break;
            case RESTORE_LOAD_FAIL:
                Utils.toastShort(getString(R.string.invalid_file));
                break;
        }
    }

    private boolean atLeastOneSelected() {
        if(page == Page.BACKUP) {
            for (ReminderItemBackupRestore item : ReminderList.getInstance().getBackupData()) {
                if (item.selected) {
                    return true;
                }
            }
        } else if(page == Page.RESTORE) {
            for (ReminderItemBackupRestore item : ReminderList.getInstance().getRestoreData()) {
                if (item.selected) {
                    return true;
                }
            }
        }
        return false;
    }

    private void restoreOk() {
        if(atLeastOneSelected()) {
            ReminderList.getInstance().restoreSelectedData(includeLog.isChecked());
            Utils.toastShort(getString(R.string.restore_complete));
        } else {
            Utils.toastShort(getString(R.string.select_atleast_one));
        }
    }

    private void backupOk() {
        if(!atLeastOneSelected()) {
           Utils.toastShort(getString(R.string.select_atleast_one));
           return;
        }
        String title = getString(R.string.backup_location);
        String[] items = new String[] {getString(R.string.share), getString(R.string.file)};

        Bus.postObject(new SingleChoiceRequest(title, items, true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    //Share
                    ReminderList.getInstance().backupSelectedDataForShare(includeLog.isChecked(), new BackupAsyncTask.BackupListener() {
                        @Override
                        public void backupComplete(boolean success, Uri filePath) {
                            if(success && filePath != null) {
                                Utils.shareStream(getString(R.string.share), filePath, "text/json");
                            } else {
                                Utils.toastShort(getString(R.string.backup_failed));
                            }
                        }
                    });
                } else {
                    //FIle
                    Bus.postObject(new FileBrowserRequest(FileBrowserRequest.DirType.DIRECTORY, true, null, new FileBrowserRequest.FileBrowserRequestListener() {
                        @Override
                        public void path(@Nullable Uri uri) {
                            if(uri != null) {
                                String path = uri.getPath();
                                ReminderList.getInstance().backupSelectedData(path, includeLog.isChecked(), new BackupAsyncTask.BackupListener() {
                                    @Override
                                    public void backupComplete(boolean success, Uri filePath) {
                                        if(success) {
                                            Utils.toastShort(getString(R.string.backup_complete));
                                        } else {
                                            Utils.toastShort(getString(R.string.backup_failed));
                                        }
                                    }
                                });
                            }
                        }
                    }));
                }
            }
        }, null));
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
        bundle.putInt("page", page.ordinal());
    }

    @Override
    protected void restoreState(Bundle bundle) {
        if(bundle.containsKey("page")) {
            page = Page.values()[bundle.getInt("page")];
        }
    }

    @Override
    protected void afterViewCreated() {
        if(path != null) {
            ReminderList.getInstance().loadRestoreFile(path);
            path = null;
        }
        setPageVisibility();
    }

    class ListAdapter extends ArrayAdapter<ReminderItemBackupRestore> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<ReminderItemBackupRestore> items) {
            super(context, resource, items);
        }

        public ArrayList<ReminderItemBackupRestore> getItems() {
            ArrayList<ReminderItemBackupRestore> list = new ArrayList<>();
            for(int i=0; i<getCount(); i++) {
                list.add(getItem(i));
            }
            return list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ReminderItemBackupRestore item = getItem(position);

            TextView title;
            final CheckBox selected;
            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.backup_restore_item, null);
            }
            title = (TextView) view.findViewById(R.id.title);
            selected = (CheckBox) view.findViewById(R.id.selected);

            if(item != null) {
                title.setText(item.title);
                selected.setChecked(item.selected);
                selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.selected = selected.isChecked();
                    }
                });
            } else {
                selected.setOnClickListener(null);
            }
            return view;
        }

    }



}
