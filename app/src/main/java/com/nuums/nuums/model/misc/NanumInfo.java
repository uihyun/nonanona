package com.nuums.nuums.model.misc;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.misc
 * <p/>
 * Created by Uihyun on 16. 1. 15..
 */
public class NanumInfo {
    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("nanum")
    private Nanum nanum;

    @SerializedName("timeCreated")
    private Date timeCreated;


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Nanum getNanum() {
        return nanum;
    }

    public void setNanum(Nanum nanum) {
        this.nanum = nanum;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}



