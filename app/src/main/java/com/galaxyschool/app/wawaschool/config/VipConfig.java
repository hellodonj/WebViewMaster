package com.galaxyschool.app.wawaschool.config;

import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.osastudio.apps.Config;

import java.util.ArrayList;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2018/1/18 16:02
 * <br/> 描    述：
 * <br/> 修订历史：
 * <br/>================================================
 */

public final class VipConfig {

    private static final String zhangke = "283220D6-7D77-4B7B-8E74-CA5B86739F7D";
    private static final String ninan = "FEF13BB7-BD73-449D-A68C-B54495BD5A49";
    private static final String maya = "71B8B539-E6E3-4548-9197-739309B4EEA6";
    private static final String zhangkeop = "283220D6-7D77-4B7B-8E74-CA5B86739F7D";
    private static final String hanningyi111 = "0F18BCD6-CFE9-4EA7-8134-A52D7BA2C152";
    //两栖蛙蛙体验学校schoolId
    private static final String LQWAWA_EXPERIENCE_SCHOOLID = "559749DB-67B6-4C61-BFD3-20C779269DBB";
    private static MyApplication sApp;

    public static ArrayList<String> getVipList() {
        ArrayList<String> vipList = new ArrayList<>(3);

        if (Config.DEBUG) {
            vipList.add(zhangkeop);
        } else {
            vipList.add(zhangke);
            vipList.add(ninan);
            vipList.add(maya);
            vipList.add(hanningyi111);
        }

        return vipList;

    }


    public static boolean isVip(Activity context) {

        if (sApp == null) {

            sApp = (MyApplication) context.getApplication();
        }
       return sApp.getPrefsManager().isVip();
    }

    /**
     * 判断是不是vip schoolId vipSchool  校本资源库关注了就能看 以及 里面的资源都是公开的
     * @param schoolId 传入的schoolId
     */
    public static boolean CheckVipSchoolId(String schoolId){
        if (TextUtils.isEmpty(schoolId)){
            return false;
        }
        if (TextUtils.equals(schoolId.toLowerCase(),LQWAWA_EXPERIENCE_SCHOOLID.toLowerCase())){
            return true;
        } else {
            return false;
        }
    }
}
