package com.yongtrim.lib.sns;

/**
 * Hair / com.yongtrim.lib.sns
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public interface SNSLoginListener {
    public void success(boolean isLogin, String snsId);
    public void fail();
    public void successAndNeedRegist();
}
