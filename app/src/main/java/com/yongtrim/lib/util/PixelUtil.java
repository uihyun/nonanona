package com.yongtrim.lib.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * hair / com.yongtrim.lib.util
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class PixelUtil {
    private PixelUtil() {
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((px/displayMetrics.density) + 0.5);
    }

//    private static float getPixelScaleFactor(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
//    }


}

