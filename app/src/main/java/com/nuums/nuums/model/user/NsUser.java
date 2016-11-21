package com.nuums.nuums.model.user;

import com.google.gson.Gson;
import com.nuums.nuums.AppController;
import com.nuums.nuums.model.misc.Comment;
import com.nuums.nuums.model.misc.NanumInfo;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.model.user.User;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * nuums / com.nuums.nuums
 * <p/>
 * Created by Uihyun on 15.12. 7..
 */
public class NsUser extends User {

    public NsUser() {

        keywords = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            keywords.add("");
        }

    }

    public static NsUser getUser(String jsonString) {
        Gson gson = AppController.getInstance().getGson();
        NsUser user = gson.fromJson(jsonString, NsUser.class);
        return user;
    }

    public void patch(ContextHelper contextHelper) {
//        List<NanumInfo> nanumsNew = new ArrayList<>();
//
//        if(nanums != null) {
//            for (NanumInfo nanumInfo : nanums) {
//                if(nanumInfo.isActive()) {
//                    nanumsNew.add(nanumInfo);
//                }
//            }
//            nanums = nanumsNew;
//        }

        List<NanumInfo> applysNew = new ArrayList<>();

        if (applys != null) {
            for (NanumInfo nanumInfo : applys) {
                if (nanumInfo.isActive()) {
                    applysNew.add(nanumInfo);
                }
            }
            applys = applysNew;
        }

//        List<NanumInfo> wonsNew = new ArrayList<>();
//
//        if(wons != null) {
//            for (NanumInfo nanumInfo : wons) {
//                if(nanumInfo.isActive()) {
//                    wonsNew.add(nanumInfo);
//                }
//            }
//            wons = wonsNew;
//        }

    }


    public NsUserSearch[] getSearchList(ContextHelper contextHelper, String keyword, java.util.ArrayList<NsUser> userNetwork) {
        HashMap<String, NsUser> map = new HashMap<String, NsUser>();

        for (NanumInfo nanumInfo : nanums) {
            if (nanumInfo.getNanum() != null) {
                for (Comment comment : nanumInfo.getNanum().getWons()) {
                    if (!comment.getOwner().isAdmin() && comment.getOwner().getNicknameSafe().contains(keyword))
                        map.put(comment.getOwner().getUsername(), comment.getOwner());
                }
            }
        }

        for (NanumInfo nanumInfo : applys) {
            if (!nanumInfo.getNanum().getOwner().isAdmin() && nanumInfo.getNanum().getOwner().getNicknameSafe().contains(keyword))
                map.put(nanumInfo.getNanum().getOwner().getUsername(), nanumInfo.getNanum().getOwner());
        }

        List<NsUser> list = new ArrayList<NsUser>(map.values());

        final Comparator<NsUser> comparator = new Comparator<NsUser>() {
            @Override
            public int compare(NsUser lhs, NsUser rhs) {
                return Collator.getInstance().compare(lhs.getNickname(), rhs.getNickname());
            }
        };
        Collections.sort(list, comparator);


        ArrayList<NsUserSearch> arrResult = new ArrayList<>();

        for (NsUser user : list) {
            if (!user.isMe(contextHelper))
                arrResult.add(new NsUserSearch(user.getNickname(), user));
        }


        if (userNetwork == null)
            arrResult.add(new NsUserSearch(keyword + "검색중...", null));


        if (userNetwork != null) {
            for (NsUser user : userNetwork) {
                boolean has = false;
                for (NsUser user2 : list) {
                    if (user2.isSame(user)) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    if (!user.isMe(contextHelper))
                        arrResult.add(new NsUserSearch(user.getNickname(), user));
                }
            }

        }


        NsUserSearch[] appArray = new NsUserSearch[arrResult.size()];
        appArray = arrResult.toArray(appArray);


        return appArray;
    }
}
