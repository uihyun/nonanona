package com.nuums.nuums.model.chat;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class TalkList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Talk> data;

    public java.util.ArrayList<Talk> getTalks() {
        return data;
    }


    public TalkList() {
        data = new ArrayList<>();
    }

}


