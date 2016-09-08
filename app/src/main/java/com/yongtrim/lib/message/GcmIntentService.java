package com.yongtrim.lib.message;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nuums.nuums.R;
import com.nuums.nuums.activity.BaseActivity;
import com.nuums.nuums.activity.MainActivity;
import com.nuums.nuums.activity.SplashActivity;
import com.nuums.nuums.model.alarm.Alarm;
import com.nuums.nuums.model.chat.Chat;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.Application;
import com.yongtrim.lib.Config;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.config.ConfigManager;
import com.yongtrim.lib.model.user.UserManager;

import de.greenrobot.event.EventBus;

/**
 * Created by studioyongtrim on 15. 8. 18..
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    static String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        // has effect of unparcelling Bundle
        if (!extras.isEmpty()) {
            /*
            * Filter messages based on message type. Since it is likely that GCM will
            * be extended in the future with new message types, just ignore any
            * message types you're not interested in, or that you don't recognize.
            */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                String message = intent.getStringExtra("message");


                PushMessage pushMessage = PushMessage.getPushMessage(message);
                if (pushMessage == null)
                    return;

                sendEvent(pushMessage);
                sendNotification(pushMessage);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendEvent(PushMessage pushMessage) {
        ContextHelper contextHelper = new ContextHelper(getApplication().getApplicationContext());

        if (pushMessage.getActionCode() == PushMessage.ACTIONCODE_CHANGE_ME) {
            UserManager.getInstance(contextHelper).setMe((NsUser) pushMessage.getObject(contextHelper));
        }

        EventBus.getDefault().post(pushMessage);


    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(PushMessage pushMessage) {

        ContextHelper contextHelper = new ContextHelper(getApplication().getApplicationContext());
        final MessageManager.MessagePreference preference = MessageManager.getInstance(contextHelper).getPreference();

        if (pushMessage.getActionCode() == PushMessage.ACTIONCODE_CHANGETALK) {
            Talk talk = (Talk) pushMessage.getObject(contextHelper);
            NsUser user = talk.getMe(contextHelper);
            NsUser to = talk.getOther(contextHelper);

            Application.setBadge(getBaseContext(), user.getUnreadCnt());

            if(preference.getCurChatter() != null && preference.getCurChatter().isSame(talk.getOther(contextHelper)))
                return;

            if (!preference.getIsAlarmOn(user, to))
                return;

            if(!preference.getIsTalk()) {
                return;
            }
        }

        if (pushMessage.getActionCode() == PushMessage.ACTIONCODE_ALRAM) {

            Alarm alarm = (Alarm)pushMessage.getObject(contextHelper);

            if(alarm.getType().equals("APPLY") && !preference.getIsNanumComment()) {
                return;
            }

            if(alarm.getType().equals("WON") && !preference.getIsNanumWon()) {
                return;
            }

            if(alarm.getType().equals("WARNING") && !preference.getIsNanumWarn()) {
                return;
            }
            if(alarm.getType().equals("KEYWORD") && !preference.getIsNanumKeyword()) {
                return;
            }
            if(alarm.getType().equals("ZZIM") && !preference.getIsNanumZzim()) {
                return;
            }

            if(alarm.getType().equals("DELIVERY_DOING") && !preference.getIsDelivery()) {
                return;
            }

            if(alarm.getType().equals("REVIEW_COMMENT") && !preference.getIsReviewComment()) {
                return;
            }

            if(alarm.getType().equals("REVIEW_TAG") && !preference.getIsReviewTag()) {
                return;
            }

            if(alarm.getType().equals("EVENT_START") && !preference.getIsEventStart()) {
                return;
            }

            if(alarm.getType().equals("EVENT_END") && !preference.getIsEventFinish()) {
                return;
            }
        }

        if (!TextUtils.isEmpty(pushMessage.getMessage()) && preference.getPush() != 0) {
            makeNotification(pushMessage);
        }
    }


    void makeNotification(PushMessage pushMessage) {
        ContextHelper contextHelper = new ContextHelper(getApplication().getApplicationContext());
        String content = "";
        Intent i = new Intent(this, MainActivity.class);

        if (pushMessage.getActionCode() == PushMessage.ACTIONCODE_CHANGETALK) {
            Talk talk = (Talk)pushMessage.getObject(contextHelper);
            content = talk.getOther(contextHelper).getNicknameSafe() + ": " + pushMessage.getMessage();
            i.putExtra("to", talk.getOther(contextHelper).toString());
        } else if(pushMessage.getActionCode() == PushMessage.ACTIONCODE_ALRAM) {
            Alarm alarm = (Alarm)pushMessage.getObject(contextHelper);
            if(alarm.getType().equals("APPLY") ||
                    alarm.getType().equals("WON") ||
                    alarm.getType().equals("WARNING") ||
                    alarm.getType().equals("KEYWORD") ||
                    alarm.getType().equals("ZZIM")) {
                i.putExtra("nanum_id", alarm.getAlarmParam().nanum_id);
            }

            content = pushMessage.getMessage();
        } else {
            content = pushMessage.getMessage();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.mipmap.push);
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setContentTitle(Config.APPNAME);

        mCompatBuilder.setTicker(content);
        mCompatBuilder.setContentText(content);

        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);

        nm.notify(NOTIFICATION_ID, mCompatBuilder.build());
    }
}
