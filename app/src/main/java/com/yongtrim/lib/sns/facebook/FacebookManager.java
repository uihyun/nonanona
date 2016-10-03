package com.yongtrim.lib.sns.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.android.volley.Response;
import com.nuums.nuums.model.user.NsUser;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.model.user.SnsInfo;
import com.yongtrim.lib.model.user.User;
import com.yongtrim.lib.model.user.UserData;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.message.PushMessage;
import com.yongtrim.lib.sns.SNSLoginListener;
import com.yongtrim.lib.sns.SNSLoginoutListener;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * Hair / com.yongtrim.lib.sns.facebook
 * <p/>
 * Created by Uihyun on 15. 9. 14..
 */
public class FacebookManager {

    final static String TAG = "FacebookManager";
    private static FacebookManager mInstance;
    protected FacebookPreference facebookPreference;

    private SimpleFacebook mSimpleFacebook;

    SNSLoginListener mLoginListner;

    ContextHelper contextHelper;


    public static FacebookManager getInstance(ContextHelper contextHelper){
        if(mInstance == null) {
            mInstance = new FacebookManager();


        }
        mInstance.contextHelper = contextHelper;
//            mInstance.mContext = helper.context;
//            mInstance.mActivity = helper.activity;
        mInstance.facebookPreference = FacebookPreference.getInstance(contextHelper.getContext());
        mInstance.mSimpleFacebook = SimpleFacebook.getInstance(contextHelper.getActivity());

        return mInstance;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
    }


    public void login(SNSLoginListener listener, final boolean needLocalLogin) {
        mLoginListner = listener;

        mSimpleFacebook.login(new OnLoginListener() {

            @Override
            public void onFail(String reason) {
                //--mTextStatus.setText(reason);
                //--Log.w(TAG, "Failed to login");
                //Logger.debug(TAG, "onLoginListener() | onFail()");
                //Logger.trace(TAG, "onFail : " + reason);

                mLoginListner.fail();
            }

            @Override
            public void onException(Throwable throwable) {
                //--mTextStatus.setText("Exception: " + throwable.getMessage());
                //--Log.e(TAG, "Bad thing happened", throwable);
                //Logger.debug(TAG, "onLoginListener() | onException()");
                mLoginListner.fail();
            }

            @Override
            public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {

                Profile.Properties properties = new Profile.Properties.Builder()
                        .add(Profile.Properties.ID)
                        .build();

                SimpleFacebook.getInstance().getProfile(properties, new OnProfileListener() {

                    @Override
                    public void onThinking() {
                    }

                    @Override
                    public void onException(Throwable throwable) {

                        mLoginListner.fail();
                    }

                    @Override
                    public void onFail(String reason) {

                        mLoginListner.fail();
                    }

                    @Override
                    public void onComplete(Profile response) {

                        final String facebookId = response.getId();

                        //Logger.trace(TAG, "facebookId = " + facebookId);
                        if (needLocalLogin) {
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                contextHelper.showProgress("로그인 중입니다.");
                                                LoginManager.getInstance(contextHelper).login(
                                                        User.LOGINTYPE_FACEBOOK,
                                                        facebookId,
                                                        facebookId,
                                                        new Response.Listener<UserData>() {
                                                            @Override
                                                            public void onResponse(final UserData response) {

                                                                facebookPreference.putIsLogin(true);
                                                                facebookPreference.putIsPostable(true);

                                                                if (response.isSuccess()) {
                                                                    LoginManager.getInstance(contextHelper).setLoginStatus(response.user);
                                                                    UserManager.getInstance(contextHelper).setMe(response.user);


                                                                    final CountDownLatch latchFriends = new CountDownLatch(0);
                                                                    //updateFriends(latchFriends);
                                                                    new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            try {
                                                                                latchFriends.await();
                                                                                EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGE_ME).setObject(response.user));
                                                                                mLoginListner.success(true, facebookId);
                                                                            } catch (Exception e) {
                                                                            }
                                                                        }
                                                                    }).start();

                                                                } else {
                                                                    mLoginListner.successAndNeedRegist();
                                                                }

                                                                contextHelper.hideProgress();
                                                            }
                                                        },
                                                        null
                                                );
                                            }
                                        });


                                    } catch (Exception e) {

                                    }
                                }
                            }).start();
                        } else {

                            final CountDownLatch latchFriends = new CountDownLatch(0);
                            //updateFriends(latchFriends);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        latchFriends.await();

                                        contextHelper.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                facebookPreference.putIsLogin(true);
                                                facebookPreference.putIsPostable(true);
                                                mLoginListner.success(true, facebookId);
                                            }
                                        });

                                    } catch (Exception e) {
                                    }
                                }
                            }).start();


                        }
                    }
                });

            }

            @Override
            public void onCancel() {
                //--mTextStatus.setText("Canceled");
                //Log.w(TAG, "Canceled the login");
                //Logger.debug(TAG, "onLoginListener() | onCancel()");
                mLoginListner.fail();
            }

        });
    }



    public void logout(final SNSLoginoutListener listener) {

        mSimpleFacebook.logout(new OnLogoutListener() {
            @Override
            public void onLogout() {
                facebookPreference.putIsLogin(false);
                facebookPreference.putIsPostable(false);
                listener.success(false);
            }
        });
    }


//    public void updateFriends(final CountDownLatch latchFriends) {
//        Profile.Properties properties = new Profile.Properties.Builder()
//                .add(Profile.Properties.ID)
//                .add(Profile.Properties.NAME)
//                .build();
//        SimpleFacebook.getInstance().getFriends(properties, new OnFriendsListener() {
//
//            @Override
//            public void onThinking() {
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                latchFriends.countDown();
//            }
//
//            @Override
//            public void onFail(String reason) {
//                latchFriends.countDown();
//            }
//
//            @Override
//            public void onComplete(List<Profile> response) {
//
//                ArrayList<String> arrFrends = new ArrayList<>();
//
//                if(response != null) {
//
//                    for(Profile profile : response) {
//                        arrFrends.add(profile.getId());
//                    }
//                }
//                final NsUser user = UserManager.getInstance(contextHelper).getMe();
//                SnsInfo info = user.getFacebookInfo();
//                info.setSnsFriends(arrFrends);
//                info.setSnsId(user.getUsername());
//
//                final CountDownLatch latchUpdate = new CountDownLatch(1);
//                contextHelper.updateUser(user, null, latchUpdate);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            latchUpdate.await();
//                            latchFriends.countDown();
//                        } catch (Exception e) {
//                        }
//                    }
//                }).start();
//
//            }
//        });
//    }



}
