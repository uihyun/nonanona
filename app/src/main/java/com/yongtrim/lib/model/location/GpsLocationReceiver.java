package com.yongtrim.lib.model.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yongtrim.lib.message.PushMessage;

import de.greenrobot.event.EventBus;

/**
 * hair / com.yongtrim.lib.model.location
 * <p/>
 * Created by yongtrim.com on 15. 12. 4..
 */
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

//            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
//                    Toast.LENGTH_SHORT).show();
            //Intent pushIntent = new Intent(context, LocalService.class);
            //context.startService(pushIntent);
            EventBus.getDefault().post(new PushMessage().setActionCode(PushMessage.ACTIONCODE_CHANGEGPS));

        }
    }
}
