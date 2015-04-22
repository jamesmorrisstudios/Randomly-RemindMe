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

package jamesmorrisstudios.com.randremind.utilities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import jamesmorrisstudios.com.randremind.application.App;

/**
 * Basic utility class for this project.
 *
 * Created by James on 4/20/2015.
 */
public final class Utils {

    /**
     * Device orientations
     */
    public enum Orientation {
        UNDEFINED, PORTRAIT, LANDSCAPE
    }

    /**
     * Android defined screen size categories
     */
    public enum ScreenSize {
        SMALL, NORMAL, LARGE, XLARGE, UNDEFINED
    }

    /**
     * Displays a popup toast for a short time
     * @param text Text to display
     */
    public static void toastShort(@NonNull String text) {
        Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a popup toast for a long time
     * @param text Text to display
     */
    public static void toastLong(@NonNull String text) {
        Toast.makeText(App.getContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Converts a dip value into a pixel value
     * Usually you want to use getDipInt unless you are performing additional calculations with the result.
     *
     * @param dp Dip value
     * @return Pixel value
     */
    public static float getDip(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getContext().getResources().getDisplayMetrics());
    }

    /**
     * Converts a dip value into a pixel value rounded to the nearest int
     * This is typically the desired choice as pixels are only in ints
     *
     * @param dip Dip value
     * @return Pixel value
     */
    public static int getDipInt(int dip) {
        return Math.round(getDip(dip));
    }

    /**
     * Gets the current device orientation.
     * There are some reports that this may return the wrong result on some devices
     * but I have not found any yet. I may update this will a fallback screensize check
     *
     * @return The device orientation
     */
    @NonNull
    public static Orientation getOrientation() {
        switch (App.getContext().getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_UNDEFINED:
                return Orientation.UNDEFINED;
            case Configuration.ORIENTATION_PORTRAIT:
                return Orientation.PORTRAIT;
            case Configuration.ORIENTATION_LANDSCAPE:
                return Orientation.LANDSCAPE;
            default:
                return Orientation.UNDEFINED;
        }
    }

    /**
     * Gets the screen size bucket category
     *
     * @return The screensize
     */
    @NonNull
    public static ScreenSize getScreenSize() {
        int screenLayout = App.getContext().getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return ScreenSize.SMALL;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return ScreenSize.NORMAL;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return ScreenSize.LARGE;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return ScreenSize.XLARGE;
            default:
                return ScreenSize.UNDEFINED;
        }
    }

    /**
     * Backwards compatible method of removing the global layout listener
     *
     * @param src    The view to remove from
     * @param victim The global layout listener to remove
     */
    @SuppressWarnings("deprecation")
    public static void removeGlobalLayoutListener(@NonNull View src, @NonNull ViewTreeObserver.OnGlobalLayoutListener victim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            src.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
        } else {
            src.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
        }
    }

    /**
     * Gets the formatted version string of the app in the format 1.2.3
     *
     * @return The formatted version string
     */
    @NonNull
    public static String getVersionName() {
        PackageInfo pInfo;
        try {
            pInfo = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //Ignore faults
        }
        return "";
    }

    /**
     * @return True if the user preference is 24 hour time
     */
    public static boolean is24HourTime() {
        return DateFormat.is24HourFormat(App.getContext());
    }

    /**
     * @param hour Hour in 24 hour (calendar form)
     * @return True if am, false if pm
     */
    public static boolean isAM(int hour) {
        return hour < 12;
    }

    /**
     * Converts the 24 hour calendar form to the user preferred style
     * @param hour Hour in 24 hour (calendar form)
     * @return Hour in the user preferred style
     */
    public static int getHourInTimeFormat(int hour) {
        if(is24HourTime() || hour <= 12 && hour >= 1) {
            return hour;
        } else if(hour >= 13) {
            return hour - 12;
        } else {
            return 12;
        }
    }

}
