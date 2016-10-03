package com.nuums.nuums.model.alarm;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class AlarmListData extends ACommonData {
    @SerializedName("results")
    private AlarmList results;

    public AlarmList getAlarmList() {
        return results;
    }

}

