package com.yongtrim.lib.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;

import com.nuums.nuums.AppController;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.log.Logger;
import com.yongtrim.lib.model.user.LoginManager;
import com.yongtrim.lib.ui.sweetalert.SweetAlertDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by yongtrim.com on 15. 9. 1..
 */
public class GsonBodyRequest<T> extends JsonRequest<T> {
    final String TAG = "GsonBodyRequest";

    private final Gson mGson;
    private final Class<T> mClassType;
    private final Response.Listener<T> mListener;
    private ContextHelper mContextHelper;
    private LoginManager mLoginManager;


    private String url;

    public GsonBodyRequest(ContextHelper contextHelper,
                           int method,
                           String url,
                           Class<T> classType,
                           JSONObject jsonRequest,
                           Response.Listener<T> listener,
                           Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);

        mGson = AppController.getInstance().getGson();
        mClassType = classType;
        mListener = listener;
        mContextHelper = contextHelper;
        mLoginManager = LoginManager.getInstance(contextHelper);

        this.url = url;

        setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map params = new HashMap();

        params.put("yt_platform", "android");
        params.put("yt_accesstoken", mLoginManager.getAccessToken());

        return params;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {

        String json = "";
        try {
            json = new String(networkResponse.data, HttpHeaderParser.parseCharset
                    (networkResponse.headers));

            //Logger.debug(TAG, "json = " + json);

            return Response.success(mGson.fromJson(json, mClassType),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Logger.debug(TAG, e.toString());
            Logger.debug(TAG, "url = " + url);
            Logger.debug(TAG, "json = " + json);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mContextHelper.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mContextHelper.hideProgress();
                } catch (Exception e) {

                }
            }
        });

        if(error instanceof AuthFailureError || (error.getMessage() != null && error.getMessage().equals("java.io.IOException: No authentication challenges found"))) {
            mContextHelper.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LoginManager.getInstance(mContextHelper).setInavalidCurrentUser();
                        SweetAlertDialog dialog = new SweetAlertDialog(mContextHelper.getContext())
                                .setContentText("권한이 없거나, 다른 디바이스에서 로그인 하였습니다. 다시 앱을 시작합니다.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();
                                        mContextHelper.restart();
                                    }
                                });

                        dialog.setCancelable(false);
                        dialog.show();
                    } catch (Exception e) {

                    }
                }
            });

            return;
        } else {
            mContextHelper.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PackageInfo pinfo = mContextHelper.getContext().getPackageManager().getPackageInfo(mContextHelper.getContext().getPackageName(), 0);

                        SweetAlertDialog dialog = new SweetAlertDialog(mContextHelper.getContext())
                                .setContentText("네트웍 장애입니다. 네트워크 확인후 다시 실행해주세요~" + pinfo.versionName)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        mContextHelper.getActivity().finish();
                                    }
                                });

                        dialog.setCancelable(false);
                        dialog.show();
                    } catch (Exception e) {

                    }
                }
            });
        }
        //--super.deliverError(error);
    }

//
//    public static JSONObject getRequestBody() {
//        JSONObject jsonBody = new JSONObject();
//        User user = Application.getInstance().getMe();
//        try {
//            if(user != null) {
//                jsonBody.put("accessToken", user.getAccessToken());
//            }
//        } catch(Exception e) {
//        }
//        return jsonBody;
//    }
}
