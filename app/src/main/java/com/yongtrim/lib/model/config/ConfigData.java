package com.yongtrim.lib.model.config;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model.config
 * <p/>
 * Created by Uihyun on 15. 9. 2..
 */
public class ConfigData extends ACommonData {
    @SerializedName("config")
    public Config config;
}
