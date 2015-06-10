package jamesmorrisstudios.com.randremind.util;

import android.support.annotation.DrawableRes;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 6/8/2015.
 */
public class IconUtil {

    @DrawableRes
    public static int[] iconList = new int[]{
            R.drawable.notif_1, R.drawable.notif_2, R.drawable.notif_3, R.drawable.notif_4,
            R.drawable.notif_5, R.drawable.notif_6, R.drawable.notif_7, R.drawable.notif_8,
            R.drawable.notif_9, R.drawable.notif_10, R.drawable.notif_11, R.drawable.notif_12,
            R.drawable.notif_13, R.drawable.notif_14, R.drawable.notif_15, R.drawable.notif_16,
            R.drawable.notif_17, R.drawable.notif_18, R.drawable.notif_19, R.drawable.notif_20,
            R.drawable.notif_21, R.drawable.notif_22, R.drawable.notif_23, R.drawable.notif_24,
            R.drawable.notif_25, R.drawable.notif_26, R.drawable.notif_27, R.drawable.notif_28,
            R.drawable.notif_29, R.drawable.notif_30, R.drawable.notif_31, R.drawable.notif_32,
            R.drawable.notif_33, R.drawable.notif_34, R.drawable.notif_35, R.drawable.notif_36,
            R.drawable.notif_37, R.drawable.notif_38, R.drawable.notif_39, R.drawable.notif_40,
            R.drawable.notif_41, R.drawable.notif_42, R.drawable.notif_43, R.drawable.notif_44,
            R.drawable.notif_45, R.drawable.notif_46, R.drawable.notif_47, R.drawable.notif_48,
            R.drawable.notif_49, R.drawable.notif_50, R.drawable.notif_51, R.drawable.notif_52,
            R.drawable.notif_53, R.drawable.notif_54, R.drawable.notif_55, R.drawable.notif_56,
            R.drawable.notif_57, R.drawable.notif_58, R.drawable.notif_59, R.drawable.notif_60,
            R.drawable.notif_61, R.drawable.notif_62, R.drawable.notif_63, R.drawable.notif_64,
            R.drawable.notif_65, R.drawable.notif_66, R.drawable.notif_67, R.drawable.notif_68,
            R.drawable.notif_69, R.drawable.notif_70, R.drawable.notif_71, R.drawable.notif_72
    };

    @DrawableRes
    public static int getIconRes(int index) {
        if(index < 0 || index >= iconList.length) {
            return iconList[0];
        }
        return iconList[index];
    }

    public static int getIndex(@DrawableRes int iconRes) {
        for(int i=0; i<iconList.length; i++) {
            if(iconList[i] == iconRes) {
                return i;
            }
        }
        return 0;
    }

}

