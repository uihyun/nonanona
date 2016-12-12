package com.yongtrim.lib.model.user;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model.user
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class UserData extends ACommonData {

    @SerializedName("errfor")
    public Errfor errfor;
    @SerializedName("user")
    public NsUser user;

    public String getErrorMessage() {
        if (!TextUtils.isEmpty(super.getErrorMessage())) {
            return super.getErrorMessage();
        }

        if (errfor != null) {
            if (errfor.username != null)
                return errfor.username;
            else if (errfor.password != null)
                return errfor.password;
            else if (errfor.email != null)
                return errfor.email;
            else if (errfor.nickname != null)
                return errfor.nickname;
            else if (errfor.realname != null)
                return errfor.realname;
            else if (errfor.confirmEmail != null)
                return errfor.confirmEmail;
        }

        return null;
    }

    public boolean isCerified() {
        if (errfor != null && errfor.certifyOk != null)
            return true;
        return false;
    }

    public class Errfor {
        @SerializedName("username")
        private String username;

        @SerializedName("password")
        private String password;

        @SerializedName("email")
        private String email;

        @SerializedName("nickname")
        private String nickname;


        @SerializedName("realname")
        private String realname;

        @SerializedName("certifyOk")
        private String certifyOk;

        @SerializedName("confirmEmail")
        private String confirmEmail;

    }
}
