package com.yongtrim.lib.model.user;

import android.content.Context;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.message.MessageManager;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.util.BasePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class LoginManager {
    public static String LOGINSTATUS_NONE = "NONE"; // 최초 실행
    public static String LOGINSTATUS_LOGIN = "LOGIN"; // 멤버가 로그인 된 상태
    public static String LOGINSTATUS_LOGOUT = "LOGOUT"; // 게스트가 로그인 된 상태
    private static LoginManager instance;
    private final String TAG = getClass().getSimpleName();
    ContextHelper contextHelper;
    private LoginPreference preference;
    private OnLogoutListener mOnLogoutListener;

    public static LoginManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new LoginManager();
        }
        if (instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }

    public void setPreference(ContextHelper contextHelper) {
        preference = new LoginPreference(contextHelper.getContext());
    }

    public String getAccessToken() {
        if (getLoginStatus().equals(LOGINSTATUS_LOGOUT))
            return preference.getAccessToken(true);
        else if (getLoginStatus().equals(LOGINSTATUS_LOGIN))
            return preference.getAccessToken(false);
        return null;
    }

    public String getLoginStatus() {
        return preference.getLoginStatus();
    }

    public void setLoginStatus(NsUser user) {
        if (user != null) {
            if (user.getLoginType().equals(User.LOGINTYPE_GUEST)) {
                preference.putLoginStatus(LOGINSTATUS_LOGOUT);
                preference.putAccessToken(user.getAccessToken(), true);
            } else {
                preference.putLoginStatus(LOGINSTATUS_LOGIN);

                if (user.getLoginType().equals(User.LOGINTYPE_EMAIL)) {
                    preference.putRecentLoginUsername(user.getUsername());
                }

                preference.putAccessToken(user.getAccessToken(), false);

            }
        } else {
            preference.putLoginStatus(LOGINSTATUS_NONE);
        }
    }

    public String getRecentLoginUsername() {
        return preference.getRecentLoginUsername();
    }

    public void setInavalidCurrentUser() {
        if (getLoginStatus().equals(LOGINSTATUS_LOGIN)) {
            preference.putLoginStatus(LOGINSTATUS_LOGOUT);
        } else {
            preference.putLoginStatus(LOGINSTATUS_NONE);
        }
    }

    void patchUser(final CountDownLatch latchWait, final CountDownLatch latchCount) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (latchWait != null)
                        latchWait.await();

                    mOnLogoutListener.onLogout(UserManager.getInstance(contextHelper).getMe());

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void findPassword(final String email,
                             final Response.Listener<UserData> listener,
                             final Response.ErrorListener errorListener
    ) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/login/forgot");

        JSONObject body = new JSONObject();

        try {
            body.put("email", email);

        } catch (JSONException e) {
        }

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                UserData.class,
                body,
                listener,
                errorListener);


        RequestManager.getRequestQueue().add(request);
    }

    public void login(String loginType,
                      String username,
                      String password,
                      final Response.Listener<UserData> listener,
                      final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/login");
//
//        loginType = User.LOGINTYPE_FACEBOOK;
//        username = "880889018633650";
//        password = "880889018633650";


        JSONObject body = new JSONObject();

        try {
            body.put("loginType", loginType);
            body.put("username", username);

            if (loginType.equals(User.LOGINTYPE_GUEST)) {
                body.put("password", "temp");
            } else {
                body.put("password", password);
            }

            body.put("deviceType", "android");
            body.put("deviceToken", MessageManager.getInstance(contextHelper).getRegistrationId());

            if (UserManager.getInstance(contextHelper).getMe() != null && UserManager.getInstance(contextHelper).getMe().getLoginType().equals("GUEST")) {
                body.put("guest", UserManager.getInstance(contextHelper).getMe().getUsername());
            }


            JSONObject deviceInfo = new JSONObject();

            deviceInfo.put("OS_Version", System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
            deviceInfo.put("OS_API_Level", android.os.Build.VERSION.SDK_INT);
            deviceInfo.put("Device", android.os.Build.DEVICE);
            deviceInfo.put("Model_Product", android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")");

            DisplayMetrics dm = new DisplayMetrics();
            contextHelper.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            deviceInfo.put("widthPixels", dm.widthPixels);
            deviceInfo.put("heightPixels", dm.heightPixels);
            deviceInfo.put("densityDpi", dm.densityDpi);
            deviceInfo.put("density", dm.density);

            body.put("deviceInfo", deviceInfo);

        } catch (JSONException e) {
        }

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                UserData.class,
                body,
                listener,
                errorListener);


        RequestManager.getRequestQueue().add(request);
    }

    public void logout(
            final Response.Listener<UserData> listener,
            final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/logout");

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                UserData.class,
                null,
                listener,
                errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    public void logout(OnLogoutListener listener) {
        mOnLogoutListener = listener;

        preference.putLoginStatus(LOGINSTATUS_LOGOUT);

        CountDownLatch latchUserInfo = new CountDownLatch(1);
        contextHelper.userInfo(null, latchUserInfo);

        final CountDownLatch latchPatch = new CountDownLatch(1);
        patchUser(latchUserInfo, latchPatch);

    }


    public interface OnLogoutListener {
        public void onLogout(NsUser userGuest);
    }

    private class LoginPreference extends BasePreferenceUtil {
        private static final String PROPERTY_ACCESSTOKEN_FOR_GUEST = "login_accesstoken_for_guest";
        private static final String PROPERTY_ACCESSTOKEN_FOR_MEMBER = "login_accesstoken_for_member";
        private static final String PROPERTY_LOGINSTATUS = "login_loginstatus";
        private static final String PROPERTY_RECENT_LOGINUSERNAME = "login_recentloginusername";


        private LoginPreference(Context context) {
            super(context);
        }

        private void putLoginStatus(String loginType) {
            put(PROPERTY_LOGINSTATUS, loginType);
        }

        private String getLoginStatus() {
            return get(PROPERTY_LOGINSTATUS, LOGINSTATUS_NONE);
        }

        private void putRecentLoginUsername(String username) {
            put(PROPERTY_RECENT_LOGINUSERNAME, username);
        }

        private String getRecentLoginUsername() {
            return get(PROPERTY_RECENT_LOGINUSERNAME);
        }

        private void putAccessToken(String accessToken, boolean isGuest) {
            if (isGuest) {
                put(PROPERTY_ACCESSTOKEN_FOR_GUEST, accessToken);
            } else {
                put(PROPERTY_ACCESSTOKEN_FOR_MEMBER, accessToken);
            }
        }

        private String getAccessToken(boolean isGuest) {
            if (isGuest) {
                return get(PROPERTY_ACCESSTOKEN_FOR_GUEST);
            } else {
                return get(PROPERTY_ACCESSTOKEN_FOR_MEMBER);
            }
        }

    }

}
