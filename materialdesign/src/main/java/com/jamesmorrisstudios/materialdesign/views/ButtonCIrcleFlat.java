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

package com.jamesmorrisstudios.materialdesign.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesmorrisstudios.materialdesign.R;
import com.jamesmorrisstudios.materialdesign.utils.Utils;

public class ButtonCircleFlat extends Button {
	TextView textButton;

	public ButtonCircleFlat(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void setDefaultProperties(){
		minHeight = 36;
		minWidth = 36;
		rippleSize = 3;
		// Min size
		setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
		setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
		setBackgroundResource(R.drawable.background_transparent);
	}

	@Override
	protected void setAttributes(@NonNull AttributeSet attrs) {
		// Set text button
		String text = null;
		int textResource = attrs.getAttributeResourceValue(ANDROIDXML,"text",-1);
		if(textResource != -1){
			text = getResources().getString(textResource);
		}else{
			text = attrs.getAttributeValue(ANDROIDXML,"text");
		}
		if(text == null) {
			text = "";
		}
		this.setGravity(Gravity.CENTER);

		textButton = new TextView(getContext());
		textButton.setText(text.toUpperCase());
		textButton.setGravity(Gravity.CENTER);
		textButton.setTextColor(backgroundColor);
		textButton.setTypeface(null, Typeface.BOLD);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		textButton.setLayoutParams(params);
		addView(textButton);

		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,"background",-1);
		if(bacgroundColor != -1){
			setBackgroundColor(getResources().getColor(bacgroundColor));
		}else{
			// Color by hexadecimal
			// Color by hexadecimal
			background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
			if (background != -1)
				setBackgroundColor(background);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(makePressColor());
			canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, radius, paint);
			if (radius > getHeight() / rippleSize)
				radius += rippleSpeed;
			if (radius >= getWidth() / 2.0f) {
				x = -1;
				y = -1;
				radius = getHeight() / rippleSize;
				if (onClickListener != null && clickAfterRipple)
					onClickListener.onClick(this);
			}
			invalidate();
		} else if(isActivated()) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(makePressColor());
			canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, paint);
		}
	}

	@Override
	public void setActivated(boolean activated) {
		super.setActivated(activated);
		invalidate();
	}
	
	/**
	 * Make a dark color to ripple effect
	 * @return
	 */
	@Override
	protected int makePressColor(){
		return Color.parseColor("#CCFFFFFF");
	}
	
	public void setText(String text){
		textButton.setText(text.toUpperCase());
	}
	
	// Set color of background
	public void setBackgroundColor(int color){
		backgroundColor = color;
		if(isEnabled())
			beforeBackground = backgroundColor;
		textButton.setTextColor(color);
	}

	@Override
	public TextView getTextView() {
		return textButton;
	}
	
	public String getText(){
        	return textButton.getText().toString();
 	}

}
