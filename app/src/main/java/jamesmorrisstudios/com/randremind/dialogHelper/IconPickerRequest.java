package jamesmorrisstudios.com.randremind.dialogHelper;

import android.support.annotation.NonNull;

import jamesmorrisstudios.com.randremind.fragments.IconPickerDialogBuilder;

/**
 * Created by James on 6/29/2015.
 */
public class IconPickerRequest {
    public final IconPickerDialogBuilder.IconPickerListener iconPickerListener;
    public final int accentColor;

    public IconPickerRequest(@NonNull IconPickerDialogBuilder.IconPickerListener iconPickerListener, int accentColor) {
        this.iconPickerListener = iconPickerListener;
        this.accentColor = accentColor;
    }

}
