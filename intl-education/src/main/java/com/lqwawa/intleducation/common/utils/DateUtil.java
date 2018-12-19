package com.lqwawa.intleducation.common.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.factory.data.entity.LiveEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQWaWa专用时间工具类
 * @date 2018/06/11 15:45
 * @history v1.0
 * **********************************
 */
public class DateUtil {

    /**
     * 获取当前直播显示的起止时间
     *
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 显示字串 2018-05-12 18:20 -- 20:22 如果是隔天 2018-06-11  2018-06-18
     */
    public static String formatLiveTime(@NonNull String beginTime,@NonNull String endTime) {
        // 先判断是否是隔天的直播
        long beginMillis = TimeUtil.string2Millis(beginTime,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        long endMillis = TimeUtil.string2Millis(endTime,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        Date beginDate = new Date(beginMillis);
        Date endDate = new Date(endMillis);

        String showTime = "";

        if(beginDate.getYear() != endDate.getYear() ||
                beginDate.getMonth() != endDate.getMonth() ||
                beginDate.getDay() != endDate.getDay()){
            // 是隔天的日期
            beginTime = TimeUtil.date2String(beginDate,new SimpleDateFormat("yyyy-MM-dd"));
            endTime = TimeUtil.date2String(endDate,new SimpleDateFormat("yyyy-MM-dd"));
            showTime = beginTime + " -- " + endTime;

        }else{
            if (!TextUtils.isEmpty(beginTime)) {
                beginTime = beginTime.substring(0, beginTime.length() - 3);
            }
            if (!TextUtils.isEmpty(endTime)) {
                endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
            }
            showTime = beginTime + " -- " + endTime;
        }

        if (TextUtils.isEmpty(showTime)) {
            return "";
        }
        return showTime;


    }

}
