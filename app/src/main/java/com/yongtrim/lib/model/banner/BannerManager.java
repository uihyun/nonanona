package com.yongtrim.lib.model.banner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.Configuration;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

/**
 * Created by YongTrim on 16. 6. 17. for nuums_ad
 */
public class BannerManager {
    private static BannerManager instance;
    private final String TAG = getClass().getSimpleName();
    private ContextHelper contextHelper;

    public static BannerManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new BannerManager();
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void count(final String id,
                      final Response.Listener<BannerData> listener,
                      final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/bannercount/");
        url.append(id);

        GsonBodyRequest<BannerData> request = new GsonBodyRequest<BannerData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                BannerData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void find(
            final Response.Listener<BannerData> listener,
            final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/bannernow/");

        GsonBodyRequest<BannerData> request = new GsonBodyRequest<BannerData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                BannerData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

}

