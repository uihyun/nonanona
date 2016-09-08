package com.nuums.nuums.model.user;

/**
 * nuums / com.nuums.nuums.model.user
 * <p/>
 * Created by yongtrim.com on 16. 1. 15..
 */
public class NsUserSearch {

    String nickname;
    NsUser user;

    public NsUserSearch(String nickname, NsUser user) {
        this.nickname = nickname;
        this.user = user;
    }

    @Override
    public String toString() {
        return nickname;
    }

    public NsUser getUser() {
        return user;
    }
}
