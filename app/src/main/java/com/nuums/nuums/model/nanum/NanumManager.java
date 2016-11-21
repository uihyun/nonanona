package com.nuums.nuums.model.nanum;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.misc.Comment;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.nanum
 * <p/>
 * Created by Uihyun on 15. 12. 21..
 */
public class NanumManager {
    private static NanumManager instance;
    private final String TAG = getClass().getSimpleName();
    boolean isBookmarking;
    private ContextHelper contextHelper;

    public static NanumManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new NanumManager();
        }
        if (instance.contextHelper == null || contextHelper == null || instance.contextHelper.getActivity() != contextHelper.getActivity()) {
            instance.isBookmarking = false;
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void create(final Response.Listener<NanumData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum");

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                NanumData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void delete(final Nanum nanum,
                       final Response.Listener<NanumData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");

        url.append(nanum.getId());

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.DELETE,
                url.toString(),
                NanumData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void read(final String id,
                     final Response.Listener<NanumData> listener,
                     final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");

        if (id != null)
            url.append(id);

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                NanumData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void update(final Nanum nanum,
                       final Response.Listener<NanumData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("nanum", nanum.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                NanumData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

    public boolean isBookmarking() {
        return isBookmarking;
    }

    public void setIsBookmarking(boolean isBookmarking) {
        this.isBookmarking = isBookmarking;
    }

    public void bookmark(final Nanum nanum,
                         final boolean isBookmark,
                         final Response.Listener<NanumData> listener,
                         final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());
        url.append("/bookmark/");
        url.append(isBookmark ? "enable" : "disable");

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                NanumData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void find(
            String userid,
            String type,
            int page,
            int limit,
            JSONObject option,
            final Response.Listener<NanumListData> listener,
            final Response.ErrorListener errorListener
    ) {
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/nanumfind?");
        url.append("&userid=").append(userid);
        url.append("&type=").append(type);
        url.append("&page=").append(page);
        url.append("&limit=").append(limit);

        JSONObject body = new JSONObject();

        try {
            body.put("option", option);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonBodyRequest<NanumListData> request = new GsonBodyRequest<NanumListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                NanumListData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

    public void won(final Nanum nanum,
                    final Response.Listener<NanumData> listener,
                    final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());
        url.append("/won/");

        JSONObject body = new JSONObject();

        try {
            JSONArray select = new JSONArray();
            for (Comment comment : nanum.getComments()) {
                if (comment.isSelect) {
                    select.put(comment.getOwner().getId());
                }
            }
            body.put("select", select);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                NanumData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }
}
