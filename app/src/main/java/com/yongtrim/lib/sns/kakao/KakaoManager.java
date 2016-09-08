package com.yongtrim.lib.sns.kakao;

import android.content.Intent;


import com.android.volley.Response;
import com.kakao.auth.APIErrorResult;
import com.kakao.auth.Session;
import com.kakao.auth.SessionCallback;
import com.kakao.usermgmt.LogoutResponseCallback;
import com.kakao.usermgmt.MeResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.sns.SNSLoginListener;
import com.yongtrim.lib.sns.SNSLoginoutListener;

import de.greenrobot.event.EventBus;

/**
 * Hair / com.yongtrim.lib.sns.kakao
 * <p/>
 * Created by yongtrim.com on 15. 9. 14..
 */
public class KakaoManager {
    final String TAG = "KakaoManager";

    private static KakaoManager mInstance;
    protected KakaoPreference kakaoPreference;

    private MySessionStatusCallback mySessionCallback;

    private Session session;

    SNSLoginListener mLoginListener;

    boolean isUpdate;
    ContextHelper mHelper;

    public static KakaoManager getInstance(ContextHelper helper, SNSLoginListener listener, final boolean isUpdate) {
        if(mInstance == null) {
            mInstance = new KakaoManager();
        }

        mInstance.mHelper = helper;
//            mInstance.mContext = helper.context;
//            mInstance.mActivity = helper.activity;
        mInstance.mLoginListener = listener;
        mInstance.isUpdate = isUpdate;

        mInstance.kakaoPreference = KakaoPreference.getInstance(helper.getContext());
        return mInstance;
    }


    public KakaoManager() {
        session = Session.getCurrentSession();
        mySessionCallback = new MySessionStatusCallback();
        session.addCallback(mySessionCallback);
    }


    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }


    private class MySessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바 종료

            // 세션 오픈후 보일 페이지로 이동
//            final Intent intent = new Intent(SampleLoginActivity.this, SampleSignupActivity.class);
//            startActivity(intent);
//            finish();
            //Logger.debug(TAG, "onSessionOpened()");

            if(isUpdate) {
                kakaoPreference.putIsLogin(true);
                kakaoPreference.putIsPostable(true);
                if(mLoginListener != null)
                    mLoginListener.success(true, null);
            }
//            else {
//                requestMe();
//            }
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            // 프로그레스바 종료
            // 세션 오픈을 못했으니 다시 로그인 버튼 노출.
//            loginButton.setVisibility(View.VISIBLE);
            //Logger.debug(TAG, "onSessionClosed()");
            if(mLoginListener != null)
                mLoginListener.fail();
        }

        @Override
        public void onSessionOpening() {
            //프로그레스바 시작
            Logger.debug(TAG, "onSessionOpening()");
        }
    }

//    private void requestMe() {
//        UserManagement.requestMe(new MeResponseCallback() {
//
//            @Override
//            public void onSuccess(final UserProfile userProfile) {
////                Logger.d("UserProfile : " + userProfile);
//                userProfile.saveUserToCache();
//                mHelper.showProgress("로그인 중입니다.");
//                LoginManager.getInstance(mHelper).login(
//                        User.LOGINTYPE_KAKAO,
//                        userProfile.getId() + "",
//                        userProfile.getId() + "",
//                        new Response.Listener<UserData>() {
//                            @Override
//                            public void onResponse(UserData response) {
//
//                                kakaoPreference.putIsLogin(true);
//                                kakaoPreference.putIsPostable(true);
//
//                                if (response.isSuccess()) {
//                                    LoginManager.getInstance(mHelper).setLoginStatus(response.user);
//                                    UserManager.getInstance(mHelper).setMe(response.user);
//                                    EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
//
//                                    if (mLoginListener != null)
//                                        mLoginListener.success(true, userProfile.getId() + "");
//
//                                } else {
//                                    if (mLoginListener != null)
//                                        mLoginListener.successAndNeedRegist();
//                                }
//                                mHelper.hideProgress();
//                            }
//                        },
//                        null
//                );
//            }
//
//            @Override
//            public void onNotSignedUp() {
//                if(mLoginListener != null)
//                    mLoginListener.fail();
//            }
//
//            @Override
//            public void onSessionClosedFailure(final APIErrorResult errorResult) {
//                if(mLoginListener != null)
//                    mLoginListener.fail();
//            }
//
//            @Override
//            public void onFailure(final APIErrorResult errorResult) {
//                if(mLoginListener != null)
//                    mLoginListener.fail();
//            }
//        });
//    }


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

}
