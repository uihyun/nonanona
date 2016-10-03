package com.yongtrim.lib.sns.googleplus;

import android.content.Context;

import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * Hair / com.yongtrim.lib.sns.googleplus
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class GooglePlusPreference extends BasePreferenceUtil {
    private static GooglePlusPreference mInstance = null;

    private static final String PROPERTY_ISPOSTABLE = "googleplus_ispostable";
    private static final String PROPERTY_ISLOGIN = "googleplus_islogin";

    public static synchronized GooglePlusPreference getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new GooglePlusPreference(context);
        }
        return mInstance;
    }

    protected GooglePlusPreference(Context context) {
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
