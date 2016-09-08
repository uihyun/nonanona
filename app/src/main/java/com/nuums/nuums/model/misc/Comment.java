package com.nuums.nuums.model.misc;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.user.NsUser;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.misc
 * <p/>
 * Created by yongtrim.com on 15. 12. 28..
 */
public class Comment {
    @SerializedName("owner")
    private NsUser owner;

    @SerializedName("message")
    private String message;

    @SerializedName("timeCreated")
    private Date timeCreated;

    @SerializedName("index")
    private int index;

    @SerializedName("isActive")
    private boolean isActive;

    public int indexApply;
    public boolean isWon;

    public boolean isSelect;

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

    public int getIndex() {
        return index;
    }

    public boolean isActive() {
        return isActive;
    }
}

