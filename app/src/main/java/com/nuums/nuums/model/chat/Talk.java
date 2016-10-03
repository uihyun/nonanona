package com.nuums.nuums.model.chat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.user.UserManager;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class Talk extends Model {

    @SerializedName("userA")
    NsUser userA;

    @SerializedName("userB")
    NsUser userB;

    @SerializedName("userAInfo")
    UserInfo userAInfo;

    @SerializedName("userBInfo")
    UserInfo userBInfo;

    @SerializedName("timeCreated")
    Date timeCreated;

    @SerializedName("timeUpdated")
    Date timeUpdated;

    @SerializedName("messageRecent")
    String messageRecent;

    @SerializedName("roomName")
    String roomName;


    public Talk() {
        userAInfo = new UserInfo();
        userBInfo = new UserInfo();

    }

    public NsUser getOther(ContextHelper contextHelper) {
        if(UserManager.getInstance(contextHelper).getMe().isSame(userA)) {
            return userB;
        } else {
            return userA;
        }
    }


    public NsUser getMe(ContextHelper contextHelper) {

        if(userA == null)
            return null;

        if(UserManager.getInstance(contextHelper).getMe().isSame(userA)) {
            return userA;
        } else {
            return userB;
        }
    }

    public UserInfo getMyInfo(ContextHelper contextHelper) {
        if(UserManager.getInstance(contextHelper).getMe().isSame(userA)) {
            return userAInfo;
        } else {
            return userBInfo;
        }
    }


    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public NsUser getUserA() {
        return userA;
    }

    public void setUserA(NsUser userA) {
        this.userA = userA;
    }

    public NsUser getUserB() {
        return userB;
    }

    public void setUserB(NsUser userB) {
        this.userB = userB;
    }

    public UserInfo getUserAInfo() {
        return userAInfo;
    }

    public void setUserAInfo(UserInfo userAInfo) {
        this.userAInfo = userAInfo;
    }

    public UserInfo getUserBInfo() {
        return userBInfo;
    }

    public void setUserBInfo(UserInfo userBInfo) {
        this.userBInfo = userBInfo;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getMessageRecent() {
        return messageRecent;
    }

    public void setMessageRecent(String messageRecent) {
        this.messageRecent = messageRecent;
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public static Talk getTalk(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Talk talk = gson.fromJson(jsonString, Talk.class);
        return talk;
    }


}
