package com.yongtrim.lib.sns.twitter;

import android.content.Context;

import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * Hair / com.yongtrim.lib.sns.twitter
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class TwitterPreference extends BasePreferenceUtil {
    private static TwitterPreference mInstance = null;

    private static final String PROPERTY_ISPOSTABLE = "twitter_ispostable";
    private static final String PROPERTY_ISLOGIN = "twitter_islogin";

    public static synchronized TwitterPreference getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new TwitterPreference(context);
        }
        return mInstance;
    }

    protected TwitterPreference(Context context) {
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

