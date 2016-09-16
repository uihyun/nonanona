package com.nuums.nuums;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Uihyun on 2016. 9. 17..
 */
public class MarketVersionChecker {
    private static String mData = "", mVer = "";
    private static boolean isOver;

    public static String getMarketVersion(String packageName) {
        AsyncPostData asyncPostData = new AsyncPostData(packageName);
        asyncPostData.execute();
        while (!isOver) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mVer;
    }

    public static class AsyncPostData extends AsyncTask<Void, Void, Void> {
        String packageName;

        public AsyncPostData(String packageName) {
            this.packageName = packageName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL mUrl = new URL("https://play.google.com/store/apps/details?id="
                        + packageName);
                HttpURLConnection mConnection = (HttpURLConnection) mUrl
                        .openConnection();

                if (mConnection == null)
                    return null;

                mConnection.setConnectTimeout(5000);
                mConnection.setUseCaches(false);
                mConnection.setDoOutput(true);

                if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader mReader = new BufferedReader(
                            new InputStreamReader(mConnection.getInputStream()));

                    while (true) {
                        String line = mReader.readLine();
                        if (line == null)
                            break;
                        mData += line;
                    }

                    mReader.close();
                }

                mConnection.disconnect();

                String startToken = "softwareVersion\">";
                String endToken = "<";
                int index = mData.indexOf(startToken);

                if (index == -1) {
                    mVer = null;

                } else {
                    mVer = mData.substring(index + startToken.length(), index
                            + startToken.length() + 100);
                    mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isOver = true;
            }
            return null;
        }
    }
}
