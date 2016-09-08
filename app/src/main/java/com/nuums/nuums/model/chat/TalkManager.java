package com.nuums.nuums.model.chat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by yongtrim.com on 16. 1. 6..
 */
public class TalkManager {
    private final String TAG = getClass().getSimpleName();
    private static TalkManager instance;

    private ContextHelper contextHelper;

    public static TalkManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new TalkManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }



    public void find(
            int page,
            final Response.Listener<TalkListData> listener,
            final Response.ErrorListener errorListener){


        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/talkfind?");
        url.append("&page=").append(page);

        GsonBodyRequest<TalkListData> request = new GsonBodyRequest<TalkListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                TalkListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);

    }


    public void out(String talk_id,
                    final Response.Listener<TalkData> listener,
                    final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/talkout");
        url.append("?talk_id=").append(talk_id);

        GsonBodyRequest<TalkData> request = new GsonBodyRequest<TalkData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                TalkData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


}

