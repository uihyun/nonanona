package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuums.nuums.R;


/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by Uihyun on 15. 9. 16..
 */
public class UltraButton extends LinearLayout {

    public static final int POSITION_LEFT  		= 1;
    public static final int POSITION_RIGHT  	= 2;
    public static final int POSITION_TOP  		= 3;
    public static final int POSITION_BOTTOM  	= 4;
    public static final int POSITION_LEFTTOP  	= 5;

    RelativeLayout mViewGroup;
    private ImageView mIconView;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    private String mTextStyle = "";

    private ColorStateList mTint;


    private int mStyle = 0; // 0: 버튼을 가장자리에, 1:버튼과 텍스트를 중앙 정렬, 2:왼쪽으로 몰리게,

    //private int mCurrentBackgroundColor = Color.BLACK;
    private int mDefaultBackgroundColor = Color.BLACK;
    private int mFocusBackgroundColor = 0;
    private int mDisableBackgroundColor = 0;

    private int mDefaultTextColor = Color.WHITE;
    private int mDefaultTextSize = 15;
    private String mText = null;
    Typeface mDefaultTypeface = Typeface.DEFAULT;

    private int mBorderColor = Color.TRANSPARENT;
    private int mBorderWidth = 0;
    private int mRadius = 0;
    private int mIconMargin = 0;


    private int mIconPosition = POSITION_LEFT;
    private Drawable mIconResource = null;

    boolean isProgress;

    boolean isEnable = true;

    public UltraButton(Context context) {
        super(context);
        init();
    }

    public UltraButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.UltraButtonAttrs, 0, 0);
        initAttributs(attrsArray);
        attrsArray.recycle();

        init();
    }

    private void initAttributs(TypedArray attrsArray) {
        mStyle = attrsArray.getInt(R.styleable.UltraButtonAttrs_style, mStyle);

        try {
            mTint = attrsArray.getColorStateList(R.styleable.UltraButtonAttrs_iconTint);
        } catch(Exception e){
            mTint = null;
        }

        try {
            mTextStyle = attrsArray.getString(R.styleable.UltraButtonAttrs_textStyle);
        } catch (Exception e) {
        }

        try {
            mIconMargin = (int) attrsArray.getDimension(R.styleable.UltraButtonAttrs_iconMargin,mIconMargin);
        } catch(Exception e){
            mIconMargin = 0;
        }

        mDefaultTextColor = attrsArray.getColor(R.styleable.UltraButtonAttrs_textColor, mDefaultTextColor);
        mDefaultTextSize = (int)attrsArray.getDimension(R.styleable.UltraButtonAttrs_textSize, mDefaultTextSize);
        mText = attrsArray.getString(R.styleable.UltraButtonAttrs_text);

        mDefaultBackgroundColor = attrsArray.getColor(R.styleable.UltraButtonAttrs_defaultColor, mDefaultBackgroundColor);
        mFocusBackgroundColor = attrsArray.getColor(R.styleable.UltraButtonAttrs_focusColor, mFocusBackgroundColor);

        mDisableBackgroundColor = attrsArray.getColor(R.styleable.UltraButtonAttrs_focusColor, mDisableBackgroundColor);


        mBorderColor = attrsArray.getColor(R.styleable.UltraButtonAttrs_borderColor,mBorderColor);
        mBorderWidth = (int) attrsArray.getDimension(R.styleable.UltraButtonAttrs_borderWidth, mBorderWidth);

        mRadius = (int)attrsArray.getDimension(R.styleable.UltraButtonAttrs_radius, mRadius);

        mIconPosition = attrsArray.getInt(R.styleable.UltraButtonAttrs_iconPosition,mIconPosition);
        try {
            mIconResource = attrsArray.getDrawable(R.styleable.UltraButtonAttrs_iconResource);
        } catch(Exception e) {
        }
    }


    private void addTextView() {
        if(mTextView != null) {

            if(mViewGroup != null) {
                RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mTextView.setLayoutParams(textViewParams);
                mViewGroup.addView(mTextView);
            } else {
                addView(mTextView);
            }
        }

    }

    public void setTextViewAlign(boolean isLeft) {
        if(mViewGroup != null) {
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mTextView.setLayoutParams(textViewParams);
        }
    }

    private void addIconView() {
        if(mIconView != null) {
            if(mViewGroup != null) {
                RelativeLayout.LayoutParams iconViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                iconViewParams.addRule(RelativeLayout.CENTER_VERTICAL);

                if(mIconPosition == POSITION_RIGHT) {
                    iconViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else if(mIconPosition == POSITION_LEFTTOP) {
                    iconViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    iconViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                }
                mIconView.setLayoutParams(iconViewParams);
                mViewGroup.addView(mIconView);
            } else {
                LinearLayout.LayoutParams iconViewParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

                if(mIconPosition == POSITION_RIGHT) {
                    iconViewParams.leftMargin = mIconMargin;

                } else {
                    iconViewParams.rightMargin = mIconMargin;
                }
                mIconView.setLayoutParams(iconViewParams);

                addView(mIconView);
            }
        }
    }

    private void addProgressView() {
        if(mViewGroup != null) {
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mProgressBar.setLayoutParams(textViewParams);
            mViewGroup.addView(mProgressBar);
        } else {
            addView(mProgressBar);
        }
    }

    private void init() {
        initContainer();
        mTextView = setupTextView();
        mIconView = setupIconView();
        mProgressBar = setupProgressView();

        setupBackground();

        if(mStyle == 1 && mIconPosition == POSITION_RIGHT) {
            addTextView();
            addIconView();
        } else {
            addIconView();
            addTextView();
        }


        if(mStyle == 3)
            setTextViewAlign(true);

        addProgressView();
    }

    public TextView getTextView() {
        return mTextView;
    }

    ProgressBar setupProgressView() {
        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        return progressBar;
    }


    private TextView setupTextView() {
        if(mText == null)
            return null;

        TextView textView = new TextView(getContext());
        textView.setText(mText);

        textView.setTextColor(mDefaultTextColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDefaultTextSize);
        textView.setTypeface(mDefaultTypeface);

        if(mTextStyle != null && mTextStyle.equals("bold")) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }

        if(mStyle == 2 ) {
            textView.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            textView.setGravity(Gravity.CENTER);
        }


        return textView;

    }


    public void setGravity(boolean isCenter) {
        if(isCenter) {
            setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_VERTICAL);
        }
    }


    private ImageView setupIconView() {
        if(mIconResource == null)
            return null;
        ImageView iconView = new ImageView(getContext());
        iconView.setImageDrawable(mIconResource);

        if(mTint != null) {
            int color = mTint.getColorForState(getDrawableState(), 0);
            iconView.setColorFilter(color);
        }


        return iconView;

    }



    public void setIconColor(int color) {
        mIconView.setColorFilter(color);
    }


    private void setupBackground() {
        // Default Drawable
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(mRadius);

        if(isEnable) {
            drawable.setColor(mDefaultBackgroundColor);
        } else {
            drawable.setColor(mDisableBackgroundColor);
        }

        if (mBorderColor != 0) {
            drawable.setStroke(mBorderWidth, mBorderColor);
        }

        // Focus/Pressed Drawable
        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setCornerRadius(mRadius);
        drawable2.setColor(mFocusBackgroundColor);
        if (mBorderColor != 0) {
            drawable2.setStroke(mBorderWidth, mBorderColor);
        }

        StateListDrawable states = new StateListDrawable();

        if(mFocusBackgroundColor!=0){
            states.addState(new int[] { android.R.attr.state_pressed }, drawable2);
            states.addState(new int[] { android.R.attr.state_focused }, drawable2);
        }
        states.addState(new int[]{}, drawable);

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(states);
        } else {
            this.setBackground(states);
        }
    }

    private void initContainer() {

        if(mStyle == 0 || mStyle == 3) {
            mViewGroup = new RelativeLayout(getContext());
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            mViewGroup.setLayoutParams(containerParams);
            addView(mViewGroup);
        } else if(mStyle == 1 ) {
            setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        } else {

        }



        setClickable(true);
        setFocusable(true);
    }

    public void setText(String text){
        this.mText = text;
        if(mTextView == null) {
            init();
        }else{
            mTextView.setText(text);
        }
    }

    public String getText() {
        return this.mText;
    }

    public void setTextColor(int color){
        this.mDefaultTextColor = color;
        if(mTextView == null) {
            init();
        }else{
            mTextView.setTextColor(color);
        }
    }

    public void setTextStyleColor(Typeface tf){
        this.mDefaultTypeface = tf;
        if(mTextView == null) {
            init();
        }else{
            mTextView.setTypeface(tf);
        }
    }

    public void setIconResource(int drawable){
        if(drawable == 0) {
            mIconView.setVisibility(View.GONE);
            return;
        } else {
            mIconView.setVisibility(View.VISIBLE);
        }
        this.mIconResource = ContextCompat.getDrawable(getContext(), drawable);
        mIconView.setImageDrawable(mIconResource);
    }

    public void setIconVisible(int visibility) {
        mIconView.setVisibility(visibility);
    }


    public void setBackgroundColor(int color){
        this.mDefaultBackgroundColor = color;
        if(mTextView != null) {
            this.setupBackground();
        }
    }
    public void setFocusBackgroundColor(int color){
        this.mFocusBackgroundColor = color;
        if(mTextView!=null)
            this.setupBackground();

    }

    public void setDisableBackgroundColor(int color){
        this.mDisableBackgroundColor = color;
        if(mTextView!=null)
            this.setupBackground();
    }

    public void setTextSize(int textSize){
        this.mDefaultTextSize = textSize;
        if(mTextView!=null)
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setEnabled(boolean enable, boolean changeColor) {

        isEnable = enable;

        if(changeColor) {
            if (mDisableBackgroundColor == 0) {
                setDisableBackgroundColor(ContextCompat.getColor(getContext(), R.color.buttonsel_bg));
            }
            this.setupBackground();
        }

        super.setEnabled(enable);
    }


    public boolean isProgress() {
        return isProgress;
    }
    public void setProgressBar(boolean enable) {
        super.setEnabled(!enable);
        isProgress = enable;
        if(enable) {
            if(mIconView != null)
                mIconView.setVisibility(View.GONE);
            if(mTextView != null)
                mTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            if(mIconView != null)
                mIconView.setVisibility(View.VISIBLE);
            if(mTextView != null)
                mTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

        }
    }
}
