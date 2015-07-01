package jamesmorrisstudios.com.randremind.dialogHelper;

import android.content.DialogInterface;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.fragments.EditMessageDialogBuilder;

/**
 * Created by James on 6/30/2015.
 */
public class EditMessageRequest {
    public final ArrayList<String> messages;
    public final EditMessageDialogBuilder.EditMessageListener onPositive;
    public final DialogInterface.OnClickListener onNegative;

    public EditMessageRequest(ArrayList<String> messages, EditMessageDialogBuilder.EditMessageListener onPositive, DialogInterface.OnClickListener onNegative) {
        this.messages = messages;
        this.onPositive = onPositive;
        this.onNegative = onNegative;
    }

}
