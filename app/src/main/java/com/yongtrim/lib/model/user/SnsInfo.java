package com.yongtrim.lib.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class SnsInfo {
    @SerializedName("snsId")
    String snsId;
    @SerializedName("snsFriends")
    List<String> snsFriends;
    @SerializedName("isCheckSnsFriends")
    Boolean isCheckSnsFriends;

    public SnsInfo() {
        snsFriends = new ArrayList<>();
    }

    public Boolean getIsCheckSnsFriends() {
        return isCheckSnsFriends;
    }

    public void setIsCheckSnsFriends(Boolean isCheckSnsFriends) {
        this.isCheckSnsFriends = isCheckSnsFriends;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    public List<String> getSnsFriends() {
        return snsFriends;
    }

    public void setSnsFriends(List<String> snsFriends) {
        this.snsFriends = snsFriends;
    }
}
