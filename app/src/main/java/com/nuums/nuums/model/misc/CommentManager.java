package com.nuums.nuums.model.misc;

import com.android.volley.Request;
import com.android.volley.Response;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.nanum.NanumData;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.review.ReviewData;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.misc
 * <p/>
 * Created by Uihyun on 15. 12. 28..
 */
public class CommentManager {
    private static CommentManager instance;
    private final String TAG = getClass().getSimpleName();
    private ContextHelper contextHelper;

    public static CommentManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new CommentManager();
        }
        instance.contextHelper = contextHelper;
        return instance;
    }


    public void createInNanum(Nanum nanum,
                              String message,
                              boolean applymode,
                              final Response.Listener<NanumData> listener,
                              final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());
        url.append("/comment/");

        JSONObject body = new JSONObject();

        try {
            body.put("message", message);
            body.put("applymode", applymode);
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


    public void updateInNanum(Nanum nanum,
                              int index,
                              String message,
                              final Response.Listener<NanumData> listener,
                              final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());
        url.append("/comment/");

        JSONObject body = new JSONObject();

        try {
            body.put("message", message);
            body.put("index", index);
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


    public void deleteInNanum(Nanum nanum,
                              int index,
                              final Response.Listener<NanumData> listener,
                              final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/nanum/");
        url.append(nanum.getId());
        url.append("/comment");
        url.append("?index=" + index);


        GsonBodyRequest<NanumData> request = new GsonBodyRequest<NanumData>(contextHelper,
                Request.Method.DELETE,
                url.toString(),
                NanumData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void createInReview(Review review,
                               String message,
                               final Response.Listener<ReviewData> listener,
                               final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");
        url.append(review.getId());
        url.append("/comment/");

        JSONObject body = new JSONObject();

        try {
            body.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ReviewData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void updateInReview(Review review,
                               int index,
                               String message,
                               final Response.Listener<ReviewData> listener,
                               final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");
        url.append(review.getId());
        url.append("/comment/");

        JSONObject body = new JSONObject();

        try {
            body.put("message", message);
            body.put("index", index);
        } catch (JSONException e) {
            e.printStackTrace();
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


    public void deleteInReview(Review review,
                               int index,
                               final Response.Listener<ReviewData> listener,
                               final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/review/");
        url.append(review.getId());
        url.append("/comment");
        url.append("?index=" + index);


        GsonBodyRequest<ReviewData> request = new GsonBodyRequest<ReviewData>(contextHelper,
                Request.Method.DELETE,
                url.toString(),
                ReviewData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }
}

