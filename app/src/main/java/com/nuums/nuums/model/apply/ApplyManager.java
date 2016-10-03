package com.nuums.nuums.model.apply;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.alarm.AlarmListData;
import com.nuums.nuums.model.chat.ChatListData;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.apply
 * <p/>
 * Created by Uihyun on 16. 1. 10..
 */
public class ApplyManager {
    private final String TAG = getClass().getSimpleName();
    private static ApplyManager instance;

    private ContextHelper contextHelper;

    public static ApplyManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new ApplyManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }


    public void create(final Response.Listener<ApplyData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/apply");

        GsonBodyRequest<ApplyData> request = new GsonBodyRequest<ApplyData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ApplyData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void update(final Apply apply,
                       final Response.Listener<ApplyData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/apply/");
        url.append(apply.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("apply", apply.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<ApplyData> request = new GsonBodyRequest<ApplyData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                ApplyData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void find(
            String type,
            int page,
            final Response.Listener<ApplyListData> listener,
            final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/applyfind?");
        url.append("&type=").append(type);
        url.append("&page=").append(page);

        GsonBodyRequest<ApplyListData> request = new GsonBodyRequest<ApplyListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ApplyListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }



}


