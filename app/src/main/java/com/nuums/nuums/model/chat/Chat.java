package com.nuums.nuums.model.chat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;

import java.util.Date;

/**
 * nuums / com.nuums.nuums.model.chat
 * <p/>
 * Created by Uihyun on 16. 1. 6..
 */
public class Chat extends Model {

    public final static String TYPE_MESSAGE = "MESSAGE";
    public final static String TYPE_PHOTO = "PHOTO";
    public final static String TYPE_ADDRESS = "ADDRESS";
    public final static String TYPE_DELIVERY = "DELIVERY";


    @SerializedName("from")
    private NsUser from;

    @SerializedName("to")
    private NsUser to;

    @SerializedName("type")
    private String type;//MESSAGE, PHOTO, ADDRESS, DELIVERY

    @SerializedName("read")
    boolean read;

    @SerializedName("message")
    private String message;

    @SerializedName("timeCreated")
    private Date timeCreated;


    @SerializedName("photo")
    private Photo photo;

    @SerializedName("address")
    private Address address;

    @SerializedName("delivery")
    private Delivery delivery;


    public NsUser getFrom() {
        return from;
    }

    public void setFrom(NsUser from) {
        this.from = from;
    }

    public NsUser getTo() {
        return to;
    }

    public void setTo(NsUser to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setIsRead(boolean read) {
       this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }


    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public static Chat getChat(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Chat chat = gson.fromJson(jsonString, Chat.class);
        return chat;
    }



}
