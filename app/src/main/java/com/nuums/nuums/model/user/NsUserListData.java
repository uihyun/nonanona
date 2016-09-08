package com.nuums.nuums.model.user;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums
 * <p/>
 * Created by yongtrim.com on 15.12. 7..
 */
public class NsUserListData extends ACommonData {
    @SerializedName("results")
    private NsUserList results;

    public NsUserList getUserList() {
        return results;
    }
}

