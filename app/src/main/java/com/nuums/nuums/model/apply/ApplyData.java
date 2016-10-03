package com.nuums.nuums.model.apply;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.chat.Chat;
import com.nuums.nuums.model.chat.Talk;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.apply
 * <p/>
 * Created by Uihyun on 16. 1. 10..
 */
public class ApplyData extends ACommonData {

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

    @SerializedName("apply")
    public Apply apply;

    @SerializedName("user")
    public NsUser user;



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