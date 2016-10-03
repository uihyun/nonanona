package com.yongtrim.lib.model.user;

import android.content.Context;


import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewData;
import com.nuums.nuums.model.user.NsUser;
import com.nuums.nuums.model.user.NsUserListData;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.ModelManager;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.util.BasePreferenceUtil;
import com.yongtrim.lib.util.MiscUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * hair / com.yongtrim.lib.model.user
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class UserManager extends ModelManager {

    private final String TAG = getClass().getSimpleName();

    private static UserManager instance;

    private ContextHelper contextHelper;
    private UserPreference preference;


    private NsUser me;


    public static UserManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new UserManager();
        }

        if(instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void setPreference(ContextHelper contextHelper) {
        preference = new UserPreference(contextHelper.getContext());
    }


    public void create(final String loginType,
                       final String role,
                       final String username,
                       final String password,
                       final String email,
                       final String nickname,
                       final Response.Listener<UserData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user");
        url.append("?login_type=").append(loginType);
        if(role != null)
            url.append("&role=").append(role);
        if(username != null)
            url.append("&username=").append(username);
        if(password != null)
            url.append("&password=").append(password);
        if(email != null)
            url.append("&email=").append(email);
        if(nickname != null)
            url.append("&nickname=").append(nickname);

        url.append("&role=").append("MEMBER");

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                UserData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void read(final String id,
                     final Response.Listener<UserData> listener,
                     final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/");

        if(id != null)
            url.append(id);

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                UserData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void update(final NsUser user, final Response.Listener<UserData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/");
        url.append(user.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("user", user.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                UserData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

    public void find(
            int page,
            JSONObject option,
            final Response.Listener<NsUserListData> listener,
            final Response.ErrorListener errorListener
    ){
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/userfind?");
        url.append("&page=").append(page);

        JSONObject body = new JSONObject();

        try {
            body.put("option", option);

        } catch(JSONException e) {
        }

        GsonBodyRequest<NsUserListData> request = new GsonBodyRequest<NsUserListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                NsUserListData.class,
                body,
                listener,
                errorListener);

        request.setTag(this);

        RequestManager.getRequestQueue().add(request);
    }

    public void certifyEmail(final int type,
                             final String email,
                             final String realname,
                             final Response.Listener<UserData> listener,
                             final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/certifyemail");

        JSONObject body = new JSONObject();

        try {
            body.put("type", type);
            body.put("email", email);
            if(realname != null)
                body.put("realname", realname);
        } catch(JSONException e) {
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


    public void checkValidCertifyPhonenumber(final String phonenumber,
                                             final Response.Listener<ACommonData> listener,
                                             final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/checkphonenumber/");
        url.append(phonenumber);

        GsonBodyRequest<ACommonData> request = new GsonBodyRequest<ACommonData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ACommonData.class,
                null,
                listener,
                errorListener);


        RequestManager.getRequestQueue().add(request);
    }


    public void checkValidCertifyFacebook(final String facebookId,
                                             final Response.Listener<ACommonData> listener,
                                             final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/checkfacebook/");
        url.append(facebookId);

        GsonBodyRequest<ACommonData> request = new GsonBodyRequest<ACommonData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ACommonData.class,
                null,
                listener,
                errorListener);


        RequestManager.getRequestQueue().add(request);
    }

    public void checkEmail(final String email,
                           final Response.Listener<ACommonData> listener,
                           final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/checkemail/");

        url.append(email);

        GsonBodyRequest<ACommonData> request = new GsonBodyRequest<ACommonData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ACommonData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void checkNickname(final String nickname,
                              final Response.Listener<ACommonData> listener,
                              final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/checknickname/");
        url.append(MiscUtil.encode(nickname));

        GsonBodyRequest<ACommonData> request = new GsonBodyRequest<ACommonData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ACommonData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void checkPassword(final String password,
                              final Response.Listener<ACommonData> listener,
                              final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/checkpassword");


        JSONObject body = new JSONObject();

        try {
            body.put("password", password.toString());

        } catch(JSONException e) {
        }
        GsonBodyRequest<ACommonData> request = new GsonBodyRequest<ACommonData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ACommonData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

    public NsUser getMe() {
        if(me == null) {
            me = NsUser.getUser(preference.getMe());
        }
        return me;
    }



    public void readTutorial() {
        preference.readTutorial();
    }

    public boolean isReadTutorial() {
        return preference.isReadTutorial();
    }

    public void changePassword(final String username,
                               final String password,
                               final Response.Listener<UserData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/changepassword/");
        url.append(username);

        JSONObject body = new JSONObject();

        try {
            body.put("password", password);

        } catch(JSONException e) {
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


    public void block(final NsUser user,
                         final boolean isBlock,
                         final Response.Listener<UserData> listener,
                         final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/");
        url.append(user.getId());
        url.append("/block/");
        url.append(isBlock ? "enable" : "disable");

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                UserData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void delete(final NsUser user,
                       final Response.Listener<UserData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/user/");

        url.append(user.getId());

        GsonBodyRequest<UserData> request = new GsonBodyRequest<UserData>(contextHelper,
                Request.Method.DELETE,
                url.toString(),
                UserData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void setMe(NsUser me) {
        me.patch(contextHelper);
        this.me = me;
        preference.putMe(me.toString());
    }

    public boolean isMe(NsUser user) {
        return getMe().isSame(user);
    }

    private class UserPreference extends BasePreferenceUtil {
        private static final String PROPERTY_ME = "user_me";
        private static final String PROPERTY_TUTORIAL = "user_readtutorial";

        public UserPreference(Context context) {
            super(context);
        }

        public void putMe(String jsonMe) {
            put(PROPERTY_ME, jsonMe);
        }

        public String getMe() {
            return get(PROPERTY_ME);
        }


        public void readTutorial() {
            put(PROPERTY_TUTORIAL, true);
        }

        public boolean isReadTutorial() {
            return get(PROPERTY_TUTORIAL, false);
        }

    }
}
