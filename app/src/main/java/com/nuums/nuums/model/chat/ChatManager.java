package com.nuums.nuums.model.chat;

import android.text.TextUtils;

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
 * Created by Uihyun on 16. 1. 6..
 */
public class ChatManager {
    private final String TAG = getClass().getSimpleName();
    private static ChatManager instance;

    private ContextHelper contextHelper;

    public static ChatManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new ChatManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }


    public void find(
            String from_id,
            String to_id,
            int page,
            final Response.Listener<ChatListData> listener,
            final Response.ErrorListener errorListener){


        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/chatfind");
        url.append("?page=").append(page);

        url.append("&from_id=").append(from_id);
        url.append("&to_id=").append(to_id);

        GsonBodyRequest<ChatListData> request = new GsonBodyRequest<ChatListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ChatListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }




}


