package com.yongtrim.lib.message;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Configuration;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.fragment.SplashFragment;
import com.yongtrim.lib.logger.Logger;
import com.yongtrim.lib.util.BasePreferenceUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by studioyongtrim on 15. 8. 17..
 * <p>
 * reference : http://susemi99.kr/1012
 */
public class MessageManager {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static MessageManager instance;
    private final String TAG = "MessageManager";
    private GoogleCloudMessaging gcm;
    private String regId;
    private MessagePreference preference;
    private ContextHelper contextHelper;

    public static MessageManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new MessageManager();
        }

        if (instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }

    public MessagePreference getPreference() {
        return preference;
    }

    public void setPreference(ContextHelper contextHelper) {
        preference = new MessagePreference(contextHelper.getContext());
    }

    public void initialize(final CountDownLatch latchWait, final CountDownLatch latchCount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (latchWait != null)
                        latchWait.await();

                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(contextHelper.getContext());
                        regId = getRegistrationId();

                        if (TextUtils.isEmpty(regId)) {
                            registerInBackground(latchCount);
                        } else {
                            latchCount.countDown();
                        }
                    }

                } catch (Exception e) {

                }
            }
        }).start();


    }


    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contextHelper.getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contextHelper.getContext());
                if (status != ConnectionResult.SUCCESS) {
                    if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, contextHelper.getActivity(),
                                        SplashFragment.PLAY_SERVICES_RESOLUTION_REQUEST);
                                dialog.setCancelable(false);
                                dialog.show();

                            }
                        }, 0);

                    } else {
                        Toast.makeText(contextHelper.getContext(), "이 단말기는 지원하지 않습니다.",
                                Toast.LENGTH_LONG).show();
                        contextHelper.getActivity().finish();
                    }
                }
            } else {
                Logger.info(TAG, "checkPlayServices() | This device is not supported. |");
                contextHelper.getActivity().finish();
            }
            return false;
        }
        return true;
    }


    public String getRegistrationId() {
        String registrationId = preference.regId();
        if (TextUtils.isEmpty(registrationId)) {
            Logger.info(TAG, "getRegistrationId() | Registration not found. |");
            return "";
        }

        return registrationId;
    }


    private void registerInBackground(final CountDownLatch latchCount) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(contextHelper.getContext());
                    }
                    regId = gcm.register(Configuration.PROJECT_ID);
                    msg = "Device registered, registration ID = " + regId;

                    preference.putRegId(regId);

                } catch (IOException ex) {
                    msg = "Error : " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.info(TAG, "onPostExecute() | " + msg + " |");
                latchCount.countDown();
            }
        }.execute(null, null, null);
    }


    public class MessagePreference extends BasePreferenceUtil {

        private static final String PROPERTY_REG_ID = "registration_id";
        private static final String PROPERTY_PUSH = "gcm_push"; //0:off 1:on
        private static final String CUR_CHATTER = "gcm_cur_chatter";


        private static final String PROPERTY_NANUM_COMMENT = "property_nanum_comment"; // 나눔글에 댓글이 달렸을 때
        private static final String PROPERTY_NANUM_WON = "property_nanum_won"; //나눔에 당첨되었을 때
        private static final String PROPERTY_NANUM_WARN = "property_nanum_warn"; //나눔 마감전 1시간 알림
        private static final String PROPERTY_NANUM_KEYWORD = "property_nanum_keyword"; //관심 키워드 나눔 알림
        private static final String PROPERTY_NANUM_ZZIM = "propety_nanum_zzim"; //나눔이 찜 되었을 때

        private static final String PROPERTY_DELIVERY = "property_delivery"; //배송 진행시
        private static final String PROPERTY_TALK = "property_talk"; //톡 메시지를 받았을 때

        private static final String PROPERTY_REVIEW_COMMENT = "property_review_comment";
        private static final String PROPERTY_REVIEW_TAG = "property_review_tag"; //게시글에 태그 되었을 때

        private static final String PROPERTY_EVENT_START = "property_event_start";
        private static final String PROPERTY_EVENT_FINISH = "property_event_finish";


        protected MessagePreference(Context context) {
            super(context);
        }

        public void putRegId(String regId) {
            put(PROPERTY_REG_ID, regId);
        }

        public String regId() {
            return get(PROPERTY_REG_ID);
        }

        public void putPush(int push) {
            put(PROPERTY_PUSH, push);
        }

        public int getPush() {
            return get(PROPERTY_PUSH, 1);
        }


        public void putAlarm(NsUser me, NsUser target, boolean isOn) {
            put(me.getId() + "-" + target.getId(), isOn);
        }


        public boolean getIsAlarmOn(NsUser me, NsUser target) {
            return get(me.getId() + "-" + target.getId(), true);
        }


        public void putCurChatter(NsUser chatter) {
            if (chatter == null)
                put(CUR_CHATTER, null);
            else
                put(CUR_CHATTER, chatter.toString());
        }

        public NsUser getCurChatter() {
            String str = get(CUR_CHATTER);
            if (TextUtils.isEmpty(str)) {
                return null;
            } else {
                return NsUser.getUser(str);
            }
        }

        public void putNanumComment(boolean isOn) {
            put(PROPERTY_NANUM_COMMENT, isOn);
        }

        public boolean getIsNanumComment() {
            return get(PROPERTY_NANUM_COMMENT, true);
        }

        public void putNanumWon(boolean isOn) {
            put(PROPERTY_NANUM_WON, isOn);
        }

        public boolean getIsNanumWon() {
            return get(PROPERTY_NANUM_WON, true);
        }


        public void putNanumWarn(boolean isOn) {
            put(PROPERTY_NANUM_WARN, isOn);
        }

        public boolean getIsNanumWarn() {
            return get(PROPERTY_NANUM_WARN, true);
        }

        public void putNanumKeyword(boolean isOn) {
            put(PROPERTY_NANUM_KEYWORD, isOn);
        }

        public boolean getIsNanumKeyword() {
            return get(PROPERTY_NANUM_KEYWORD, true);
        }

        public void putNanumZzim(boolean isOn) {
            put(PROPERTY_NANUM_ZZIM, isOn);
        }

        public boolean getIsNanumZzim() {
            return get(PROPERTY_NANUM_ZZIM, true);
        }

        public void putDelivery(boolean isOn) {
            put(PROPERTY_DELIVERY, isOn);
        }

        public boolean getIsDelivery() {
            return get(PROPERTY_DELIVERY, true);
        }


        public void putTalk(boolean isOn) {
            put(PROPERTY_TALK, isOn);
        }

        public boolean getIsTalk() {
            return get(PROPERTY_TALK, true);
        }


        public void putReviewComment(boolean isOn) {
            put(PROPERTY_REVIEW_COMMENT, isOn);
        }

        public boolean getIsReviewComment() {
            return get(PROPERTY_REVIEW_COMMENT, true);
        }


        public void putReviewTag(boolean isOn) {
            put(PROPERTY_REVIEW_TAG, isOn);
        }

        public boolean getIsReviewTag() {
            return get(PROPERTY_REVIEW_TAG, true);
        }

        public void putEventStart(boolean isOn) {
            put(PROPERTY_EVENT_START, isOn);
        }

        public boolean getIsEventStart() {
            return get(PROPERTY_EVENT_START, true);
        }

        public void putEventFinish(boolean isOn) {
            put(PROPERTY_EVENT_FINISH, isOn);
        }

        public boolean getIsEventFinish() {
            return get(PROPERTY_EVENT_FINISH, true);
        }
    }
}
