package com.nuums.nuums.model.review;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewData extends ACommonData {

    public class Errfor {
//        @SerializedName("count")
//        private String count;
//        @SerializedName("month")
//        private String month;
//
//        @SerializedName("reject")
//        private String reject;
    }

//    @SerializedName("errfor")
//    public Errfor errfor;

    @SerializedName("review")
    public Review review;

    @SerializedName("user")
    public NsUser user;

    public String getErrorMessage() {
        if(!TextUtils.isEmpty(super.getErrorMessage())){
            return super.getErrorMessage();
        }
//
//        if(errfor != null) {
//            if(errfor.count != null)
//                return errfor.count;
//            if(errfor.month != null)
//                return errfor.month;
//            if(errfor.reject != null)
//                return errfor.reject;
//        }

        return null;
    }
}

