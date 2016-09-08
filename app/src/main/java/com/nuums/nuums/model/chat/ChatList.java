package com.nuums.nuums.model.chat;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by yongtrim.com on 16. 1. 6..
 */
public class ChatList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Chat> data;

    public java.util.ArrayList<Chat> getChats() {
        return data;
    }

    public ChatList() {
        data = new ArrayList<>();
    }
}



