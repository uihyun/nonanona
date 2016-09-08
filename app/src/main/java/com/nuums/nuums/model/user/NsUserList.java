package com.nuums.nuums.model.user;
import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums
 * <p/>
 * Created by yongtrim.com on 15.12. 7..
 */
public class NsUserList extends List {
    @SerializedName("data")
    private java.util.ArrayList<NsUser> data;

    public java.util.ArrayList<NsUser> getUsers() {
        return data;
    }

    public NsUserList() {
        data = new ArrayList<>();
    }

}
