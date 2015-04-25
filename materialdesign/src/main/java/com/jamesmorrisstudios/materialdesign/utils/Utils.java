package com.jamesmorrisstudios.materialdesign.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;

public final class Utils {

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, @NonNull Resources resources){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}

	public static int getRelativeTop(@NonNull View myView) {
	    if(myView.getId() == android.R.id.content)
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static int getRelativeLeft(@NonNull View myView) {
		if(myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}

}
