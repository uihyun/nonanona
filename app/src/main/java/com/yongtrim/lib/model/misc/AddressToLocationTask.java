package com.yongtrim.lib.model.misc;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.yongtrim.lib.util.MiscUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * nuums / com.yongtrim.lib.model.misc
 * <p/>
 * Created by yongtrim.com on 16. 1. 22..
 */
public class AddressToLocationTask extends AsyncTask<Void, Void, Void> {

    public interface AServiceCallback {
        void success(Object object);
        void failure(int errorCode, String message);
    }

    AServiceCallback callback;
    String address;
    boolean isError = false;
    Map<String,Object> map =  new HashMap<String,Object>();

    public AddressToLocationTask(String address, AServiceCallback callback) {
        this.callback = callback;
        this.address = address;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ", "%20");
            address = address.replaceAll("\n", "");

            address = MiscUtil.encodeIfNeed(address);

            String strUrl = "http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=true&language=ko";

            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

            conn.disconnect();

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
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if(isError) {
            callback.failure(-1, "위치 검색을 할 수 없는 주소입니다.");
        } else {
            callback.success(map);
        }
    }
}

