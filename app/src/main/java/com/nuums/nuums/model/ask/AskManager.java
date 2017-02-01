package com.nuums.nuums.model.ask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.Configuration;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.ask
 * <p/>
 * Created by Uihyun on 16. 1. 22..
 */
public class AskManager {
    private final String TAG = getClass().getSimpleName();
    private static AskManager instance;

    private ContextHelper contextHelper;

    public static AskManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new AskManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }


    public void create(final Response.Listener<AskData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/ask");

        GsonBodyRequest<AskData> request = new GsonBodyRequest<AskData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                AskData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void update(final Ask ask,
                       final Response.Listener<AskData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/ask/");
        url.append(ask.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("ask", ask.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<AskData> request = new GsonBodyRequest<AskData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                AskData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void find(
            int page,
            final Response.Listener<AskListData> listener,
            final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/askfind?");

        GsonBodyRequest<AskListData> request = new GsonBodyRequest<AskListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                AskListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }



}

