package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.nuums.nuums.R;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yongtrim.lib.util.PixelUtil;

/**
 * hair / com.yongtrim.lib.ui
 * <p/>
 * Created by Uihyun on 15. 9. 16..
 */
public class CircularNetworkImageView extends CustomNetworkImageView {

    int strokeColor;
    float strokeWidth;

    public CircularNetworkImageView(Context context) {
        super(context);
        setStrokeColorAndWidth(ContextCompat.getColor(getContext(), R.color.gray), 1);
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        setStrokeColorAndWidth(ContextCompat.getColor(getContext(), R.color.gray), 1);
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setStrokeColorAndWidth(ContextCompat.getColor(getContext(), R.color.gray), 1);
    }


    public void setStrokeColorAndWidth(@ColorInt int color, float width) {
        this.strokeColor = color;
        this.strokeWidth = width;

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm==null) return;
        setImageDrawable(new BitmapDrawable(getContext().getResources(),
                getCircularBitmap(bm)));
    }

    /**
     * Creates a circular bitmap and uses whichever dimension is smaller to determine the width
     * <br/>Also constrains the circle to the leftmost part of the image
     *
     * @param bitmap
     * @return bitmap
     */
    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int width = bitmap.getWidth();
        if(bitmap.getWidth()>bitmap.getHeight())
            width = bitmap.getHeight();
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, width);
        final RectF rectF = new RectF(rect);
        final float roundPx = width / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if(strokeWidth > 0) {
            paint.setColor(strokeColor);
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        }


        return output;
    }

}
