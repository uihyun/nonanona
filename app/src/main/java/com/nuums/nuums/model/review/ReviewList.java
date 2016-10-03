package com.nuums.nuums.model.review;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.list.List;

import java.util.ArrayList;

/**
 * nuums / com.nuums.nuums.model.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewList extends List {
    @SerializedName("data")
    private java.util.ArrayList<Review> data;

    public java.util.ArrayList<Review> getReviews() {
        return data;
    }

    public ReviewList() {
        data = new ArrayList<>();
    }


    public void patch(ContextHelper contextHelper) {
        for(Review review : data) {
            review.patch(contextHelper);
        }
    }

}


