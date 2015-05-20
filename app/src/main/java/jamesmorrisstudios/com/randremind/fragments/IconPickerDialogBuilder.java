package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jamesmorrisstudios.utilitieslibrary.Utils;

import jamesmorrisstudios.com.randremind.R;

/**
 * Created by James on 5/20/2015.
 */
public class IconPickerDialogBuilder {
    private static int[] icons = new int[] {
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

    private AlertDialog.Builder builder;
    private ScrollView mainView;
    private LinearLayout pickerContainer;
    private IconPickerListener onIconPickedListener;
    private int accentColor;
    private AlertDialog dialog;

    private IconPickerDialogBuilder(Context context) {
        builder = new AlertDialog.Builder(context, R.style.ColorPickerDialog);
        mainView = new ScrollView(context);
        pickerContainer = new LinearLayout(context);
        pickerContainer.setOrientation(LinearLayout.VERTICAL);
        pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mainView.addView(pickerContainer);
        builder.setView(mainView);
    }

    public static IconPickerDialogBuilder with(Context context) {
        return new IconPickerDialogBuilder(context);
    }

    public IconPickerDialogBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public IconPickerDialogBuilder setAccentColor(int accentColor) {
        this.accentColor = accentColor;
        return this;
    }

    public IconPickerDialogBuilder setOnIconPicked(IconPickerListener onIconPickedListener) {
        this.onIconPickedListener = onIconPickedListener;
        return this;
    }

    public AlertDialog build() {
        Context context = builder.getContext();
        buildIconList(context);
        dialog = builder.create();
        return dialog;
    }

    private void buildIconList(Context context) {
        LinearLayout row = null;
        for(int i=0; i<icons.length; i++) {
            if(i % 4 == 0) {
                row = new LinearLayout(context);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);
                pickerContainer.addView(row);
            }
            if(row != null) {
                addIcon(context, row, icons[i]);
            }
        }
    }

    private void addIcon(@NonNull Context context, @NonNull LinearLayout parent, @DrawableRes final int iconRes) {
        RelativeLayout top = new RelativeLayout(context);
        LinearLayout.LayoutParams paramTop = new LinearLayout.LayoutParams(Utils.getDipInt(45), Utils.getDipInt(45));
        paramTop.setMargins(Utils.getDipInt(6), Utils.getDipInt(6), Utils.getDipInt(6), Utils.getDipInt(6));
        top.setLayoutParams(paramTop);
        top.setBackgroundResource(R.drawable.circle);
        ((GradientDrawable)top.getBackground()).setColor(accentColor);

        ImageView image = new ImageView(context);
        RelativeLayout.LayoutParams paramImage = new RelativeLayout.LayoutParams(Utils.getDipInt(25), Utils.getDipInt(25));
        paramImage.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        image.setLayoutParams(paramImage);
        image.setBackgroundResource(iconRes);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIconPickedListener.onClick(iconRes);
                dialog.dismiss();
            }
        });
        top.addView(image);

        parent.addView(top);
    }

    public interface IconPickerListener {
        void onClick(@DrawableRes int iconRes);
    }

}
