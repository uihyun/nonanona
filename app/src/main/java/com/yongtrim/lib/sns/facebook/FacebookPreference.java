package com.yongtrim.lib.sns.facebook;

import android.content.Context;

import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * Hair / com.yongtrim.lib.sns.facebook
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class FacebookPreference extends BasePreferenceUtil {
    private static final String PROPERTY_ISPOSTABLE = "facebook_ispostable";
    private static final String PROPERTY_ISLOGIN = "facebook_islogin";
    private static final String PROPERTY_FACEBOOK_ID = "facebook_id";
    private static final String PROPERTY_FACEBOOK_EMAIL = "facebook_email";

    private static FacebookPreference mInstance = null;

    protected FacebookPreference(Context context) {
        super(context);
    }

    public static synchronized FacebookPreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacebookPreference(context);
        }
        return mInstance;
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

    public void putFacebookId(String id) {
        put(PROPERTY_FACEBOOK_ID, id);
    }

    public String getFacebookId() {
        return get(PROPERTY_FACEBOOK_ID, "");
    }

    public void putFacebookEmail(String email) {
        put(PROPERTY_FACEBOOK_EMAIL, email);
    }

    public String getFacebookEmail() {
        return get(PROPERTY_FACEBOOK_EMAIL, "");
    }
}

