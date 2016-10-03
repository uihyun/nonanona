package com.nuums.nuums.model.alarm;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class AlarmList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Alarm> data;

    public java.util.ArrayList<Alarm> getAlarms() {
        return data;
    }

    public AlarmList() {
        data = new ArrayList<>();
    }

}


