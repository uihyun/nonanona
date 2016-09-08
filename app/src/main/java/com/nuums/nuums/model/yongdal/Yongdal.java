package com.nuums.nuums.model.yongdal;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.chat.Address;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.misc.CodeName;
import com.yongtrim.lib.util.MiscUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * nuums / com.nuums.nuums.model.yongdal
 * <p/>
 * Created by yongtrim.com on 16. 1. 31..
 */
public class Yongdal extends Model {
    public static final String TYPE_NORMAL = "TYPE_NORMAL";
    public static final String TYPE_RAPID = "TYPE_RAPID";


    public static final String METHOD_BIKE = "METHOD_BIKE";
    public static final String METHOD_SMALLSTRUCK = "METHOD_SMALLSTRUCK";
    public static final String METHOD_1TONTRUCK = "METHOD_1TONTRUCK";

    @SerializedName("owner")
    NsUser owner;

    @SerializedName("addressStart")
    String addressStart;

    @SerializedName("addressEnd")
    private String addressEnd;


    @SerializedName("addressStartFake")
    private String addressStartFake;
    @SerializedName("addressEndFake")
    private String addressEndFake;

    @SerializedName("coordinatesStart")
    private List<Double> coordinatesStart;

    @SerializedName("coordinatesEnd")
    private List<Double> coordinatesEnd;

    @SerializedName("type")
    private String type;

    @SerializedName("method")
    private String method;


    @SerializedName("message")
    private String message;

    @SerializedName("weight")
    private String weight;

    @SerializedName("count")
    private String count;


    @SerializedName("price")
    private String price;

    @SerializedName("timeCreated")
    Date timeCreated;


    @SerializedName("distance")
    Double distance;

    @SerializedName("phoneNumber")
    String phoneNumber;


    public Yongdal() {
        type = TYPE_NORMAL;
        method = METHOD_BIKE;
    }


    public NsUser getOwner() {
        return owner;
    }

    public void setOwner(NsUser owner) {
        this.owner = owner;
    }

    public String getAddressStart() {
        return addressStart;
    }

    public void setAddressStart(String addressStart) {
        this.addressStart = addressStart;
    }

    public String getAddressEnd() {
        return addressEnd;
    }

    public void setAddressEnd(String addressEnd) {
        this.addressEnd = addressEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getAddressStartFake() {
        return addressStartFake;
    }

    public void setAddressStartFake(String addressStartFake) {
        this.addressStartFake = addressStartFake;
    }

    public String getAddressEndFake() {
        return addressEndFake;
    }

    public void setAddressEndFake(String addressEndFake) {
        this.addressEndFake = addressEndFake;
    }

    public String getAddressFakeShorter(boolean isStart) {
        String[] array = null;

        if(isStart)
            array = addressStartFake.split(" ");
        else
            array = addressEndFake.split(" ");

        return array[array.length - 1];
    }


    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }


    public LatLng getLocation(boolean isStart) {

        if(isStart) {
            if (coordinatesStart != null) {
                return new LatLng(coordinatesStart.get(1).doubleValue(), coordinatesStart.get(0).doubleValue());
            }
        } else {
            if (coordinatesEnd != null) {
                return new LatLng(coordinatesEnd.get(1).doubleValue(), coordinatesEnd.get(0).doubleValue());
            }
        }

        return null;
    }


    public Double getDistance() {
        return distance;
    }

    public void setDistance() {

        this.distance = MiscUtil.distance(coordinatesStart.get(1).doubleValue(), coordinatesStart.get(0).doubleValue(),
                coordinatesEnd.get(1).doubleValue(), coordinatesEnd.get(0).doubleValue());
    }



    public void setLocation(LatLng latLng, boolean isStart) {
        if(isStart) {
            coordinatesStart = new ArrayList<Double>();
            coordinatesStart.add(latLng.longitude);
            coordinatesStart.add(latLng.latitude);
        } else {
            coordinatesEnd = new ArrayList<Double>();
            coordinatesEnd.add(latLng.longitude);
            coordinatesEnd.add(latLng.latitude);
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static Yongdal getYongdal(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Yongdal yongdal = gson.fromJson(jsonString, Yongdal.class);
        return yongdal;
    }
}


