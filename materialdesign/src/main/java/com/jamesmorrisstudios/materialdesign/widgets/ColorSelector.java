package com.jamesmorrisstudios.materialdesign.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.jamesmorrisstudios.materialdesign.R;
import com.jamesmorrisstudios.materialdesign.views.ButtonFlat;
import com.jamesmorrisstudios.materialdesign.views.Slider;

public final class ColorSelector extends android.app.Dialog implements Slider.OnValueChangedListener {
	int color = Color.BLACK;
	Context context;
	View colorView;
	View view, backView;//background
	OnColorSelectedListener onColorSelectedListener;
	Slider red, green, blue;
	private boolean accepted = false;
	private ButtonFlat btnCancel, btnAccept;

	public ColorSelector(@NonNull Context context, @Nullable Integer color, @NonNull OnColorSelectedListener onColorSelectedListener) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;
		this.onColorSelectedListener = onColorSelectedListener;
		if(color != null)
			this.color = color;
		setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(@NonNull DialogInterface dialog) {
				if(ColorSelector.this.onColorSelectedListener != null && accepted)
					ColorSelector.this.onColorSelectedListener.onColorSelected(ColorSelector.this.color);
			}
		});
	}
	
	@Override
	  protected void onCreate(@Nullable Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.color_selector);

		btnAccept = (ButtonFlat) findViewById(R.id.button_accept);
		btnAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accepted = true;
				dismiss();
			}
		});
		btnCancel = (ButtonFlat) findViewById(R.id.button_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accepted = false;
				dismiss();
			}
		});
	    view = (LinearLayout)findViewById(R.id.contentSelector);
		backView = (RelativeLayout)findViewById(R.id.rootSelector);
		backView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
				if (event.getX() < view.getLeft() || event.getX() >view.getRight()
						|| event.getY() > view.getBottom() || event.getY() < view.getTop()) {
					accepted = false;
					dismiss();
				}
				return false;
			}
		});

	    colorView = findViewById(R.id.viewColor);
	    colorView.setBackgroundColor(color);
	    
	    
	    // Configure Sliders
	    red = (Slider) findViewById(R.id.red);
	    green = (Slider) findViewById(R.id.green);
	    blue = (Slider) findViewById(R.id.blue);
	    
	    int r = (this.color >> 16) & 0xFF;
		int g = (this.color >> 8) & 0xFF;
		int b = (this.color >> 0) & 0xFF;
		
		red.setValue(r);
		green.setValue(g);
		blue.setValue(b);
		
		red.setOnValueChangedListener(this);
		green.setOnValueChangedListener(this);
		blue.setOnValueChangedListener(this);
	}

	@Override
	public void show() {
		super.show();
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
		backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
	}

	@Override
	public void onValueChanged(int value) {
		color = Color.rgb(red.getValue(), green.getValue(), blue.getValue());
		colorView.setBackgroundColor(color);
	}
	
	
	// Event that execute when color selector is closed
	public interface OnColorSelectedListener{
		void onColorSelected(int color);
	}
		
	@Override
	public void dismiss() {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
		
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.post(new Runnable() {
					@Override
					public void run() {
						ColorSelector.super.dismiss();
			        }
			    });
			}
		});
		
		Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
		
		view.startAnimation(anim);
		backView.startAnimation(backAnim);
	}

}
