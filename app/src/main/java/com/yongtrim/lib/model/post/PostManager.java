package com.yongtrim.lib.model.post;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.Configuration;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.RequestManager;

/**
 * hair / com.yongtrim.lib.model.post
 * <p/>
 * Created by Uihyun on 15. 9. 28..
 */
public class PostManager {
    private static PostManager instance;
    private final String TAG = getClass().getSimpleName();
    private ContextHelper contextHelper;
    private PostManager preference;


    public static PostManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new PostManager();
        }
//        if(instance.contextHelper != contextHelper) {
//            instance.setPreference(contextHelper);
//        }
        instance.contextHelper = contextHelper;
        return instance;
    }

//    public void setPreference(ContextHelper contextHelper) {
//        preference = new HousePreference(contextHelper.getContext());
//    }

    public void read(final String type,
                     final Response.Listener<PostData> listener,
                     final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/post/");

        url.append(type);

        GsonBodyRequest<PostData> request = new GsonBodyRequest<PostData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                PostData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void count(final String id,
                      final Response.Listener<PostData> listener,
                      final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/postcount/");
        url.append(id);

        GsonBodyRequest<PostData> request = new GsonBodyRequest<PostData>(contextHelper,
                Request.Method.GET,
                url.toString(),
                PostData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }


    public void find(
            String type,
            int page,
            final Response.Listener<PostListData> listener,
            final Response.ErrorListener errorListener) {

        StringBuffer url = new StringBuffer();
        url.append(Configuration.url);
        url.append("/postfind?");
        url.append("&type=").append(type);
        url.append("&page=").append(page);

        GsonBodyRequest<PostListData> request = new GsonBodyRequest<PostListData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                PostListData.class,
                null,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }

}
