package com.nuums.nuums.model.nanum;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.misc.Bookmark;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.misc.CodeName;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.photo.PhotoManager;
import com.yongtrim.lib.model.user.SnsInfo;
import com.yongtrim.lib.model.user.UserManager;
import com.yongtrim.lib.ui.CircularNetworkImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * nuums / com.nuums.nuums.model.nanum
 * <p/>
 * Created by Uihyun on 15. 12. 21..
 */
public class Nanum extends Model {

    public static final String METHOD_ARRIVAL = "METHOD_ARRIVAL";  //선착순
    public static final String METHOD_SELECT = "METHOD_SELECT";    //나눔자 선택
    public static final String METHOD_RANDOM = "METHOD_RANDOM";    //추첨

    public static final String STATUS_READY = "READY";
    public static final String STATUS_ONGOING = "ONGOING";
    public static final String STATUS_FINISH_TIME = "FINISH_TIME";
    public static final String STATUS_FINISH_SELECT = "FINISH_SELECT";

    @SerializedName("owner")
    NsUser owner;

    @SerializedName("photos")
    private List<Photo> photos;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("amount")
    private int amount;

    @SerializedName("addressPostcode")
    private String addressPostcode;

    @SerializedName("addressOld")
    private String addressOld;

    @SerializedName("addressNew")
    private String addressNew;

    @SerializedName("addressFake")
    private String addressFake;

    @SerializedName("coordinates")
    private List<Double> coordinates;

    @SerializedName("method")
    private String method;

    @SerializedName("timeCreated")
    private Date timeCreated;

    @SerializedName("timeUpdated")
    private Date timeUpdated;

    @SerializedName("timeOngoing")
    private Date timeOngoing;

    @SerializedName("status")
    private String status;

    @SerializedName("comments")
    List<Comment> comments;

    @SerializedName("wons")
    List<Comment> wons;


    @SerializedName("bookmarks")
    private List<Bookmark> bookmarks;

    @SerializedName("isAdmin")
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }


    public List<Comment> getWons() {
        return wons;
    }

    public void  setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    private boolean isBookmark;
    public boolean isBookmark() {
        return isBookmark;
    }

    public Nanum() {
        photos = new ArrayList<>();
        for(int i = 0;i < 6;i++) {
            Photo photo = new Photo();
            photos.add(photo);
        }
        method = METHOD_ARRIVAL;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public int getPhotoCnt() {
        int count = 0;
        for(Photo photo : photos) {
            if(photo.hasPhoto())
                count++;
        }
        return  count;
    }

    public List<Photo> getPhotoSafe() {
        List<Photo> _photos = new ArrayList<>();

        for(Photo photo : photos) {
            if(photo.hasPhoto())
                _photos.add(photo);
        }

        return _photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAddressPostcode() {
        return addressPostcode;
    }

    public void setAddressPostcode(String addressPostcode) {
        this.addressPostcode = addressPostcode;
    }

    public String getAddressOld() {
        return addressOld;
    }

    public void setAddressOld(String addressOld) {
        this.addressOld = addressOld;
    }

    public String getAddressNew() {
        return addressNew;
    }

    public void setAddressNew(String addressNew) {
        this.addressNew = addressNew;
    }

    public String getAddressFake() {
        return addressFake;
    }
    public String getAddressFakeShorter() {
        String[] array = addressFake.split(" ");
        return array[array.length - 1];
    }

    public void setAddressFake(String addressFake) {
        this.addressFake = addressFake;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodDesc() {
        if(method.equals(METHOD_ARRIVAL)) {
            return "선착순";
        } else if(method.equals(METHOD_SELECT)) {
            return "나눔자 선택";
        } else {
            return "추첨";
        }
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LatLng getLocation() {
        if(coordinates != null) {
            return new LatLng(coordinates.get(1).doubleValue(), coordinates.get(0).doubleValue());
        }
        return null;
    }
    public void setLocation(LatLng latLng) {
        coordinates = new ArrayList<Double>();
        coordinates.add(latLng.longitude);
        coordinates.add(latLng.latitude);
    }

    public Date getTimeCreated() {
        return timeCreated;
    }


    public String getStatus() {
        return status;
    }

    public Date getTimeOngoing() {
        return timeOngoing;
    }

    public NsUser getOwner() {
        return owner;
    }

    public void setOwner(NsUser owner) {
        this.owner = owner;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public int getApplyCount() {
        int count = 0;
        for(Comment comment : comments) {
            if(!comment.getOwner().isSame(this.owner))
                count++;
        }
        return count;
    }

    public List<Comment> getApplys() {
        ArrayList<Comment> newArray = new ArrayList<>();

        for(Comment comment : comments) {
            if(!comment.getOwner().isSame(this.owner))
                newArray.add(comment);
        }
        return newArray;
    }

    public int getSelectCount() {
        int count = 0;
        for(Comment comment : comments) {
            if(comment.isSelect)
                count++;
        }
        return count;
    }


    public void patch(ContextHelper contextHelper) {
        NsUser me = UserManager.getInstance(contextHelper).getMe();

        isBookmark = false;
        if(bookmarks != null) {
            for(Bookmark bookmark : bookmarks) {
                if(bookmark.getOwner().isSame(me))
                    isBookmark = true;
            }
        }

        ArrayList<Comment> removeArray = new ArrayList<>();
        for(Comment comment : comments) {
            if(!comment.isActive())
                removeArray.add(comment);
        }

        comments.removeAll(removeArray);

        int index = 0;
        for(Comment comment : comments) {
            if(!owner.isSame(comment.getOwner())) {
                comment.indexApply = index++;

                for(Comment won : wons) {
                    if(won.getOwner().isSame(comment.getOwner())) {
                        comment.isWon = true;
                    }
                }
            }
        }
    }


    public ArrayList<String> getMediumUrlArray() {
        ArrayList<String> listMediumUrls;

        listMediumUrls = new ArrayList<String>();
        for (int i = 0;photos != null && i < photos.size(); i++) {
            if(photos.get(i).hasPhoto())
                listMediumUrls.add(photos.get(i).getMediumUrl());
        }
        return listMediumUrls;
    }

    public String getUrlTag() {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0;photos != null && i < photos.size(); i++) {
            if(photos.get(i).hasPhoto())
                stringBuffer.append(photos.get(i).getId());
        }

        return stringBuffer.toString();
    }



    public static Nanum getNanum(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Nanum nanum = gson.fromJson(jsonString, Nanum.class);
        return nanum;
    }


}

