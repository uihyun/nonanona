package com.nuums.nuums.model.alarm;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.Model;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by yongtrim.com on 16. 1. 20..
 */
public class Alarm extends Model {

    @SerializedName("timeCreated")
    Date timeCreated;

    @SerializedName("actor")
    NsUser actor;

    @SerializedName("receiver")
    NsUser receiver;

    @SerializedName("alarmData")
    AlarmParam alarmParam;

    @SerializedName("type")
    String type;

    @SerializedName("group")
    String group;

    @SerializedName("message")
    String message;


    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public NsUser getActor() {
        return actor;
    }

    public void setActor(NsUser actor) {
        this.actor = actor;
    }

    public NsUser getReceiver() {
        return receiver;
    }

    public void setReceiver(NsUser receiver) {
        this.receiver = receiver;
    }

    public AlarmParam getAlarmParam() {
        return alarmParam;
    }

    public void setAlarmParam(AlarmParam alarmParam) {
        this.alarmParam = alarmParam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
