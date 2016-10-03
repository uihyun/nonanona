package com.nuums.nuums;

import android.app.Application;


/**
 * nuums / com.nuums.nuums
 * <p/>
 * Created by Uihyun on 15.12. 7..
 */
public class AppController extends com.yongtrim.lib.Application {

    private static AppController instance;

    @Override
    public void onCreate() {
        instance = this;

        super.onCreate();

    }

    public static synchronized AppController getInstance() {
        return instance;
    }



}
