package com.yongtrim.lib.sns.kakao;

import android.content.Intent;

import com.kakao.auth.APIErrorResult;
import com.kakao.auth.Session;
import com.kakao.auth.SessionCallback;
import com.kakao.usermgmt.LogoutResponseCallback;
import com.kakao.usermgmt.MeResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.logger.Logger;
import com.yongtrim.lib.sns.SNSLoginListener;
import com.yongtrim.lib.sns.SNSLoginoutListener;

/**
 * Hair / com.yongtrim.lib.sns.kakao
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class KakaoManager {
    private static KakaoManager mInstance;
    final String TAG = "KakaoManager";
    protected KakaoPreference kakaoPreference;
    SNSLoginListener mLoginListener;
    boolean isUpdate;
    boolean nonaLogin;
    ContextHelper mHelper;
    private MySessionStatusCallback mySessionCallback;
    private Session session;

    public KakaoManager() {
        session = Session.getCurrentSession();
        mySessionCallback = new MySessionStatusCallback();
        session.addCallback(mySessionCallback);
    }

    public static KakaoManager getInstance(ContextHelper helper, SNSLoginListener listener, final boolean isUpdate, final boolean nonaLogin) {
        if (mInstance == null) {
            mInstance = new KakaoManager();
        }

        mInstance.mHelper = helper;
//            mInstance.mContext = helper.context;
//            mInstance.mActivity = helper.activity;
        mInstance.mLoginListener = listener;
        mInstance.isUpdate = isUpdate;
        mInstance.nonaLogin = nonaLogin;

        mInstance.kakaoPreference = KakaoPreference.getInstance(helper.getContext());
        return mInstance;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    public void logout(final SNSLoginoutListener listener) {
        kakaoPreference.putIsLogin(false);
        kakaoPreference.putIsPostable(false);

        if (!session.isClosed()) {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onSuccess(final long userId) {
                    listener.success(false);
                }

                @Override
                public void onFailure(final APIErrorResult apiErrorResult) {
                    listener.success(false);
                }
            });
        } else {
            listener.success(false);
        }

    }

    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onSuccess(final UserProfile userProfile) {
                Logger.debug(TAG, "UserProfile : " + userProfile);
                userProfile.saveUserToCache();


            }

            @Override
            public void onNotSignedUp() {
                if (mLoginListener != null)
                    mLoginListener.fail();
            }

            @Override
            public void onSessionClosedFailure(final APIErrorResult errorResult) {
                if (mLoginListener != null)
                    mLoginListener.fail();
            }

            @Override
            public void onFailure(final APIErrorResult errorResult) {
                if (mLoginListener != null)
                    mLoginListener.fail();
            }
        });
    }

    private class MySessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            requestMe();

            if (isUpdate) {
                kakaoPreference.putIsLogin(nonaLogin);
                kakaoPreference.putIsPostable(true);
                if (mLoginListener != null) {
                    mLoginListener.success(true, null);
                }
            }
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            // 프로그레스바 종료
            // 세션 오픈을 못했으니 다시 로그인 버튼 노출.
//            loginButton.setVisibility(View.VISIBLE);
            //Logger.debug(TAG, "onSessionClosed()");
            if (mLoginListener != null)
                mLoginListener.fail();
        }

        @Override
        public void onSessionOpening() {
            //프로그레스바 시작
            Logger.debug(TAG, "onSessionOpening()");
        }
    }

}
