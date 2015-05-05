package jamesmorrisstudios.com.randremind;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by James on 5/4/2015.
 */
public class CompatImageView extends ImageView {

    public CompatImageView(Context context) {
        super(context);
    }

    public CompatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(getBackground() != null) {
            setImageDrawable(getBackground());
        }
    }

    public CompatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(getBackground() != null) {
            setImageDrawable(getBackground());
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        drawable = DrawableCompat.wrap(drawable);
        // We can now set a tint
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.tintColor));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_OVER);
        super.setImageDrawable(drawable);
    }

}
