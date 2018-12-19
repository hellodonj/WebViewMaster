package com.lqwawa.intleducation.base.utils;

import android.content.Context;

import com.lqwawa.intleducation.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class StringUtils {
    public static boolean isValidString(String value){
        if (value != null){
            if (!value.isEmpty()){
                return  true;
            }
        }
        return false;
    }

    public static boolean isValidWebResString(String value){
        if (isValidString(value)){
            if(value.contains("http://") || value.contains("https://")){
                return true;
            }
        }
        return false;
    }

    public static boolean isLongString(String value){
        if (isValidString(value)){
            try {
                Long.parseLong(value);
                return true;
            }catch (Exception ex){
                return false;
            }
        }
        return false;
    }

    public static boolean isIntString(String value){
        if (isValidString(value)){
            try {
                Integer.parseInt(value);
                return true;
            }catch (Exception ex){
                return false;
            }
        }
        return false;
    }

    public static int getInt(String value){
        if (isIntString(value)){
            return Integer.parseInt(value);
        }else{
            return -1;
        }
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String join(List<String> list, String split){
        if (list == null || list.size() < 1){
            return "";
        }
        String str = list.get(0);
        for (int i = 1; i < list.size(); i++){
            str += split;
            str += list.get(i);
        }
        return  str;
    }

    public static boolean isPhoneNumber(String str) {
        Pattern p = Pattern
                .compile("(^((1[0-9]))\\d{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$)");
        return str.matches(p.pattern());
    }

    public static boolean isEmailAddress(String str) {
        Pattern p = Pattern
                .compile("^(\\w+((-\\w+)|(.\\w+))*)+\\w+((-\\w+)|(.\\w+))*\\@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*.[A-Za-z0-9]+$");
        return str.matches(p.pattern());
    }

    public static String getSectionNumString(Context context, String sectionName, String num){
        if (languageIsEnglish()){
            if (sectionName.equals("节")){
                return  "Section" + num;
            }else if(sectionName.equals("课")){
                return  "Lesson" + num;
            }else if(sectionName.equals("课时")){
                return  "Period" + num;
            }
        }
        return context.getResources().getString(R.string.the) + num + sectionName;
    }

    public static String getChapterNumString(Context context, String chapterName, String num){
        if (languageIsEnglish()){
            if (chapterName.equals("单元")){
                return  "Unit" + num;
            }else if(chapterName.equals("章")){
                return  "Chapter" + num;
            }else if(chapterName.equals("周")){
                return  "Week" + num;
            }
        }
        return context.getResources().getString(R.string.the) + num + chapterName;
    }



    public static boolean languageIsEnglish(){
        return getLocaleLanguage().contains("en-");
    }

    public static String getLocaleLanguage() {
        Locale l = Locale.getDefault();
        return String.format("%s-%s", l.getLanguage(), l.getCountry());
    }

    public static String getThousandText(int number){
        if (number >= 10000) {
            double count = div(number, 10000, 1);
            return String.valueOf(count);
        } else {
            return String.valueOf(number);
        }
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
