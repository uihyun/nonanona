package com.yongtrim.lib.model.list;

import com.google.gson.annotations.SerializedName;

/**
 * hair / com.yongtrim.lib.model.list
 * <p/>
 * Created by yongtrim.com on 15. 9. 15..
 */
public class Item {

    @SerializedName("begin")
    private int begin;

    @SerializedName("end")
    private int end;

    @SerializedName("total")
    private int total;

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getTotal() {
        return total;
    }
}
