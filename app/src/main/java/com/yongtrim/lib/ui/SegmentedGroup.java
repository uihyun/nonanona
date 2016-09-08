package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.nuums.nuums.R;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 10. 28..
 */
public class SegmentedGroup extends RadioGroup {

    private int oneDP;
    private Resources resources;
    private int mTintColor;
    @SuppressWarnings("FieldCanBeLocal")

    private int mUnCheckedTextColor = Color.BLACK;
    private int mCheckedTextColor = Color.WHITE;

    public SegmentedGroup(Context context) {
        super(context);
        resources = getResources();
        oneDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());
        init(context);
    }

    public SegmentedGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        resources = getResources();
        oneDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());
        init(context);
    }

    public void init(Context context) {
        mTintColor = resources.getColor(R.color.red);

        mUnCheckedTextColor = resources.getColor(R.color.gray);
        mCheckedTextColor = resources.getColor(R.color.white);

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //Use holo light for default
        updateBackground();
    }

    public void setTintColor(int tintColor) {
        mTintColor = tintColor;

        updateBackground();
    }

//    public void setTintColor(int tintColor, int checkedTextColor) {
//        mTintColor = tintColor;
//        mUnCheckedTextColor = tintColor;
//        mCheckedTextColor = checkedTextColor;
//        updateBackground();
//    }

    public void setCheckColor(int uncheckedTextColor, int checkedTextColor) {
        mUnCheckedTextColor = uncheckedTextColor;
        mCheckedTextColor = checkedTextColor;
    }

    public void updateBackground() {
        int count = super.getChildCount();
        if (count > 1) {
            View child = getChildAt(0);
            LayoutParams initParams = (LayoutParams) child.getLayoutParams();
            LayoutParams params = new LayoutParams(initParams.width, initParams.height, initParams.weight);
            // Check orientation for proper margins
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                params.setMargins(0, 0, -oneDP, 0);
            } else {
                params.setMargins(0, 0, 0, -oneDP);
            }
            child.setLayoutParams(params);
            // Check orientation for proper layout
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                updateBackground(getChildAt(0), R.drawable.radio_checked, R.drawable.radio_unchecked);
            } else {
                updateBackground(getChildAt(0), R.drawable.radio_checked, R.drawable.radio_unchecked);
            }
            for (int i = 1; i < count - 1; i++) {
                // Check orientation for proper layout
                if (getOrientation() == LinearLayout.HORIZONTAL) {
                    updateBackground(getChildAt(i), R.drawable.radio_checked, R.drawable.radio_unchecked);
                } else {
                    // Middle radiobutton when checked is the same as horizontal.
                    updateBackground(getChildAt(i), R.drawable.radio_checked, R.drawable.radio_unchecked);
                }
                View child2 = getChildAt(i);
                initParams = (LayoutParams) child2.getLayoutParams();
                params = new LayoutParams(initParams.width, initParams.height, initParams.weight);
                // Check orientation for proper margins
                if (getOrientation() == LinearLayout.HORIZONTAL) {
                    params.setMargins(0, 0, -oneDP, 0);
                } else {
                    params.setMargins(0, 0, 0, -oneDP);
                }
                child2.setLayoutParams(params);
            }
            // Check orientation for proper layout
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                updateBackground(getChildAt(count - 1), R.drawable.radio_checked, R.drawable.radio_unchecked);
            } else {
                updateBackground(getChildAt(count - 1), R.drawable.radio_checked, R.drawable.radio_unchecked);
            }
        } else if (count == 1) {
            updateBackground(getChildAt(0), R.drawable.radio_checked, R.drawable.radio_unchecked);
        }
    }

    private void updateBackground(View view, int checked, int unchecked) {
        //Set text color
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                {android.R.attr.state_pressed},
                {-android.R.attr.state_pressed, -android.R.attr.state_checked},
                {-android.R.attr.state_pressed, android.R.attr.state_checked}},
                //new int[]{Color.GRAY, mTintColor, mCheckedTextColor});
                //new int[]{Color.GRAY, Color.GRAY, Color.GRAY});
                new int[]{Color.GRAY, mUnCheckedTextColor, mCheckedTextColor});


        ((Button) view).setTextColor(colorStateList);

        //Redraw with tint color
        Drawable checkedDrawable = resources.getDrawable(checked).mutate();
        Drawable uncheckedDrawable = resources.getDrawable(unchecked).mutate();
        ((GradientDrawable) checkedDrawable).setColor(mTintColor);
        ((GradientDrawable) uncheckedDrawable).setStroke(oneDP, mTintColor);

        //Create drawable
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_checked}, uncheckedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);

        //Set button background
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(stateListDrawable);
        } else {
            view.setBackgroundDrawable(stateListDrawable);
        }
    }
}

