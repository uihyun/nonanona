package com.yongtrim.lib.model.post;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model.post
 * <p/>
 * Created by yongtrim.com on 15. 9. 28..
 */
public class PostData extends ACommonData {

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

    @SerializedName("post")
    public Post post;

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

