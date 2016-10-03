package com.nuums.nuums.model.review;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.NanumList;
import com.yongtrim.lib.model.ACommonData;

/**
 * nuums / com.nuums.nuums.model.review
 * <p/>
 * Created by Uihyun on 16. 1. 18..
 */
public class ReviewListData extends ACommonData {
    @SerializedName("results")
    private ReviewList results;

    public ReviewList getReviewList() {
        return results;
    }

}





