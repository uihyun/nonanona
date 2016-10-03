package com.yongtrim.lib.model.list;

import com.google.gson.annotations.SerializedName;

/**
 * hair / com.yongtrim.lib.model.list
 * <p/>
 * Created by Uihyun on 15. 9. 15..
 */
public class Page {

    @SerializedName("current")
    private int current;

    @SerializedName("prev")
    private int prev;

    @SerializedName("hasPrev")
    private boolean hasPrev;

    @SerializedName("next")
    private int next;

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("total")
    private int total;

    public int getCurrent() {
        return current;
    }

    public int getPrev() {
        return prev;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public int getNext() {
        return next;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public int getTotal() {
        return total;
    }

    public void setIsHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
