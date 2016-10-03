package com.yongtrim.lib.util;

import android.text.TextUtils;

import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.log.Logger;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * hair / com.yongtrim.lib.util
 * <p/>
 * Created by Uihyun on 15. 9. 21..
 */
public class MiscUtil {

    public static int getIndex(String[] array, String target) {
        if(target == null)
            return -1;
        for(int i = 0;i < array.length;i++) {
            if (target.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int[] convert(List<Integer> list) {
        int[] ret = new int[list.size()];
        for(int i = 0;i < list.size();i++) {
            ret[i] = list.get(i).intValue();
        }
        return ret;
    }

    public static boolean contain(int[] list, int value) {
        if(list == null)
            return false;

        for(int i = 0;i < list.length;i++) {
            if(list[i] == value)
                return true;
        }
        return false;
    }

    public static String getDataFromAsset(ContextHelper contextHelper, String filename) {

        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = contextHelper.getActivity().getAssets().open(filename);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();


        } catch (Exception e) {
            return null;

        }

        return buf.toString();
    }

    private static Pattern HANGLE_PATTERN = Pattern.compile("[\\x{ac00}-\\x{d7af}]");

    public static String encodeIfNeed(String input) {
//        if (StringUtil.isEmpty(input)) {
//            return input;
//        }

        Matcher matcher = HANGLE_PATTERN.matcher(input);
        while(matcher.find()) {
            String group = matcher.group();

            try {
                input = input.replace(group, URLEncoder.encode(group, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }

        return input;
    }

    public static String encode(String input) {
        try {
            input = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }

        return input;
    }

    public static String toMoneyStyle(String string) {
        DecimalFormat myFormatter = new DecimalFormat("#,###");
        String output = myFormatter.format(Double.parseDouble(string));
        return "￦" + output;
    }

    public static String toMoneyStyleNoWon(String string) {
        DecimalFormat myFormatter = new DecimalFormat("#,###");
        String output = myFormatter.format(Double.parseDouble(string));
        return output;
    }


    public static String getPostTime(Date date) {
        Date dateCur = new Date();
        long time = (dateCur.getTime() - date.getTime());


        int days = (int)(time/(1000*60*60*24));
        int hours = (int)(time/(1000*60*60));
        int minutes = (int)(time/(1000*60));

        if(minutes < 1)
            return "방금 전";
        else if(minutes < 60)
            return minutes + "분 전";
        else if(hours < 24)
            return  hours + "시간 전";
        else if(days < 10)
            return days + "일 전";

        return DateUtil.dateToString(date, "yyyy.MM.dd a hh:mm");

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        //double earthRadius = 6371000; //meters
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }


    public static String longDouble2String(int size, double value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(size);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }



    public static String stringJoin(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    public static int findIndex(String[] array, String key) {
        int index = -1;
        for(int i = 0;i < array.length;i++) {
            if (array[i].equals(key)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static int findIndex(ArrayList<String> array, String key) {
        int index = -1;
        for(int i = 0;i < array.size();i++) {
            if (array.get(i).equals(key)) {
                index = i;
                break;
            }
        }

        return index;
    }


    public static boolean containsSpecialCharacter(String s) {
        return (s == null) ? false : s.matches("[a-zA-Z.? ]*");
    }
}



