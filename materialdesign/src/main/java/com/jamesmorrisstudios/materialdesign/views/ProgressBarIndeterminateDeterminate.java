package com.jamesmorrisstudios.materialdesign.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

public class ProgressBarIndeterminateDeterminate extends ProgressBarDeterminate {
	
	boolean firstProgress = true;
	boolean runAnimation = true;
	ObjectAnimator animation;
	
	

	public ProgressBarIndeterminateDeterminate(Context context, AttributeSet attrs) {
		super(context, attrs);
		post(new Runnable() {
			
			@Override
			public void run() {
				// Make progress animation
				
				setProgress(60);
				progressView.setX(getWidth()+progressView.getWidth()/2);
				animation = ObjectAnimator.ofFloat(progressView, "x", -progressView.getWidth()/2);
				animation.setDuration(1200);
				animation.addListener(new Animator.AnimatorListener() {
					
					int cont = 1;
					int suma = 1;
					int duration = 1200;
					
					public void onAnimationEnd(Animator arg0) {
						// Repeat animation
						if(runAnimation){
						progressView.setX(getWidth()+progressView.getWidth()/2);
						cont += suma;
						animation = ObjectAnimator.ofFloat(progressView, "x", -progressView.getWidth()/2);
						animation.setDuration(duration/cont);
						animation.addListener(this);
						animation.start();
						if(cont == 3 || cont == 1) suma *=-1;
						}
						
					}
					
					public void onAnimationStart(Animator arg0) {}
					public void onAnimationRepeat(Animator arg0) {}
					public void onAnimationCancel(Animator arg0) {}
				});
				animation.start();
			}
		});
	}
	
	@Override
	public void setProgress(int progress) {
		if(firstProgress){
			firstProgress = false;
		}else{
			stopIndeterminate();
		}
		super.setProgress(progress);
	}
	
	/**
	 * Stop indeterminate animation to convert view in determinate progress bar
	 */
	private void stopIndeterminate(){
		animation.cancel();
		progressView.setX(0);
		runAnimation = false;
	}

}
