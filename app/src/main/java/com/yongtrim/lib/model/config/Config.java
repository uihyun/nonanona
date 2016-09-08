package com.yongtrim.lib.model.config;

import com.nuums.nuums.AppController;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.Model;

/**
 * hair / com.yongtrim.lib.model.config
 * <p/>
 * Created by yongtrim.com on 15. 9. 2..
 */
public class Config extends Model {
    @SerializedName("type")
    private String type;

    @SerializedName("params")
    private ConfigParams params;


    public ConfigParams getParams() {
        return params;
    }

    public static Config getConfig(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Config config = gson.fromJson(jsonString, Config.class);
        return config;
    }



}
