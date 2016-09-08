package com.nuums.nuums.model.misc;

import com.google.gson.annotations.SerializedName;
import com.nuums.nuums.model.nanum.Nanum;
import com.nuums.nuums.model.review.Review;
import com.nuums.nuums.model.user.NsUser;

/**
 * nuums / com.nuums.nuums.model.misc
 * <p/>
 * Created by yongtrim.com on 15. 12. 29..
 */
public class Bookmark {
    @SerializedName("owner")
    private NsUser owner;

    @SerializedName("nanum")
    private Nanum nanum;

    @SerializedName("review")
    private Review review;

    public NsUser getOwner() {
        return owner;
    }

    public Nanum getNanum() {
        return nanum;
    }

    public Review getReview() {
        return review;
    }
}

