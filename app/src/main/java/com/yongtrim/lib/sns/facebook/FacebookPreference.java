package com.yongtrim.lib.sns.facebook;

import android.content.Context;

import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * Hair / com.yongtrim.lib.sns.facebook
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class FacebookPreference extends BasePreferenceUtil {
    private static FacebookPreference mInstance = null;

    private static final String PROPERTY_ISPOSTABLE = "facebook_ispostable";
    private static final String PROPERTY_ISLOGIN = "facebook_islogin";

    public static synchronized FacebookPreference getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new FacebookPreference(context);
        }
        return mInstance;
    }

    protected FacebookPreference(Context context) {
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

