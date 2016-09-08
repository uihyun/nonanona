package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 9. 16..
 */
public class CustomNetworkImageView  extends NetworkImageView {

    private Bitmap mLocalBitmap;

    private boolean mShowLocal;

    public CustomNetworkImageView(Context context) {
        this(context, null);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        int[] ATTRS = new int[] {
                android.R.attr.background,
        };

//        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
//        int backgroundResId = a.getResourceId(0, R.drawable.ic_profile_photo);
//        setBackgroundResource(backgroundResId);
    }


    public void setLocalImageBitmap(Bitmap bitmap) {
        mShowLocal = bitmap != null;
        this.mLocalBitmap = bitmap;
        requestLayout();
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mShowLocal = false;
        super.setImageUrl(url, imageLoader);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        if (mShowLocal) {
            setImageBitmap(mLocalBitmap);
        }
    }

    public void setRatio(int xRatio, int yRatio) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float height = 0;
        float width = 0;

        if(metrics.widthPixels < metrics.heightPixels){
            width = metrics.widthPixels;
            height= (float)(metrics.widthPixels/(float)xRatio) * (float)yRatio;



        } else {
            height= metrics.heightPixels;
            width= (float)(metrics.heightPixels/(float)xRatio) * (float)yRatio;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)width, (int)height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        setLayoutParams(layoutParams);

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)width, (int)height);
//        setLayoutParams(layoutParams);
    }

}
