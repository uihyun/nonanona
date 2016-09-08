package com.nuums.nuums.model.nanum;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.nanum
 * <p/>
 * Created by yongtrim.com on 15. 12. 21..
 */
public class NanumData extends ACommonData {

    public class Errfor {
        @SerializedName("count")
        private String count;
        @SerializedName("month")
        private String month;

        @SerializedName("reject")
        private String reject;
    }

    @SerializedName("errfor")
    public Errfor errfor;

    @SerializedName("nanum")
    public Nanum nanum;

    @SerializedName("user")
    public NsUser user;

    public String getErrorMessage() {
        if(!TextUtils.isEmpty(super.getErrorMessage())){
            return super.getErrorMessage();
        }

        if(errfor != null) {
            if(errfor.count != null)
                return errfor.count;
            if(errfor.month != null)
                return errfor.month;
            if(errfor.reject != null)
                return errfor.reject;
        }

        return null;
    }
}



