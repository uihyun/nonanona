package com.yongtrim.lib.model.post;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.user.NsUser;
import com.yongtrim.lib.model.ACommonData;

/**
 * hair / com.yongtrim.lib.model.post
 * <p/>
 * Created by Uihyun on 15. 10. 14..
 */
public class PostListData extends ACommonData {
    @SerializedName("results")
    private PostList results;


    @SerializedName("user")
    public NsUser user;

    public PostList getPostList() {
        return results;
    }


}

