package com.nuums.nuums.model.ask;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.apply.Apply;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.ask
 * <p/>
 * Created by Uihyun on 16. 1. 22..
 */
public class AskList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Ask> data;

    public java.util.ArrayList<Ask> getAsks() {
        return data;
    }

    public AskList() {
        data = new ArrayList<>();
    }


}



