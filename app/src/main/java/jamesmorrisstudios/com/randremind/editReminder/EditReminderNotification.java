package jamesmorrisstudios.com.randremind.editReminder;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.appbaselibrary.dialogHelper.ColorPickerRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.RingtoneRequest;
import com.jamesmorrisstudios.appbaselibrary.dialogHelper.SingleChoiceRequest;
import com.jamesmorrisstudios.utilitieslibrary.Bus;
import com.jamesmorrisstudios.utilitieslibrary.app.AppUtil;
import com.jamesmorrisstudios.utilitieslibrary.dialogs.colorpicker.builder.ColorPickerClickListener;
import com.jamesmorrisstudios.utilitieslibrary.notification.NotificationContent;

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
    private TextView sound, priority, vibrate, led;
    private View ledColor, accentColor;
    private ImageView icon;
    private RelativeLayout notificationIconTop, notificationSoundContainer, notificationPriorityContainer,
            notificationVibrateContainer, notificationLedContainer, notificationIconContainer, notificationAccentContainer;

    public EditReminderNotification(View parent) {
        sound = (TextView) parent.findViewById(R.id.notificationSound);
        ledColor = parent.findViewById(R.id.notification_led_color);
        icon = (ImageView) parent.findViewById(R.id.notificationIcon);
        accentColor = parent.findViewById(R.id.notification_accent_color);
        notificationIconTop = (RelativeLayout) parent.findViewById(R.id.notificationIconTop);
        notificationSoundContainer = (RelativeLayout) parent.findViewById(R.id.notificationSoundContainer);
        notificationPriorityContainer = (RelativeLayout) parent.findViewById(R.id.notificationPriorityContainer);
        priority = (TextView) parent.findViewById(R.id.notificationPriority);
        notificationVibrateContainer = (RelativeLayout) parent.findViewById(R.id.notificationVibrateContainer);
        vibrate = (TextView) parent.findViewById(R.id.notificationVibrate);
        notificationLedContainer = (RelativeLayout) parent.findViewById(R.id.notificationLedContainer);
        led = (TextView) parent.findViewById(R.id.notificationLed);
        notificationIconContainer = (RelativeLayout) parent.findViewById(R.id.notificationIconContainer);
        notificationAccentContainer = (RelativeLayout) parent.findViewById(R.id.notificationAccentContainer);
    }

    public final void bindItem(EditReminderItem item) {
        final ReminderItem reminderItem = ReminderList.getInstance().getCurrentReminder();
        if(reminderItem == null) {
            return;
        }
        sound.setText(reminderItem.getNotificationToneName());
        priority.setText(reminderItem.getNotificationPriority().name);
        vibrate.setText(reminderItem.getNotificationVibratePattern().name);
        if(reminderItem.isNotificationLED()) {
            led.setText(AppUtil.getContext().getString(R.string.enabled));
        } else {
            led.setText(AppUtil.getContext().getString(R.string.disabled));
        }

        ((GradientDrawable) ledColor.getBackground()).setColor(reminderItem.getNotificationLEDColor());
        ((GradientDrawable) accentColor.getBackground()).setColor(reminderItem.getNotificationAccentColor());
        ((GradientDrawable) notificationIconTop.getBackground()).setColor(reminderItem.getNotificationAccentColor());
        icon.setImageResource(IconUtil.getIconRes(reminderItem.getNotificationIcon()));

        notificationListeners();
    }

    private void notificationListeners() {
        notificationSoundContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                final ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (remind.getNotificationTone() != null) {
                    defaultUri = remind.getNotificationTone();
                }
                Bus.postObject(new RingtoneRequest(defaultUri, AppUtil.getContext().getResources().getString(R.string.select_notification), new RingtoneRequest.RingtoneRequestListener() {
                    @Override
                    public void ringtoneResponse(Uri uri, String name) {
                        if (uri != null && name != null) {
                            remind.setNotificationTone(uri.toString());
                            remind.setNotificationToneName(name);
                        } else {
                            remind.setNotificationTone(null);
                            remind.setNotificationToneName(AppUtil.getContext().getString(R.string.none));
                        }
                        sound.setText(remind.getNotificationToneName());
                    }
                }));
            }
        });
        notificationVibrateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                String title = AppUtil.getContext().getString(R.string.vibrate);
                final NotificationContent.NotificationVibrate[] vibrateList = NotificationContent.NotificationVibrate.values();
                String[] items = new String[vibrateList.length];
                for (int i = 0; i < vibrateList.length; i++) {
                    items[i] = vibrateList[i].name;
                }
                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setNotificationVibratePattern(vibrateList[which]);
                        vibrate.setText(remind.getNotificationVibratePattern().name);
                    }
                }, null));
            }
        });
        notificationPriorityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = AppUtil.getContext().getString(R.string.priority);
                final NotificationContent.NotificationPriority[] priorityList = NotificationContent.NotificationPriority.values();
                String[] items = new String[priorityList.length];
                for(int i=0; i<priorityList.length; i++) {
                    items[i] = priorityList[i].name;
                }
                Bus.postObject(new SingleChoiceRequest(title, items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Item Selected
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setNotificationPriority(priorityList[which]);
                        priority.setText(remind.getNotificationPriority().name);
                    }
                }, null));
            }
        });
        notificationLedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new ColorPickerRequest(remind.getNotificationLEDColor(), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setNotificationLED(true);
                        remind.setNotificationLEDColor(color);
                        led.setText(AppUtil.getContext().getString(R.string.enabled));
                        ((GradientDrawable) ledColor.getBackground()).setColor(remind.getNotificationLEDColor());
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Disable
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        Log.v("Notification", "Disable LED");
                        remind.setNotificationLED(false);
                        led.setText(AppUtil.getContext().getString(R.string.disabled));
                    }
                }));
            }
        });
        notificationAccentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                if (remind == null) {
                    return;
                }
                Bus.postObject(new ColorPickerRequest(remind.getNotificationAccentColor(), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        ReminderItem remind = ReminderList.getInstance().getCurrentReminder();
                        if (remind == null) {
                            return;
                        }
                        remind.setNotificationAccentColor(color);
                        ((GradientDrawable) accentColor.getBackground()).setColor(remind.getNotificationAccentColor());
                        ((GradientDrawable) notificationIconTop.getBackground()).setColor(remind.getNotificationAccentColor());
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, null));
            }
        });
        notificationIconContainer.setOnClickListener(new View.OnClickListener() {
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
                        remind.setNotificationIcon(IconUtil.getIndex(iconRes));
                        icon.setImageResource(iconRes);
                    }
                }, remind.getNotificationAccentColor()));
            }
        });
    }

}
