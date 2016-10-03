package com.nuums.nuums.model.chat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.yongtrim.lib.model.misc.CodeName;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 10..
 */
public class Delivery {
    @SerializedName("company")
    CodeName company;


    @SerializedName("number")
    String number;

    public CodeName getCompany() {
        return company;
    }

    public void setCompany(CodeName company) {
        this.company = company;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public static Delivery getDelivery(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Delivery delivery = gson.fromJson(jsonString, Delivery.class);
        return delivery;
    }

    public String toString() {
        return AppController.getInstance().getGson().toJson(this);
    }

}

