package com.nuums.nuums.model.chat;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class ChatListData extends ACommonData {
    @SerializedName("results")
    private ChatList results;

    @SerializedName("talk")
    public Talk talk;


    public ChatList getChatList() {
        return results;
    }
}


