package com.lqwawa.intleducation.common.utils;

import java.math.BigDecimal;

/**
 * @desc 两栖蛙蛙数据显示工具类
 * @author medici
 */
public final class LQNumberUtil {

    private LQNumberUtil(){}

    /**
     * 数据转换
     * @param count 数量词
     * @return 转换后的文本
     */
    public static String transferNumberData(int count){
        String numData = null;
        if (count < 1000){
            numData =  String.valueOf(count);
        } else if (count < 10000){
            numData =  String.valueOf(div(count, 1000, 1)) + "千";
        } else if (count < 10000000){
            numData =  String.valueOf(div(count,10000,1)) + "万";
        } else {
            numData =  String.valueOf(div(count,10000000,1)) + "千万";
        }
        return numData;
    }

    private static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
