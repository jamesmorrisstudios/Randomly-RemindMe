package jamesmorrisstudios.com.randremind.util;

import android.content.Intent;

import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.preferences.Prefs;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 11/3/2015.
 */
public class RemindUtils {

    public enum WakeAction {
        MIDNIGHT, REMINDER, REMINDER_SNOOZE, REMINDER_AUTO_SNOOZE, BOOT_COMPLETED, INVALID;

        public String getKey() {
            switch(this) {
                case MIDNIGHT:
                    return AppBase.getContext().getString(R.string.wake_midnight);
                case REMINDER:
                    return AppBase.getContext().getString(R.string.wake_reminder);
                case REMINDER_SNOOZE:
                    return AppBase.getContext().getString(R.string.wake_reminder_snooze);
                case REMINDER_AUTO_SNOOZE:
                    return AppBase.getContext().getString(R.string.wake_reminder_auto_snooze);
                case BOOT_COMPLETED:
                    return AppBase.getContext().getString(R.string.wake_boot_completed);
            }
            return "";
        }
    }

    public enum NotificationUserActions {
        CLICKED, DELETED, DISMISSED, SNOOZED, ACKNOWLEDGED, INVALID;

        public String getKey() {
            switch(this) {
                case CLICKED:
                    return AppBase.getContext().getString(R.string.notification_clicked);
                case DELETED:
                    return AppBase.getContext().getString(R.string.notification_deleted);
                case DISMISSED:
                    return AppBase.getContext().getString(R.string.notification_dismiss);
                case SNOOZED:
                    return AppBase.getContext().getString(R.string.notification_snooze);
                case ACKNOWLEDGED:
                    return AppBase.getContext().getString(R.string.notification_acknowledge);
            }
            return "";
        }
    }

    public static WakeAction getWakeAction(Intent intent) {
        if(intent == null) {
            return WakeAction.INVALID;
        }

        for(WakeAction action : WakeAction.values()) {
            if(action.getKey().equals(intent.getAction())) {
                return action;
            }
        }
        return WakeAction.INVALID;
    }

    public static NotificationUserActions getNotificationUserAction(Intent intent) {
        if(intent == null) {
            return NotificationUserActions.INVALID;
        }

        for(NotificationUserActions action : NotificationUserActions.values()) {
            if(action.getKey().equals(intent.getAction())) {
                return action;
            }
        }
        return NotificationUserActions.INVALID;
    }

    public enum NotificationActions {
        COMPLETE, DISMISS, SNOOZE, NOTHING
    }

    public static NotificationActions getClickAction() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_notification_click_action);

        switch(Prefs.getInt(pref, key, 0)) {
            case 0:
                return NotificationActions.COMPLETE;
            case 1:
                return NotificationActions.DISMISS;
            case 2:
                return NotificationActions.SNOOZE;
            case 3:
                return NotificationActions.NOTHING;
            default:
                return NotificationActions.COMPLETE;
        }
    }

    public static NotificationActions getSwipeAction() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_notification_swipe_action);

        switch(Prefs.getInt(pref, key, 1)) {
            case 0:
                return NotificationActions.COMPLETE;
            case 1:
                return NotificationActions.DISMISS;
            case 2:
                return NotificationActions.SNOOZE;
            case 3:
                return NotificationActions.NOTHING;
            default:
                return NotificationActions.COMPLETE;
        }
    }

    public static boolean getClickOpenApp() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_notification_click_ack);
        return Prefs.getBoolean(pref, key, true);
    }

    public static boolean getSwipeOpensApp() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_notification_swipe_ack);
        return Prefs.getBoolean(pref, key, false);
    }

    public static boolean alwaysShowAdvanced() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_force_advanced);
        return Prefs.getBoolean(pref, key, false);
    }

}
