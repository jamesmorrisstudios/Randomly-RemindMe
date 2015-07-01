package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jamesmorrisstudios.utilitieslibrary.Utils;

import java.util.ArrayList;

import jamesmorrisstudios.com.randremind.R;

/**
 *
 * Created by James on 6/30/2015.
 */
public class EditMessageDialogBuilder {
    private AlertDialog.Builder builder;
    private ScrollView mainView;
    private LinearLayout pickerContainer;
    private AlertDialog dialog;
    private ArrayList<String> messages = null;
    private EditMessageListener onEditMessageListener;

    private EditMessageDialogBuilder(Context context) {
        builder = new AlertDialog.Builder(context, R.style.alertDialog);
        mainView = new ScrollView(context);
        pickerContainer = new LinearLayout(context);
        pickerContainer.setOrientation(LinearLayout.VERTICAL);
        pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mainView.addView(pickerContainer);
        builder.setView(mainView);
    }

    public static EditMessageDialogBuilder with(@NonNull Context context) {
        return new EditMessageDialogBuilder(context);
    }

    public EditMessageDialogBuilder setTitle(@NonNull String title) {
        builder.setTitle(title);
        return this;
    }

    public EditMessageDialogBuilder setMessages(@NonNull ArrayList<String> messages) {
        this.messages = new ArrayList<>(messages);
        return this;
    }

    public EditMessageDialogBuilder setOnPositive(@NonNull String text, @NonNull EditMessageListener onClickListener) {
        this.onEditMessageListener = onClickListener;
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onEditMessageListener.onPositive(messages);
            }
        });
        return this;
    }

    public EditMessageDialogBuilder setOnNegative(@NonNull String text, @NonNull DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(text, onClickListener);
        return this;
    }

    public AlertDialog build() {
        Context context = builder.getContext();
        buildIconList(context);
        dialog = builder.create();
        return dialog;
    }

    @NonNull
    public final ArrayList<String> getMessages() {
        return messages;
    }

    private void buildIconList(@NonNull Context context) {
        for (int i = 0; i < messages.size(); i++) {
            AppCompatEditText message = new AppCompatEditText(context);
            LinearLayout.LayoutParams paramTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramTop.setMargins(Utils.getDipInt(8), Utils.getDipInt(8), Utils.getDipInt(8), Utils.getDipInt(8));
            message.setLayoutParams(paramTop);
            message.setGravity(Gravity.CENTER_HORIZONTAL);
            message.setText(messages.get(i));
            addMessagesListener(i, message);
            pickerContainer.addView(message);
        }
    }

    private void addMessagesListener(final int index, AppCompatEditText editText) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                messages.set(index, s.toString());
            }
        };
        editText.addTextChangedListener(textWatcher);
    }

    public interface EditMessageListener {
        void onPositive(ArrayList<String> messages);
    }

}
