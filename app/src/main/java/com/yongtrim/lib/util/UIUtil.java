package com.yongtrim.lib.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * hair / com.yongtrim.lib.util
 * <p/>
 * Created by yongtrim.com on 15. 9. 23..
 */
public class UIUtil {
    public static void setRatio(View view, Context context, int xRatio, int yRatio) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float height = 0;
        float width = 0;

        if(metrics.widthPixels < metrics.heightPixels){
            width = metrics.widthPixels;
            height= (float)(metrics.widthPixels/(float)xRatio) * (float)yRatio;
        } else {
            height= metrics.heightPixels;
            width= (float)(metrics.heightPixels/(float)xRatio) * (float)yRatio;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)width, (int)height);
        view.setLayoutParams(layoutParams);
    }

    public static void setDivided(View[] views, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float width = metrics.widthPixels/views.length;
        float height = width;

        for(int i = 0;i < views.length;i++) {
            View view = views[i];

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
            layoutParams.width = (int)width;
            layoutParams.height = (int)height;
            view.setLayoutParams(layoutParams);
        }
    }
}
