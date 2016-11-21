package com.yongtrim.lib.model.user;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.apply.Apply;
import com.nuums.nuums.model.misc.Block;
import com.nuums.nuums.model.misc.NanumInfo;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.Model;
import com.yongtrim.lib.model.photo.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * hair / com.yongtrim.lib.model
 * <p/>
 * Created by Uihyun on 15. 9. 1..
 */
public class User extends Model {

    public static String LOGINTYPE_GUEST = "GUEST";
    public static String LOGINTYPE_EMAIL = "EMAIL";
    public static String LOGINTYPE_FACEBOOK = "FACEBOOK";
    public static String LOGINTYPE_KAKAO = "KAKAO";

    public static String ROLE_ADMIN = "ADMIN";
    public static String ROLE_MEMBER = "MEMBER";
    @SerializedName("role")
    protected String role;
    @SerializedName("nanums")
    protected List<NanumInfo> nanums;
    @SerializedName("applys")
    protected List<NanumInfo> applys;
    @SerializedName("wons")
    protected List<NanumInfo> wons;
    @SerializedName("reviews")
    protected List<Review> reviews;
    @SerializedName("applyDeliverys")
    protected List<Apply> applyDeliverys;
    @SerializedName("keywords")
    protected List<String> keywords;
    @SerializedName("timeCreated")
    private Date timeCreated;
    @SerializedName("loginType")
    private String loginType;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("passwordChanged")
    private String passwordChanged;
    @SerializedName("email")
    private String email;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("realname")
    private String realname;
    @SerializedName("unreadCnt")
    private int unreadCnt;
    @SerializedName("photo")
    private Photo photo;
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("blocks")
    private List<Block> blocks;
    @SerializedName("beblockeds")
    private List<Block> beblockeds;
    @SerializedName("unreadAsk")
    private int unreadAsk;

    @SerializedName("unreadFaq")
    private int unreadFaq;

    @SerializedName("unreadNotice")
    private int unreadNotice;

    public int getUnreadAsk() {
        return unreadAsk;
    }

    public int getUnreadNotice() {
        return unreadNotice;
    }

    public int getUnreadFaq() {
        return unreadFaq;
    }

    public List<NanumInfo> getNanums() {

        return nanums;
    }

    public List<NanumInfo> getApplys() {
        return applys;
    }


    public List<NanumInfo> getWons() {
        return wons;
    }


    public List<Review> getReviews() {
        return reviews;
    }


    public List<Apply> getApplyDeliverys() {
        return applyDeliverys;
    }

    public boolean isBlock(NsUser user) {
        if (blocks == null)
            return false;

        for (Block block : blocks) {
            if (block.getUser().isSame(user))
                return true;
        }

        return false;
    }


    public Date getTimeCreated() {
        return timeCreated;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(String password) {
        this.passwordChanged = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        if (TextUtils.isEmpty(role))
            return false;
        return role.equals(ROLE_ADMIN);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknameSafe() {
        if (!TextUtils.isEmpty(nickname))
            return nickname;
        return "이름 없음";
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getRealnameSafe() {
        if (!TextUtils.isEmpty(realname))
            return realname;
        return "이름 없음";
    }


    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }


    public String getAccessToken() {
        return accessToken;
    }


    public boolean isLogin() {
        return !loginType.equals(LOGINTYPE_GUEST);
    }

    public int getUnreadCnt() {
        return unreadCnt;
    }

    public List<String> getKeywords() {

        if (keywords == null)
            keywords = new ArrayList<>();

        if (keywords.size() == 0) {
            for (int i = 0; i < 9; i++) {
                keywords.add("");
            }
        }

        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }


    public boolean isMe(ContextHelper contextHelper) {
        User me = UserManager.getInstance(contextHelper).getMe();

        return me.isSame(this);
    }


}
