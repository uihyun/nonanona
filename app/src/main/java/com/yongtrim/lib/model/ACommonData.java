package com.yongtrim.lib.model;

import com.nuums.nuums.AppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * hair / com.yongtrim.lib.model.common
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class ACommonData {

    @SerializedName("success")
    private boolean success;

    @SerializedName("errors")
    private List<String> errors;


    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        if(errors.size() > 0) {
            return errors.get(0);
        }

        return "";
    }


    public String toString() {
        return AppController.getInstance().getGson().toJson(this);
    }

}
