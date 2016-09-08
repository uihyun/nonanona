package com.nuums.nuums.model.apply;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.alarm.AlarmList;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.apply
 * <p/>
 * Created by yongtrim.com on 16. 1. 21..
 */
public class ApplyListData extends ACommonData {
    @SerializedName("results")
    private ApplyList results;

    public ApplyList getApplyList() {
        return results;
    }

}


