package com.nuums.nuums.model.review;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.nanum.NanumListData;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewManager {
    private final String TAG = getClass().getSimpleName();
    private static ReviewManager instance;

    private ContextHelper contextHelper;
    boolean isStaring;

    public static ReviewManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new ReviewManager();
        }

        if(instance.contextHelper == null || contextHelper == null || instance.contextHelper.getActivity() != contextHelper.getActivity()) {
            instance.isStaring = false;
        }
        instance.contextHelper = contextHelper;
        return instance;
    }

    public void create(final Response.Listener<ReviewData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review");

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ReviewData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void delete(final Review review,
                       final Response.Listener<ReviewData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");

        url.append(review.getId());

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.DELETE,
                url.toString(),
                ReviewData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void read(final String id,
                     final Response.Listener<ReviewData> listener,
                     final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");

        if(id != null)
            url.append(id);

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                ReviewData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }



    public void update(final Review review,
                       final Response.Listener<ReviewData> listener,
                       final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");
        url.append(review.getId());

        JSONObject body = new JSONObject();

        try {
            body.put("review", review.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                ReviewData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

    public boolean isStaring() {
        return isStaring;
    }

    public void setIsStaring(boolean isStaring) {
        this.isStaring = isStaring;
    }

    public void star(final Review review,
                         final boolean isStar,
                         final Response.Listener<ReviewData> listener,
                         final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");
        url.append(review.getId());
        url.append("/star/");
        url.append(isStar ? "enable" : "disable");

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.PUT,
                url.toString(),
                ReviewData.class,
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
            final Response.Listener<ReviewListData> listener,
            final Response.ErrorListener errorListener
    ) {
        StringBuffer url = new StringBuffer();
        url.append(com.yongtrim.lib.Config.url);
        url.append("/reviewfind?");
        url.append("&userid=").append(userid);
        url.append("&type=").append(type);
        url.append("&page=").append(page);
        url.append("&limit=").append(limit);

        JSONObject body = new JSONObject();

        try {
            body.put("option", option);

        } catch(JSONException e) {
        }

        GsonBodyRequest<ReviewListData> request = new GsonBodyRequest<ReviewListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ReviewListData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


}

