package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nuums.nuums.R;


/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by Uihyun on 15. 9. 16..
 */
public class TintImageView extends ImageView {
    private ColorStateList tint;

    int tintColor = 0;

    public TintImageView(Context context) {
        super(context);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyle, 0);
        tint = a.getColorStateList(R.styleable.TintImageView_tint);
        tintColor = a.getColor(R.styleable.TintImageView_tintColor, 0);

        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tint != null && tint.isStateful())
            updateTintColor();
        if (tintColor != 0)
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        if(this.tintColor != 0) {
            setColorFilter(this.tintColor);
        } else {
            int color = tint.getColorForState(getDrawableState(), 0);
            setColorFilter(color);
        }
    }

    public void setColor(int color) {
        this.tintColor = color;
        super.setColorFilter(color);
    }

}
