package com.yongtrim.lib.model.list;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by yongtrim.com on 15. 9. 15..
 */
public class List {
    @SerializedName("pages")
    private Page pages;

    @SerializedName("items")
    private Item items;


    public Page getPages() {
        return pages;
    }

    public Item getItems() {
        return items;
    }


    public void setPages(Page pages) {
        this.pages = pages;
    }


    public void setItems(Item items) {
        this.items = items;
    }


    public String toString() {
        return AppController.getInstance().getGson().toJson(this);
    }

}
