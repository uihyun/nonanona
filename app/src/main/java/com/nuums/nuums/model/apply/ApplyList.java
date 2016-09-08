package com.nuums.nuums.model.apply;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.apply
 * <p/>
 * Created by yongtrim.com on 16. 1. 21..
 */
public class ApplyList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Apply> data;

    public java.util.ArrayList<Apply> getApplys() {
        return data;
    }

    public ApplyList() {
        data = new ArrayList<>();
    }

    public void patch(ContextHelper contextHelper) {
        for(Apply apply : data) {
            apply.patch(contextHelper);
        }
    }

}


