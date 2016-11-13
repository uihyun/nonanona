package com.yongtrim.lib.model.user;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.yongtrim.lib.util.MiscUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * hair / com.yongtrim.lib.model.user
 * <p/>
 * Created by Uihyun on 15. 9. 23..
 */
public class LocationTask extends AsyncTask<Void, Void, Void> {
    AServiceCallback callback;
    String oldAddress, newAddress;
    boolean isError = false;
    Map<String, Object> map = new HashMap<String, Object>();

    public LocationTask(String oldAddress, String newAddress, AServiceCallback callback) {
        this.callback = callback;
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
    }

    //LatLng resultLocation;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            newAddress = newAddress.replaceAll(" ", "%20");
            newAddress = MiscUtil.encodeIfNeed(newAddress);

            String strUrl = "http://maps.google.com/maps/api/geocode/json?address=" + newAddress + "&sensor=false&language=ko";

            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray results = jsonObject.getJSONArray("results");

            if (results.length() == 0) {
                stringBuilder = new StringBuilder();
                try {
                    oldAddress = newAddress.replaceAll(" ", "%20");
                    oldAddress = MiscUtil.encodeIfNeed(oldAddress);

                    String strUrl = "http://maps.google.com/maps/api/geocode/json?address=" + oldAddress + "&sensor=false&language=ko";

                    URL url = new URL(strUrl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ((line = br.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                    }
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                jsonObject = new JSONObject(stringBuilder.toString());

                results = jsonObject.getJSONArray("results");
            }

            if (results.length() == 0) {
                isError = true;
            } else {
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");

                double longitude = geometry.getJSONObject("location").getDouble("lng");
                double latitude = geometry.getJSONObject("location").getDouble("lat");
                LatLng location = new LatLng(latitude, longitude);

                longitude = geometry.getJSONObject("viewport").getJSONObject("northeast").getDouble("lng");
                latitude = geometry.getJSONObject("viewport").getJSONObject("northeast").getDouble("lat");
                LatLng northeast = new LatLng(latitude, longitude);

                longitude = geometry.getJSONObject("viewport").getJSONObject("southwest").getDouble("lng");
                latitude = geometry.getJSONObject("viewport").getJSONObject("southwest").getDouble("lat");
                LatLng southwest = new LatLng(latitude, longitude);

                JSONArray address = results.getJSONObject(0).getJSONArray("address_components");
                String subLocalAddress = "";
                for (int i = 0; i < address.length(); i++) {
                    if (address.getJSONObject(i).getString("types").contains("sublocality")) {
                        subLocalAddress = address.getJSONObject(i).getString("short_name");
                        break;
                    }
                }

                map = new HashMap<String, Object>();
                map.put("location", location);
                map.put("northeast", northeast);
                map.put("southwest", southwest);
                map.put("address", subLocalAddress);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (isError) {
            callback.failure(-1, "위치 검색을 할 수 없는 주소입니다.");
        } else {
            callback.success(map);
        }
    }

    public interface AServiceCallback {
        void success(Object object);

        void failure(int errorCode, String message);
    }
}

