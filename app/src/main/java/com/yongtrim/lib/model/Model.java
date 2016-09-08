package com.yongtrim.lib.model;

import com.nuums.nuums.AppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by yongtrim.com on 15. 9. 19..
 */
public class Model {
    @SerializedName("_id")
    protected String id;

    private Object tag;
    public void setTag(Object tag) {
        this.tag = tag;
    }
    public Object getTag() {
        return tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSame(Model model) {
        return id.equals(model.getId());
    }


    public String toString() {
        return AppController.getInstance().getGson().toJson(this);
    }


    public boolean isSameContent(Model model) {
        String me = toString();
        String target = model.toString();
        if(me.compareTo(target) == 0)
            return true;
        return false;
    }
}
