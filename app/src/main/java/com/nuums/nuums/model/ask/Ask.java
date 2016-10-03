package com.nuums.nuums.model.ask;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.misc.CodeName;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.ask
 * <p/>
 * Created by Uihyun on 16. 1. 22..
 */
public class Ask extends Model {

    @SerializedName("owner")
    NsUser owner;

    @SerializedName("message")
    private String message;

    @SerializedName("timeCreated")
    Date timeCreated;


    public NsUser getOwner() {
        return owner;
    }

    public void setOwner(NsUser owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Ask() {
    }

    public static Ask getAsk(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Ask ask = gson.fromJson(jsonString, Ask.class);
        return ask;
    }
}

