package com.nuums.nuums.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kakao.usermgmt.LoginButton;
import com.nuums.nuums.R;

/**
 * nuums / com.nuums.nuums.view
 * <p/>
 * Created by yongtrim.com on 16. 1. 19..
 */
public class KakaoButton extends LoginButton {

    View view;
    public KakaoButton(Context context) {
        super(context);
    }

    public KakaoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KakaoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeAllViews();
        view = inflate(getContext(), R.layout.view_kakaobutton, this);
    }


}



