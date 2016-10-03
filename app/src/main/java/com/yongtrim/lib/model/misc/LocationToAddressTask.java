package com.yongtrim.lib.model.misc;

import android.os.AsyncTask;

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
 * Created by Uihyun on 16. 1. 22..
 */
public class LocationToAddressTask extends AsyncTask<Void, Void, Void> {

    public interface AServiceCallback {
        void success(Object object);
        void failure(int errorCode, String message);
    }

    AServiceCallback callback;
    double latitude;
    double longitude;

    boolean isError = false;

    Map<String,Object> map =  new HashMap<String,Object>();


    public LocationToAddressTask(double latitude, double longitude, AServiceCallback callback) {
        this.callback = callback;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            String strUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&language=ko";


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

            JSONObject json = new JSONObject(stringBuilder.toString());

            JSONArray arrResults = json.getJSONArray( "results" );

            JSONObject object = (JSONObject)arrResults.get(0);
            String address = (String)object.get("formatted_address");

            address = address.replace("대한민국 ", "");

            map.put("address", address);

            if(arrResults.length() > 3) {
                JSONObject objectShort = (JSONObject) arrResults.get(arrResults.length() - 3);

                String addressShort = (String) objectShort.get("formatted_address");

                addressShort = addressShort.replace("대한민국 ", "");

                map.put("addressShort", addressShort);
            } else {
                map.put("addressShort", address);
            }
        } catch (JSONException e) {
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if(isError) {
            callback.failure(-1, "주소를 추출할 수 없는 위치입니다.");
        } else {
            callback.success(map);
        }
    }
}

