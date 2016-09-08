package com.yongtrim.lib.message;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.yongtrim.lib.log.Logger;

import java.net.URLDecoder;

/**
 * Created by studioyongtrim on 15. 8. 18..
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private final String TAG = "GcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(TAG, "onReceive() |" + "=================" + "|");
        Bundle bundle = intent.getExtras();

        String message = "";

        if(bundle == null)
            return;

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);

            if( key.equals("message") ) {
                try {
                    message = URLDecoder.decode((String) value, "UTF-8");
                } catch (Exception e) {

                }
            }
            Logger.info(TAG, "onReceive() |" + String.format("%s : %s (%s)", key, value.toString(), value.getClass().getName()) + "|");
        }
        Logger.info(TAG, "onReceive() |" + "=================" + "|");

        intent.putExtra("message", message);

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}

