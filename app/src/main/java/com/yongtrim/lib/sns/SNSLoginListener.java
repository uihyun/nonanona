package com.yongtrim.lib.sns;

import com.nuums.nuums.model.misc.Sns;

/**
 * Hair / com.yongtrim.lib.sns
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public interface SNSLoginListener {
    public void success(boolean isLogin, Sns sns);

    public void fail();

    public void successAndNeedRegist();
}
