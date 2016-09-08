package com.nuums.nuums.model.alarm;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by yongtrim.com on 16. 1. 20..
 */
public class AlarmManager {

    private final String TAG = getClass().getSimpleName();

    private static AlarmManager instance;

    private ContextHelper contextHelper;
    private AlarmPreference preference;

    public static AlarmManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new AlarmManager();
        }

        if(instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void setPreference(ContextHelper contextHelper) {
        preference = new AlarmPreference(contextHelper.getContext());
    }

    public AlarmPreference getPreference() {
        return preference;
    }

    public void find(
            String type,
            int page,
            final Response.Listener<AlarmListData> listener,
            final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/alarmfind?");
        url.append("&type=").append(type);
        url.append("&page=").append(page);

        GsonBodyRequest<AlarmListData> request = new GsonBodyRequest<AlarmListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                AlarmListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public class AlarmPreference extends BasePreferenceUtil {
        private static final String KEY_PUSH_FOLLOWME = "push_followme";
        private static final String KEY_PUSH_LIKEMINE = "push_likemine";
        private static final String KEY_PUSH_SCRAPMINE = "push_scarpmine";
        private static final String KEY_PUSH_COMMENTONMINE = "push_commenttomine";
        private static final String KEY_PUSH_MENTIONME = "push_mentionme";
        private static final String KEY_PUSH_SHOPNOTICE = "push_shopnotice";
        private static final String KEY_PUSH_FACEBOOK = "push_facebook";
        private static final String KEY_PUSH_TWITTER = "push_twitter";
        private static final String KEY_PUSH_GOOGLE = "push_google";
        private static final String KEY_PUSH_LIKEMYSHOP = "push_likemyshop";
        private static final String KEY_PUSH_REVIEWMYSHOP = "push_reviewmyshop";
        private static final String KEY_PUSH_ASKMYSHOP = "push_askmyshop";

        public AlarmPreference(Context context) {
            super(context);
        }

        public void putFollowMe(boolean value) {
            put(KEY_PUSH_FOLLOWME, value);
        }
        public boolean getFollowMe() {
            return get(KEY_PUSH_FOLLOWME, true);
        }

        public void putLikeMine(boolean value) {
            put(KEY_PUSH_LIKEMINE, value);
        }
        public boolean getLikeMine() {
            return get(KEY_PUSH_LIKEMINE, true);
        }

        public void putScrapMine(boolean value) {
            put(KEY_PUSH_SCRAPMINE, value);
        }
        public boolean getScrapMine() {
            return get(KEY_PUSH_SCRAPMINE, true);
        }

        public void putCommentToMine(boolean value) {
            put(KEY_PUSH_COMMENTONMINE, value);
        }
        public boolean getCommentToMine() {
            return get(KEY_PUSH_COMMENTONMINE, true);
        }

        public void putMentionMe(boolean value) {
            put(KEY_PUSH_MENTIONME, value);
        }
        public boolean getMentionMe() {
            return get(KEY_PUSH_MENTIONME, true);
        }

        public void putShopNotice(boolean value) {
            put(KEY_PUSH_SHOPNOTICE, value);
        }
        public boolean getShopNotice() {
            return get(KEY_PUSH_SHOPNOTICE, true);
        }

        public void putFacebook(boolean value) {
            put(KEY_PUSH_FACEBOOK, value);
        }
        public boolean getFacebook() {
            return get(KEY_PUSH_FACEBOOK, true);
        }

        public void putTwitter(boolean value) {
            put(KEY_PUSH_TWITTER, value);
        }
        public boolean getTwitter() {
            return get(KEY_PUSH_TWITTER, true);
        }

        public void putGoogle(boolean value) {
            put(KEY_PUSH_GOOGLE, value);
        }
        public boolean getGoogle() {
            return get(KEY_PUSH_GOOGLE, true);
        }


        public void putLikeMyShop(boolean value) {
            put(KEY_PUSH_LIKEMYSHOP, value);
        }
        public boolean getLikeMyShop() {
            return get(KEY_PUSH_LIKEMYSHOP, true);
        }
        public void putReviewMyShop(boolean value) {
            put(KEY_PUSH_REVIEWMYSHOP, value);
        }
        public boolean getReviewMyShop() {
            return get(KEY_PUSH_REVIEWMYSHOP, true);
        }
        public void putAskMyShop(boolean value) {
            put(KEY_PUSH_ASKMYSHOP, value);
        }
        public boolean getAskMyShop() {
            return get(KEY_PUSH_ASKMYSHOP, true);
        }
    }
}

