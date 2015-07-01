package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.ColorPickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.RingtoneRequest;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.dialogs.colorpicker.builder.ColorPickerClickListener;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.dialogHelper.IconPickerRequest;
import jamesmorrisstudios.com.randremind.fragments.IconPickerDialogBuilder;
import jamesmorrisstudios.com.randremind.reminder.ReminderItem;
import jamesmorrisstudios.com.randremind.reminder.ReminderList;
import jamesmorrisstudios.com.randremind.util.IconUtil;

/**
 * Created by James on 6/10/2015.
 */
public class EditReminderNotification {
    private AppCompatCheckBox vibrateEnable, ledEnable, highPriorityEnable;
    private TextView sound;
    private View ledColor, accentColor;
    private ImageView icon;
    private RelativeLayout notificationIconTop;

    public EditReminderNotification(RelativeLayout parent) {
        vibrateEnable = (AppCompatCheckBox) parent.findViewById(R.id.notification_vibrate_enabled);
        ledEnable = (AppCompatCheckBox) parent.findViewById(R.id.notification_led_enabled);
        highPriorityEnable = (AppCompatCheckBox) parent.findViewById(R.id.notification_high_priority_enabled);
        sound = (TextView) parent.findViewById(R.id.notificationSound);
        ledColor = parent.findViewById(R.id.notification_led_color);
        icon = (ImageView) parent.findViewById(R.id.notificationIcon);
        accentColor = parent.findViewById(R.id.notification_accent_color);
        notificationIconTop = (RelativeLayout) parent.findViewById(R.id.notificationIconTop);
    }

    public final void bindItem(EditReminderItem item) {
        final ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
        if(reminderItem == null) {
            return;
        }
        vibrateEnable.setChecked(reminderItem.notificationVibrate);
        highPriorityEnable.setChecked(reminderItem.notificationHighPriority);
        ledEnable.setChecked(reminderItem.notificationLED);
        sound.setText(reminderItem.notificationToneName);

        ((GradientDrawable) ledColor.getBackground()).setColor(reminderItem.notificationLEDColor);
        ((GradientDrawable) accentColor.getBackground()).setColor(reminderItem.notificationAccentColor);
        ((GradientDrawable) notificationIconTop.getBackground()).setColor(reminderItem.notificationAccentColor);
        icon.setImageResource(IconUtil.getIconRes(reminderItem.notificationIcon));

        notificationListeners();
    }

    private void notificationListeners() {
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (remind.notificationTone != null) {
                    defaultUri = Uri.parse(remind.notificationTone);
                }
                Bus.postObject(new RingtoneRequest(defaultUri, AppUtil.getContext().getResources().getString(R.string.selectNotification), new RingtoneRequest.RingtoneRequestListener() {
                    @Override
                    public void ringtoneResponse(Uri uri, String name) {
                        if (uri != null) {
                            if(uri != null) {
                                remind.notificationTone = uri.toString();
                            } else {
                                remind.notificationTone = null;
                            }
                            if(name != null) {
                                remind.notificationToneName = name;
                            } else {
                                remind.notificationToneName = AppUtil.getContext().getString(R.string.none);
                            }
                            sound.setText(remind.notificationToneName);
                        }
                    }
                }));
            }
        });
        vibrateEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationVibrate = isChecked;
            }
        });
        highPriorityEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationHighPriority = isChecked;
            }
        });
        ledEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                remind.notificationLED = isChecked;
            }
        });
        ledColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new ColorPickerRequest(remind.notificationLEDColor, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationLEDColor = color;
                        ((GradientDrawable) ledColor.getBackground()).setColor(remind.notificationLEDColor);
                    }
                }));
            }
        });
        accentColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new ColorPickerRequest(remind.notificationAccentColor, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationAccentColor = color;
                        ((GradientDrawable) accentColor.getBackground()).setColor(remind.notificationAccentColor);
                        ((GradientDrawable) notificationIconTop.getBackground()).setColor(remind.notificationAccentColor);
                    }
                }));
            }
        });
        notificationIconTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new IconPickerRequest(new IconPickerDialogBuilder.IconPickerListener() {
                    @Override
                    public void onClick(@DrawableRes int iconRes) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.notificationIcon = IconUtil.getIndex(iconRes);
                        icon.setImageResource(iconRes);
                    }
                }, remind.notificationAccentColor));
            }
        });
    }

}
