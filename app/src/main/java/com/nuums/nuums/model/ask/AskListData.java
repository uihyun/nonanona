package com.nuums.nuums.model.ask;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.apply.ApplyList;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.ask
 * <p/>
 * Created by Uihyun on 16. 1. 22..
 */
public class AskListData extends ACommonData {
    @SerializedName("results")
    private AskList results;


    @SerializedName("user")
    public NsUser user;

    public AskList getAskList() {
        return results;
    }

}



