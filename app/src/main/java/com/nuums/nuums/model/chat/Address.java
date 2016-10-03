package com.nuums.nuums.model.chat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 10..
 */
public class Address {
    @SerializedName("name")
    String name;

    @SerializedName("postCode")
    String postCode;

    @SerializedName("number0")
    String number0;

    @SerializedName("number1")
    String number1;

    @SerializedName("number2")
    String number2;

    @SerializedName("addressBasic")
    String addressBasic;

    @SerializedName("addressDetail")
    String addressDetail;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getNumber0() {
        return number0;
    }

    public void setNumber0(String number0) {
        this.number0 = number0;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getAddressBasic() {
        return addressBasic;
    }

    public void setAddressBasic(String addressBasic) {
        this.addressBasic = addressBasic;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }


    public static Address getAddress(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Address address = gson.fromJson(jsonString, Address.class);
        return address;
    }


    public String toString() {
        return AppController.getInstance().getGson().toJson(this);
    }

}
