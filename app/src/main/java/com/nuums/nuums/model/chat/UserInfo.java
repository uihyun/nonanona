package com.nuums.nuums.model.chat;

import com.google.gson.annotations.SerializedName;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by yongtrim.com on 16. 1. 6..
 */
public class UserInfo {
    @SerializedName("unreadCnt")
    int unreadCnt;


    public int getUnreadCnt() {
        return unreadCnt;
    }

    public void setUnreadCnt(int unreadCnt) {
        this.unreadCnt = unreadCnt;
    }


}
