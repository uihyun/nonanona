package com.nuums.nuums.model.report;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * nuums / com.nuums.nuums.model.report
 * <p/>
 * Created by yongtrim.com on 16. 1. 27..
 */
public class Report extends Model {
    public static final String REPORTTYPE_REPORT = "REPORT";
    public static final String REPORTTYPE_AD = "AD";
    public static final String REPORTTYPE_INQUIRY = "INQUIRY";

    @SerializedName("type")
    String type;


    @SerializedName("message")
    String message;

    @SerializedName("photos")
    private List<Photo> photos;


    //
    // 신고하기
    @SerializedName("userTargetId")
    String userTargetId;

    @SerializedName("userTarger")
    NsUser userTarger;


    @SerializedName("reportType")
    int reportType = 0;

    //
    // 광고문의/1:1문의하기
    @SerializedName("title")
    String title;

    @SerializedName("email")
    String email;


    public Report(String type) {
        this.type = type;
        this.photos = new ArrayList<>();
        for(int i = 0;i < 3;i++) {
            this.photos.add(null);
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Photo> getPhotos() {
        if(photos == null) {
            this.photos = new ArrayList<>();
            for(int i = 0;i < 3;i++) {
                this.photos.add(null);
            }
        }
        return photos;
    }

    public void setPhoto(int index, Photo photo) {
        this.photos.set(index, photo);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getUserTargetId() {
        return userTargetId;
    }

    public void setUserTargetId(String userTargetId) {
        this.userTargetId = userTargetId;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public NsUser getUserTarger() {
        return userTarger;
    }

    public void setUserTarger(NsUser userTarger) {
        this.userTarger = userTarger;
    }

    public static Report getReport(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Report report = gson.fromJson(jsonString, Report.class);
        return report;
    }

}
