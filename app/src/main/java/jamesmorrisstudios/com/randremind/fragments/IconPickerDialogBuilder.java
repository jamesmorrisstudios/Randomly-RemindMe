package jamesmorrisstudios.com.randremind.fragments;

import android.content.Context;
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

import com.jamesmorrisstudios.appbaselibrary.Utils;

import jamesmorrisstudios.com.randremind.R;
import jamesmorrisstudios.com.randremind.util.IconUtil;

/**
 * Created by James on 5/20/2015.
 */
public class IconPickerDialogBuilder {
    private AlertDialog.Builder builder;
    private ScrollView mainView;
    private LinearLayout pickerContainer;
    private IconPickerListener onIconPickedListener;
    private int accentColor;
    private AlertDialog dialog;

    private IconPickerDialogBuilder(@NonNull Context context, int style) {
        builder = new AlertDialog.Builder(context, style);
        mainView = new ScrollView(context);
        pickerContainer = new LinearLayout(context);
        pickerContainer.setOrientation(LinearLayout.VERTICAL);
        pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mainView.addView(pickerContainer);
        builder.setView(mainView);
    }

    public static IconPickerDialogBuilder with(@NonNull Context context, int style) {
        return new IconPickerDialogBuilder(context, style);
    }

    public IconPickerDialogBuilder setTitle(@NonNull String title) {
        builder.setTitle(title);
        return this;
    }

    public IconPickerDialogBuilder setAccentColor(int accentColor) {
        this.accentColor = accentColor;
        return this;
    }

    public IconPickerDialogBuilder setOnIconPicked(@NonNull IconPickerListener onIconPickedListener) {
        this.onIconPickedListener = onIconPickedListener;
        return this;
    }

    public AlertDialog build() {
        Context context = builder.getContext();
        buildIconList(context);
        dialog = builder.create();
        return dialog;
    }

    private void buildIconList(@NonNull Context context) {
        LinearLayout row = null;
        for (int i = 0; i < IconUtil.iconList.length; i++) {
            if (i % 4 == 0) {
                row = new LinearLayout(context);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);
                pickerContainer.addView(row);
            }
            if (row != null) {
                addIcon(context, row, IconUtil.iconList[i]);
            }
        }
    }

    private void addIcon(@NonNull Context context, @NonNull LinearLayout parent, @DrawableRes final int iconRes) {
        RelativeLayout top = new RelativeLayout(context);
        LinearLayout.LayoutParams paramTop = new LinearLayout.LayoutParams(Utils.getDipInt(45), Utils.getDipInt(45));
        paramTop.setMargins(Utils.getDipInt(6), Utils.getDipInt(6), Utils.getDipInt(6), Utils.getDipInt(6));
        top.setLayoutParams(paramTop);
        top.setBackgroundResource(R.drawable.circle);
        ((GradientDrawable) top.getBackground()).setColor(accentColor);

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
