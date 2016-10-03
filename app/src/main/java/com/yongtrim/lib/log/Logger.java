package com.yongtrim.lib.log;

import android.app.Application;

import org.acra.ACRA;

/**
 * hair / com.yongtrim.lib.log
 * <p/>
 * Created by Uihyun on 15. 9. 2..
 */
public class Logger {

    static boolean isDebug;
    static String packageName;
    static boolean isDebugReport;

    public static void initialize(Application application, boolean isDebug, boolean isDebugReport) {

        Logger.isDebug = isDebug;

        Logger.isDebugReport = isDebugReport;

        if(isDebugReport) {
            ACRA.init(application);
            ACRA.getErrorReporter().setEnabled(true);
        }

        //Log.i("rr_", "Reboot is good");
        //
        packageName = application.getPackageName();
    }

    public static void info(String className, String string) {
        if(isDebug) {
            android.util.Log.i(packageName, "YTLOGGER | " + className + " | " + string);
        }
    }

    public static void debug(String className, String string) {
        if(isDebug) {
            android.util.Log.i(packageName, "YTLOGGER | " + className + " | " + string);
        }
    }

    public static void trace(String className, String string) {
        if(isDebugReport) {
            ACRA.getErrorReporter().handleSilentException(new Throwable(className + " | " + string));
        }
    }
}
