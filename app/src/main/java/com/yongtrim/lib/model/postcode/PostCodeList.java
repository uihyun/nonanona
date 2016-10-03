package com.yongtrim.lib.model.postcode;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.model.list.List;
import com.yongtrim.lib.model.post.Post;

import java.util.ArrayList;

/**
 * nuums / com.yongtrim.lib.model.postcode
 * <p/>
 * Created by Uihyun on 15. 12. 20..
 */
public class PostCodeList extends List {
    private java.util.ArrayList<PostCode> data;

    public java.util.ArrayList<PostCode> getPostCodes() {
        return data;
    }

    public PostCodeList() {
        data = new ArrayList<>();
    }

}

