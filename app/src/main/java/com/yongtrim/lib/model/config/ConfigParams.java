package com.yongtrim.lib.model.config;

import com.google.gson.annotations.SerializedName;

/**
 * hair / com.yongtrim.lib.model.config
 * <p/>
 * Created by Uihyun on 15. 9. 19..
 */
public class ConfigParams {
    @SerializedName("iosVersion")
    private String iosVersion;

    @SerializedName("iosUrl")
    private String iosUrl;

    @SerializedName("adVersion")
    private String adVersion;

    @SerializedName("adUrl")
    private String adUrl;

    @SerializedName("applyCaution")
    private String applyCaution;
    @SerializedName("nanumCaution")
    private String nanumCaution;

    @SerializedName("yongdalPhone")
    private String yongdalPhone;

    public String getIosVersion() {
        return iosVersion;
    }

    public String getIosUrl() {
        return iosUrl;
    }

    public String getAdVersion() {
        return adVersion;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public String getApplyCaution() {
        return applyCaution;
    }

    public String getNanumCaution() {
        return nanumCaution;
    }
    public String getYongdalPhone() {
        return yongdalPhone;
    }
}
