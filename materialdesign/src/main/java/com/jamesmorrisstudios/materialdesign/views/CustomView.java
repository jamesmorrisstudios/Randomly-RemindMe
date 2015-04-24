package com.jamesmorrisstudios.materialdesign.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class CustomView extends RelativeLayout{
	final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
	final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
	int beforeBackground;
	// Indicate if user touched this view the last time
	public boolean isLastTouch = false;
	boolean animation = false;

	public CustomView(@NonNull Context context, @NonNull AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled)
			setBackgroundColor(beforeBackground);
		else
			setBackgroundColor(disabledBackgroundColor);
		invalidate();
	}
	
	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		animation = true;
	}
	
	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		animation = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(animation)
			invalidate();
	}

	protected final void setBackgroundColorOriginal(int color) {
		super.setBackgroundColor(color);
	}
}
