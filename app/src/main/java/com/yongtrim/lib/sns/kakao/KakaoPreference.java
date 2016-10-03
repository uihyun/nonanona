package com.yongtrim.lib.sns.kakao;

import android.content.Context;

import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * Hair / com.yongtrim.lib.sns.kakao
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class KakaoPreference extends BasePreferenceUtil {
    private static KakaoPreference mInstance = null;

    private static final String PROPERTY_ISPOSTABLE = "kakao_ispostable";
    private static final String PROPERTY_ISLOGIN = "kakao_islogin";

    public static synchronized KakaoPreference getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new KakaoPreference(context);
        }
        return mInstance;
    }

    protected KakaoPreference(Context context) {
        super(context);
    }

    public void putIsPostable(boolean isPostable) {
        put(PROPERTY_ISPOSTABLE, isPostable);
    }

    public boolean isPostable() {
        return get(PROPERTY_ISPOSTABLE, false);
    }

    public void putIsLogin(boolean isLogin) {
        put(PROPERTY_ISLOGIN, isLogin);
    }

    public boolean isLogin() {
        return get(PROPERTY_ISLOGIN, false);
    }


}

