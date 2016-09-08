package com.yongtrim.lib.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nuums.nuums.R;
import com.yongtrim.lib.util.PixelUtil;

/**
 * nuums / com.yongtrim.lib.ui
 * <p/>
 * Created by yongtrim.com on 15. 12. 26..
 */
public class DiagonalLineView extends View {

    private int dividerColor;
    private Paint paint;

    public DiagonalLineView(Context context)
    {
        super(context);
        init(context);
    }

    public DiagonalLineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public DiagonalLineView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        dividerColor = ContextCompat.getColor(context, R.color.white);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(dividerColor);
        paint.setStrokeWidth(PixelUtil.dpToPx(context, 1));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawLine(PixelUtil.dpToPx(getContext(), 1), 0, getWidth() - PixelUtil.dpToPx(getContext(), 1), getHeight(), paint);
    }

}
