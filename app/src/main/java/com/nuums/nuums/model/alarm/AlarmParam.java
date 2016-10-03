package com.nuums.nuums.model.alarm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;

/**
 * nuums / com.nuums.nuums.model.alarm
 * <p/>
 * Created by Uihyun on 16. 1. 20..
 */
public class AlarmParam {
    @SerializedName("nanum")
    Nanum nanum;

    @SerializedName("nanum_id")
    public String nanum_id;


    @SerializedName("url")
    private String url;


    public String getUrl() {
        if(TextUtils.isEmpty(url))
            return null;

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            return "http://" + url;
        return url;
    }

    public Nanum getNanum() {
        return nanum;
    }

    public void setNanum(Nanum nanum) {
        this.nanum = nanum;
    }
}

