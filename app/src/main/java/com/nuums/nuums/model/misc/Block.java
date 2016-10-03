package com.nuums.nuums.model.misc;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;

/**
 * nuums / com.nuums.nuums.model.misc
 * <p/>
 * Created by Uihyun on 16. 1. 9..
 */
public class Block {
    @SerializedName("user")
    private NsUser user;

    public NsUser getUser() {
        return user;
    }

    public void setUser(NsUser user) {
        this.user = user;
    }
}


