package com.nuums.nuums.model.nanum;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.nanum
 * <p/>
 * Created by yongtrim.com on 15. 12. 21..
 */
public class NanumList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Nanum> data;

    public java.util.ArrayList<Nanum> getNanums() {
        return data;
    }

    public NanumList() {
        data = new ArrayList<>();
    }


    public void patch(ContextHelper contextHelper) {
        for(Nanum nanum : data) {
            nanum.patch(contextHelper);
        }
    }

}

