package jamesmorrisstudios.com.randremind.util;

import com.jamesmorrisstudios.appbaselibrary.app.AppBase;
import com.jamesmorrisstudios.appbaselibrary.preferences.Prefs;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 11/3/2015.
 */
public class RemindUtils {

    public static boolean alwaysShowAdvanced() {
        String pref = AppBase.getContext().getString(R.string.settings_pref);
        String key = AppBase.getContext().getString(R.string.pref_force_advanced);
        return Prefs.getBoolean(pref, key, false);
    }
}
