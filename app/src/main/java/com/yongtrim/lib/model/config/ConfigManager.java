package com.yongtrim.lib.model.config;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.ModelManager;
import com.yongtrim.lib.model.RequestManager;
import com.yongtrim.lib.model.banner.Banner;
import com.yongtrim.lib.model.post.PostList;
import com.yongtrim.lib.util.BasePreferenceUtil;

/**
 * hair / com.yongtrim.lib.model.config
 * <p/>
 * Created by Uihyun on 15. 9. 2..
 */
public class ConfigManager extends ModelManager {
    private static ConfigManager instance;
    private final String TAG = getClass().getSimpleName();
    private ContextHelper contextHelper;
    private ConfigPreference preference;

    private Config configHello;
    private Banner banner;
    private PostList events;

    public static ConfigManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new ConfigManager();
        }

        if (instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }

    public ConfigPreference getPreference() {
        return preference;
    }

    public void setPreference(ContextHelper contextHelper) {
        preference = new ConfigPreference(contextHelper.getContext());
    }

    public void get(final Response.Listener<ConfigData> listener,
                    final Response.ErrorListener errorListener,
                    final String type) {


        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/config/");
        url.append(type);

        GsonBodyRequest<ConfigData> request = new GsonBodyRequest<ConfigData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ConfigData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public Config getConfigHello() {
        if (configHello == null) {
            configHello = Config.getConfig(preference.getHello());
        }
        return configHello;
    }


    public void setConfigHello(Config config) {
        configHello = config;
        preference.putHello(config.toString());
    }


    public Banner getBanner() {
        if (banner == null) {
            banner = preference.getBanner();
        }
        return banner;
    }


    public void setBanner(Banner banner) {
        this.banner = banner;
        preference.putBanner(banner);
    }

    public PostList getEvents() {
        if (events == null) {
            events = preference.getEvent();
        }
        return events;
    }


    public void setEvents(PostList events) {
        this.events = events;
        preference.putEvent(events);
    }

    public class ConfigPreference extends BasePreferenceUtil {
        private static final String PROPERTY_HELLO = "config_hello";
        private static final String PROPERTY_UNREADNOTICECNT = "config_unreadnoticecnt";

        private static final String PROPERTY_AUTOLOGIN = "config_autologin";
        private static final String PROPERTY_SIGNUPPARAM = "config_signupparam";

        private static final String PROPERTY_NANUMEDITPARAM = "config_nanumeditparam";

        private static final String PROPERTY_SORT = "config_sort";

        //private static final String PROPERTY_BANNER = "config_banner";
        private static final String PROPERTY_BANNER2 = "config_banner2";
        private static final String PROPERTY_EVENT = "config_event";

        private static final String PROPERTY_MYADDRESS = "config_myaddress";

        private static final String PROPERTY_SHAREINSTAGRAM = "config_shareinstagram";


        public ConfigPreference(Context context) {
            super(context);
        }

        public void putHello(String jsonHello) {
            put(PROPERTY_HELLO, jsonHello);
        }

        public String getHello() {
            return get(PROPERTY_HELLO);
        }

        public void putUnreadCnt(int count) {
            put(PROPERTY_UNREADNOTICECNT, count);
        }

        public int getUnreadCnt() {
            return get(PROPERTY_UNREADNOTICECNT, 0);
        }

        public void putAutoLogin(boolean autologin) {
            put(PROPERTY_AUTOLOGIN, autologin);
        }

        public boolean getAutoLogin() {
            return get(PROPERTY_AUTOLOGIN, true);
        }

        public void putSignupParam(NsUser user) {
            if (user == null)
                clear(PROPERTY_SIGNUPPARAM);
            else
                put(PROPERTY_SIGNUPPARAM, user.toString());
        }

        public NsUser getSignupParam() {
            if (!TextUtils.isEmpty(get(PROPERTY_SIGNUPPARAM))) {
                return NsUser.getUser(get(PROPERTY_SIGNUPPARAM));
            }
            return null;
        }

        public void putNanumParam(Nanum nanum) {
            if (nanum == null)
                clear(PROPERTY_NANUMEDITPARAM);
            else
                put(PROPERTY_NANUMEDITPARAM, nanum.toString());
        }

        public Nanum getNanumParam() {
            if (!TextUtils.isEmpty(get(PROPERTY_NANUMEDITPARAM))) {
                return Nanum.getNanum(get(PROPERTY_NANUMEDITPARAM));
            }
            return null;
        }


//        public void putReviewParam(Review review) {
//            if(review == null)
//                clear(PROPERTY_REVIEWEDITPARAM);
//            else
//                put(PROPERTY_REVIEWEDITPARAM, review.toString());
//        }
//
//        public Review getReviewParam() {
//            if(!TextUtils.isEmpty(get(PROPERTY_REVIEWEDITPARAM))) {
//                return Review.getReview(get(PROPERTY_REVIEWEDITPARAM));
//            }
//            return null;
//        }


//        public void putReportParam(String type, Report report) {
//
//            if(type.equals(Report.REPORTTYPE_REPORT)) {
//                if(report == null)
//                    clear(PROPERTY_REPORTREPORTPARAM);
//                else
//                    put(PROPERTY_REPORTREPORTPARAM, report.toString());
//            } else if(type.equals(Report.REPORTTYPE_AD)) {
//                if(report == null)
//                    clear(PROPERTY_REPORTADPARAM);
//                else
//                    put(PROPERTY_REPORTADPARAM, report.toString());
//
//            }else if(type.equals(Report.REPORTTYPE_INQUIRY)) {
//                if(report == null)
//                    clear(PROPERTY_REPORTINQUIRYPARAM);
//                else
//                    put(PROPERTY_REPORTINQUIRYPARAM, report.toString());
//            }
//        }
//
//
//        public Report getReportParam(String type) {
//            if(type.equals(Report.REPORTTYPE_REPORT)) {
//                if (!TextUtils.isEmpty(get(PROPERTY_REPORTREPORTPARAM))) {
//                    return Report.getReport(get(PROPERTY_REPORTREPORTPARAM));
//                }
//            } else if(type.equals(Report.REPORTTYPE_AD)) {
//                if (!TextUtils.isEmpty(get(PROPERTY_REPORTADPARAM))) {
//                    return Report.getReport(get(PROPERTY_REPORTADPARAM));
//                }
//            }else if(type.equals(Report.REPORTTYPE_INQUIRY)) {
//                if (!TextUtils.isEmpty(get(PROPERTY_REPORTINQUIRYPARAM))) {
//                    return Report.getReport(get(PROPERTY_REPORTINQUIRYPARAM));
//                }
//            }
//            return null;
//        }


        public void putSort(int sort) {
            put(PROPERTY_SORT, sort);
        }

        public int getSort() {
            return get(PROPERTY_SORT, 0);
        }


        public void putBanner(Banner banner) {
            if (banner != null)
                put(PROPERTY_BANNER2, banner.toString());
            else
                clear(PROPERTY_BANNER2);
        }

        public Banner getBanner() {
            return Banner.getBanner(get(PROPERTY_BANNER2));
        }


        public void putEvent(PostList postList) {
            put(PROPERTY_EVENT, postList.toString());
        }

        public PostList getEvent() {
            return PostList.getPostList(get(PROPERTY_EVENT));
        }


        public void putMyAddress(Address address) {
            if (address == null)
                clear(PROPERTY_MYADDRESS);
            else
                put(PROPERTY_MYADDRESS, address.toString());
        }

        public Address getMyAddress() {
            return Address.getAddress(get(PROPERTY_MYADDRESS));
        }

        public void putIsShareInstagram(boolean isShare) {
            put(PROPERTY_SHAREINSTAGRAM, isShare);
        }

        public boolean getIsShareInstagram() {
            return get(PROPERTY_SHAREINSTAGRAM, false);
        }
    }

}
