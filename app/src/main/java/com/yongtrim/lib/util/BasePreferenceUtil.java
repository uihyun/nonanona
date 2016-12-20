package com.yongtrim.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * hair / com.yongtrim.lib.util
 * <p/>
 * Created by Uihyun on 15. 9. 2..
 */
public class BasePreferenceUtil {
    private SharedPreferences sharedPreferences;

    public BasePreferenceUtil(Context context) {
        super();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void put(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, null);
    }


    public String get(String key, String $default) {
        return sharedPreferences.getString(key, $default);
    }

    public void put(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean get(String key, boolean $default) {
        return sharedPreferences.getBoolean(key, $default);
    }

    public void put(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int get(String key, int $default) {
        return sharedPreferences.getInt(key, $default);
    }


    public void put(String key, double value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public double get(String key, double $default) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key, Double.doubleToLongBits($default)));
    }

    public void clear(String key) {
        sharedPreferences.edit().remove(key).commit();
    }
}
