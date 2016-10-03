package com.yongtrim.lib.message;

import com.nuums.nuums.AppController;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.alarm.Alarm;
import com.nuums.nuums.model.chat.Chat;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;

/**
 * hair / com.yongtrim.lib.plugin.message
 * <p/>
 * Created by Uihyun on 15. 9. 23..
 */
public class PushMessage {
    public static final int ACTIONCODE_CHANGE_ME = 1;
    public static final int ACTIONCODE_CHANGE_LOGIN = 2;
    public static final int ACTIONCODE_CHANGE_LOGOUT = 3;
    public static final int ACTIONCODE_CHANGE_USER = 4;

    public static final int ACTIONCODE_CERTIFYEMAIL_PW = 10;
    public static final int ACTIONCODE_CERTIFYEMAIL_SIGNUP = 11;

    public static final int ACTIONCODE_CHANGE_NANUM = 20;
    public static final int ACTIONCODE_ADDED_NANUM  = 21;
    public static final int ACTIONCODE_DELETE_NANUM  = 23;
    public static final int ACTIONCODE_ADDED_NANUM_OTHER  = 24;


    public static final int ACTIONCODE_CHANGETALK = 30;
    public static final int ACTIONCODE_CHANGEUNREADCOUNT = 31;


    public static final int ACTIONCODE_CHANGETALKLIST = 40;
    public static final int ACTIONCODE_DELETETALK = 41;

    public static final int ACTIONCODE_CHANGE_REVIEW = 50;
    public static final int ACTIONCODE_ADDED_REVIEW  = 51;
    public static final int ACTIONCODE_DELETE_REVIEW  = 52;

    public static final int ACTIONCODE_CHANGE_APPLY = 60;

    public static final int ACTIONCODE_ADDED_ASK_OTHER  = 70;


    public static final int ACTIONCODE_ADDED_POST_OTHER  = 80;


    public static final int ACTIONCODE_PUSH = 100;

    public static final int ACTIONCODE_ALRAM = 101;

    public static final int ACTIONCODE_CHANGEGPS = 130;


    @SerializedName("actionCode")
    private int actionCode;

    @SerializedName("resultCode")
    private int resultCode; //0:success, 1:failure

    @SerializedName("message")
    private String message;

    private Object object;

    @SerializedName("user")
    private NsUser user;

    @SerializedName("nanum")
    private Nanum nanum;

    @SerializedName("chat")
    private Chat chat;

    @SerializedName("talk")
    private Talk talk;

    @SerializedName("review")
    private Review review;


    @SerializedName("alarm")
    private Alarm alarm;



    public PushMessage() {
    }

    public PushMessage setActionCode(int actionCode) {
        this.actionCode = actionCode;
        return this;
    }
    public int getActionCode() {
        return actionCode;
    }

    public PushMessage setObject(Object object) {

        this.object = object;
        return this;
    }

    public Object getObject(ContextHelper contextHelper) {
        if(user != null)
            return user;
        if(nanum != null) {
            nanum.patch(contextHelper);
            return nanum;
        }
        if(chat != null) {
            return chat;
        }
        if(talk != null) {
            return talk;
        }
        if(review != null) {
            return review;
        }

        if(alarm != null)
            return alarm;


        return object;
    }


    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }


    public static PushMessage getPushMessage(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        PushMessage pushMessage = gson.fromJson(jsonString, PushMessage.class);
        return pushMessage;
    }
}
