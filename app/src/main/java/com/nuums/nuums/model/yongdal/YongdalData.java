package com.nuums.nuums.model.yongdal;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.yongdal
 * <p/>
 * Created by Uihyun on 16. 1. 31..
 */
public class YongdalData extends ACommonData {

//    public class Errfor {
//        @SerializedName("username")
//        private String username;
//
//        @SerializedName("password")
//        private String password;
//
//        @SerializedName("email")
//        private String email;
//    }
//
//    @SerializedName("errfor")
//    public Errfor errfor;

    @SerializedName("yongdal")
    public Yongdal yongdal;

    @SerializedName("price")
    public int price;



    public String getErrorMessage() {
        if(!TextUtils.isEmpty(super.getErrorMessage())){
            return super.getErrorMessage();
        }

//        if(errfor != null) {
//            if(errfor.username != null)
//                return "이메일을 입력해 주세요.";
//            else if(errfor.password != null)
//                return "비밀번호를 입력해 주세요.";
//            else if(errfor.email != null)
//                return "이메일을 입력해 주세요.";
//        }

        return null;
    }
}
