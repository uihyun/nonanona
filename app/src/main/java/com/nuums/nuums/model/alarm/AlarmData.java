package com.nuums.nuums.model.alarm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class AlarmData extends ACommonData {

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

    @SerializedName("alarm")
    public Alarm alarm;

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



