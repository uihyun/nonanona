package com.nuums.nuums.model.report;

import com.android.volley.Request;
import com.android.volley.Response;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.GsonBodyRequest;
import com.yongtrim.lib.model.ModelManager;
import com.yongtrim.lib.model.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * nuums / com.nuums.nuums.model.report
 * <p/>
 * Created by Uihyun on 16. 1. 27..
 */
public class ReportManager extends ModelManager {
    private final String TAG = getClass().getSimpleName();
    private static ReportManager instance;

    private ContextHelper contextHelper;

    public static ReportManager getInstance(ContextHelper contextHelper) {
        if(instance == null) {
            instance = new ReportManager();
        }

        instance.contextHelper = contextHelper;
        return instance;
    }


    public void create(final Report report,
                       final Response.Listener<ReportData> listener,
                       final Response.ErrorListener errorListener) {
        StringBuffer url = new StringBuffer();
        url.append(Config.url);
        url.append("/report");

        JSONObject body = new JSONObject();

        try {
            body.put("report", report.toString());

        } catch(JSONException e) {
        }

        GsonBodyRequest<ReportData> request = new GsonBodyRequest<ReportData>(contextHelper,
                Request.Method.POST,
                url.toString(),
                ReportData.class,
                body,
                listener,
                errorListener);

        RequestManager.getRequestQueue().add(request);
    }
}
