package com.yongtrim.lib.model.user;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.yongtrim.lib.util.MiscUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * hair / com.yongtrim.lib.model.user
 * <p/>
 * Created by yongtrim.com on 15. 9. 23..
 */
public class LocationTask extends AsyncTask<Void, Void, Void>
{
    public interface AServiceCallback {
        void success(Object object);
        void failure(int errorCode, String message);
    }


    AServiceCallback callback;
    String address;
    boolean isError = false;
    Map<String,Object> map =  new HashMap<String,Object>();

    //LatLng resultLocation;

    public LocationTask(String address, AServiceCallback callback) {
        this.callback = callback;
        this.address = address;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ", "%20");

            address = MiscUtil.encodeIfNeed(address);

            String url = "http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false&language=ko";

            HttpPost httppost = new HttpPost(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray results = jsonObject.getJSONArray("results");


            if(results.length() == 0) {
                isError = true;
            } else {
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");

                double longitude = geometry.getJSONObject("location").getDouble("lng");
                double latitude = geometry.getJSONObject("location").getDouble("lat");
                LatLng location = new LatLng(latitude, longitude);

                longitude = geometry.getJSONObject("viewport").getJSONObject("northeast").getDouble("lng");
                latitude =geometry.getJSONObject("viewport").getJSONObject("northeast").getDouble("lat");
                LatLng northeast = new LatLng(latitude, longitude);

                longitude = geometry.getJSONObject("viewport").getJSONObject("southwest").getDouble("lng");
                latitude =geometry.getJSONObject("viewport").getJSONObject("southwest").getDouble("lat");
                LatLng southwest = new LatLng(latitude, longitude);


                map =  new HashMap<String,Object>();
                map.put("location", location);
                map.put("northeast", northeast);
                map.put("southwest", southwest);
            }


        } catch (JSONException e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);

        if(isError) {
            callback.failure(-1, "위치 검색을 할 수 없는 주소입니다.");
        } else {
            callback.success(map);
        }
    }
}

