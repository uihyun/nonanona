package com.nuums.nuums.model.apply;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.misc.Bookmark;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.misc.NanumInfo;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.misc.CodeName;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.util.MiscUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * nuums / com.nuums.nuums.model.apply
 * <p/>
 * Created by Uihyun on 16. 1. 10..
 */
public class Apply extends Model {

    public static final String SIZE_SMALL = "SIZE_SMALL";
    public static final String SIZE_MEDIUM = "SIZE_MEDIUM";
    public static final String SIZE_BIG = "SIZE_BIG";

    public static final String PAY_PREPAID = "PAY_PREPAID";
    public static final String PAY_COD = "PAY_COD";


    public static final String STATUS_ACCEPT = "ACCEPT";
    public static final String STATUS_DOING = "DOING";
    public static final String STATUS_DONE = "DONE";
    public static final String STATUS_CONFIRM = "CONFIRM";


    @SerializedName("owner")
    NsUser owner;

    @SerializedName("title")
    private String title;

    @SerializedName("size")
    private String size;

    @SerializedName("caution")
    private String caution;

    @SerializedName("addressSender")
    private Address addressSender;

    @SerializedName("addressReceiver")
    private Address addressReceiver;

    @SerializedName("pay")
    private String pay;

    @SerializedName("companyCode")
    private String companyCode;

    private CodeName company;

    @SerializedName("number")
    private String number;

    @SerializedName("status")
    private String status;

    @SerializedName("timeCreated")
    Date timeCreated;


    public Apply() {
        addressSender = new Address();
        addressReceiver = new Address();
        pay = PAY_COD;
    }

    public NsUser getOwner() {
        return owner;
    }

    public void setOwner(NsUser owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public Address getAddressSender() {
        return addressSender;
    }

    public void setAddressSender(Address addressSender) {
        this.addressSender = addressSender;
    }

    public Address getAddressReceiver() {
        return addressReceiver;
    }

    public void setAddressReceiver(Address addressReceiver) {
        this.addressReceiver = addressReceiver;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public CodeName getCompany() {
        return company;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }


    public void patch(ContextHelper contextHelper) {
        Type collectionType = new TypeToken<List<CodeName>>() {}.getType();
        final List<CodeName> listDelivery = (new Gson()).fromJson(MiscUtil.getDataFromAsset(contextHelper, "delivery.json"), collectionType);

        for(CodeName codeName : listDelivery) {
            if(codeName.getCode().equals(companyCode)) {
                company = codeName;
                break;
            }
        }

    }


    public static Apply getApply(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Apply apply = gson.fromJson(jsonString, Apply.class);
        return apply;
    }
}

