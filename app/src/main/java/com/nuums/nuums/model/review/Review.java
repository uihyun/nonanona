package com.nuums.nuums.model.review;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.misc.Bookmark;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;
import com.yongtrim.lib.model.user.UserManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * nuums / com.nuums.nuums.model.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class Review extends Model {

    public int startDummy = 0;
    boolean isStarted;
    @SerializedName("owner")
    NsUser owner;
    @SerializedName("comments")
    List<Comment> comments;
    @SerializedName("photo")
    private Photo photo;
    @SerializedName("content")
    private String content;
    @SerializedName("stars")
    private List<Bookmark> stars;
    @SerializedName("tags")
    private List<NsUser> tags;
    @SerializedName("timeCreated")
    private Date timeCreated;
    @SerializedName("timeUpdated")
    private Date timeUpdated;
    @SerializedName("tagids")
    private List<String> tagids;

    public static Review getReview(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        Review review = gson.fromJson(jsonString, Review.class);
        return review;
    }

    public void setTagids(List<NsUser> tags) {

        tagids = new ArrayList<>();

        for (NsUser tag : tags) {
            tagids.add(tag.getId());
        }

    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public NsUser getOwner() {
        return owner;
    }

    public void setOwner(NsUser owner) {
        this.owner = owner;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Bookmark> getStars() {
        return stars;
    }

    public void setStars(List<Bookmark> stars) {
        this.stars = stars;
    }

    public List<NsUser> getTags() {
        return tags;
    }

    public void setTags(List<NsUser> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public void patch(ContextHelper contextHelper) {
        NsUser me = UserManager.getInstance(contextHelper).getMe();

        isStarted = false;
        if (stars != null) {
            for (Bookmark bookmark : stars) {
                if (bookmark.getOwner().isSame(me))
                    isStarted = true;
            }
        }

    }
}

