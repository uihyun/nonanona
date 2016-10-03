package com.yongtrim.lib.model.post;

import android.text.TextUtils;

import com.nuums.nuums.AppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * hair / com.yongtrim.lib.model.post
 * <p/>
 * Created by Uihyun on 15. 9. 15..
 */
public class Post extends Model {

    public static final String TYPE_NOTICE = "NOTICE";
    public static final String TYPE_FAQ = "FAQ";

    public static final String TYPE_POLICY = "POLICY";
    public static final String TYPE_PRIVACY = "PRIVACY";
    public static final String TYPE_EVENT = "EVENT";
    public static final String TYPE_BANNER = "BANNER";

    @SerializedName("type")
    private String type;

    @SerializedName("timeCreated")
    private Date timeCreated;

    @SerializedName("timeUpdated")
    private Date timeUpdated;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("photos")
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getType() {
        return type;
    }

    private ArrayList<String> listMediumUrls;

    public ArrayList<String> getMediumUrlArray() {

        if(listMediumUrls == null) {
            listMediumUrls = new ArrayList<String>();
            for (int i = 0; i < photos.size(); i++)
                listMediumUrls.add(photos.get(i).getMediumUrl());
        }

        return listMediumUrls;

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public String getUrl() {
        if(TextUtils.isEmpty(content))
            return null;

        if (!content.startsWith("http://") && !content.startsWith("https://"))
            return "http://" + content;
        return content;
    }


    public Date getTimeCreated() {
        return  this.timeCreated;
    }


    public String getUrlTag(boolean isAll) {
        List<Photo> array;

        array = getPhotos();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < array.size(); i++) {
            stringBuffer.append(array.get(i).getId());
        }

        return stringBuffer.toString();
    }



    public static Post getPost(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Post post = gson.fromJson(jsonString, Post.class);
        return post;
    }
}
