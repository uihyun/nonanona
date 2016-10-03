package com.yongtrim.lib.model.photo;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model.photo
 * <p/>
 * Created by Uihyun on 15. 9. 4..
 */
public class PhotoData extends ACommonData {

//    public class Errfor {
//        @SerializedName("username")
//        private String username;
//
//        @SerializedName("password")
//        private String password;
//    }
//
//    @SerializedName("errfor")
//    public Errfor errfor;

    @SerializedName("photo")
    public Photo photo;
}

