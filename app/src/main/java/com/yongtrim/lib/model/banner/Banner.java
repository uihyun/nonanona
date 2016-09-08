package com.yongtrim.lib.model.banner;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by YongTrim on 16. 6. 17. for nuums_ad
 */
public class Banner extends Model {

    @SerializedName("link")
    private String link;

    @SerializedName("photos")
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
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

    public String getUrl() {
        if(TextUtils.isEmpty(link))
            return null;

        if (!link.startsWith("http://") && !link.startsWith("https://"))
            return "http://" + link;
        return link;
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



    public static Banner getBanner(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Banner banner = gson.fromJson(jsonString, Banner.class);
        return banner;
    }
}

