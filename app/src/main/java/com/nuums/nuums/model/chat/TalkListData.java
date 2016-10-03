package com.nuums.nuums.model.chat;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;
import com.yongtrim.lib.model.list.List;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class TalkListData extends ACommonData {
    @SerializedName("results")
    private TalkList results;

    public TalkList getTalkList() {
        return results;
    }



}

