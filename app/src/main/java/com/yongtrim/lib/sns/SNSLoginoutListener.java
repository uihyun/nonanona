package com.yongtrim.lib.sns;

/**
 * Hair / com.yongtrim.lib.sns
 * <p/>
 * Created by yongtrim.com on 15. 9. 14..
 */
public interface SNSLoginoutListener {
    public void success(boolean isLogin);
    public void fail();
}
