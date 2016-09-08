package com.yongtrim.lib.model.misc;

import com.google.gson.annotations.SerializedName;
import com.yongtrim.lib.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * hair / com.yongtrim.lib.model.misc
 * <p/>
 * Created by yongtrim.com on 15. 9. 21..
 */
public class CodeName {
    @SerializedName("code")
    private String code;
    public String getCode() {
        return code;
    }

    @SerializedName("name")
    private String name;
    public String getName() {
        return name;
    }

    @SerializedName("url")
    private String url;
    public String getUrl() {
        return url;
    }

    public CodeName(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public boolean isEqual(CodeName codeName) {
        if(codeName == null)
            return false;
        if(code.equals(codeName.getCode()))
            return true;
        return false;
    }

    public static String[] getNameList(List<CodeName> list) {
        String[] ret = new String[list.size()];

        for(int i = 0;i < list.size();i++) {
            ret[i] = list.get(i).name;
        }
        return ret;
    }
    public static int getIndex(List<CodeName> list, CodeName codeName) {
        if(codeName == null)
            return -1;
        if(list == null)
            return -1;
        for(int i = 0;i < list.size();i++) {
            if (list.get(i).isEqual(codeName))
                return i;
        }
        return -1;
    }


    public static int[] getIndices(List<CodeName> listRange, List<CodeName> listTarget) {
        if(listTarget == null)
            return null;

        ArrayList<Integer> array = new ArrayList<>();

        for(int i = 0;i < listTarget.size();i++) {
            for(int j = 0;j < listRange.size();j++) {
                if(listRange.get(j).isEqual(listTarget.get(i))) {
                    array.add(new Integer(j));
                    break;
                }
            }
        }

        return MiscUtil.convert(array);
    }

    public static List<CodeName> getCodeNames(List<CodeName> listRange, int[] indices) {
        ArrayList<CodeName> array = new ArrayList<>();

        for(int i = 0;i < indices.length;i++) {
            array.add(listRange.get(indices[i]));
        }

        return array;

    }

}
