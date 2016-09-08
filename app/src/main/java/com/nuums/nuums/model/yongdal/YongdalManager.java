package com.nuums.nuums.model.yongdal;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.apply.ApplyData;
import com.nuums.nuums.model.apply.ApplyListData;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.yongdal
 * <p/>
 * Created by yongtrim.com on 16. 1. 31..
 */
public class YongdalManager {
    private final String TAG = getClass().getSimpleName();
    private static YongdalManager instance;

    private ContextHelper contextHelper;

    public static YongdalManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new YongdalManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }


    public void create(final Response.Listener<YongdalData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/yongdal");

        GsonBodyRequest<YongdalData> request = new GsonBodyRequest<YongdalData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                YongdalData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void update(final Yongdal yongdal,
                       final Response.Listener<YongdalData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/yongdal/");
        url.append(yongdal.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("yongdal", yongdal.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<YongdalData> request = new GsonBodyRequest<YongdalData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                YongdalData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void calPrice(final Yongdal yongdal,
                         final Response.Listener<YongdalData> listener,
                         final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/yongdalprice/");

        JSONObject body = new JSONObject();

        try {
            body.put("yongdal", yongdal.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<YongdalData> request = new GsonBodyRequest<YongdalData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                YongdalData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


}
