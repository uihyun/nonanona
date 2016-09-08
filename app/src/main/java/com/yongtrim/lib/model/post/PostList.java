package com.yongtrim.lib.model.post;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.AppController;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * hair / com.yongtrim.lib.model.post
 * <p/>
 * Created by yongtrim.com on 15. 10. 14..
 */
public class PostList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Post> data;

    public java.util.ArrayList<Post> getPosts() {
        return data;
    }

    public PostList() {
        data = new ArrayList<>();
    }


    public static PostList getPostList(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        PostList postList = gson.fromJson(jsonString, PostList.class);
        return postList;
    }

}

