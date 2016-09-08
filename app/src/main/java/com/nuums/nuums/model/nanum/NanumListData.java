package com.nuums.nuums.model.nanum;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.nanum
 * <p/>
 * Created by yongtrim.com on 15. 12. 21..
 */
public class NanumListData extends ACommonData {
    @SerializedName("results")
    private NanumList results;

    public NanumList getNanumList() {
        return results;
    }

}




