package com.lqwawa.intleducation.factory.data.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中学校功能列表实体
 * @date 2018/06/25 10:36
 * @history v1.0
 * **********************************
 */
public class SchoolFunctionEntity extends BaseVo{


    // 开课班
    public static final int TYPE_FUNCTION_CLASS = 0x01;
    // 精品课程
    public static final int TYPE_FUNCTION_COURSE = 0x02;
    // 名师堂
    public static final int TYPE_FUNCTION_TEACHER = 0x03;
    // 精品资源库
    public static final int TYPE_FUNCTION_CHOICE_BOOKS = 0x04;
    // 校园助手和校园巡查
    public static final int TYPE_FUNCTION_CAMPUS = 0x05;
    // 学校论坛
    public static final int TYPE_FUNCTION_SCHOOL_FORUM = 0x06;

    private int type;
    @StringRes
    private int titleId;
    @DrawableRes
    private int resId;

    public SchoolFunctionEntity(int type, int titleId, int resId) {
        this.type = type;
        this.titleId = titleId;
        this.resId = resId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @StringRes
    public int getTitleId() {
        return titleId;
    }

    public void getTitleId(@StringRes int titleId) {
        this.titleId = titleId;
    }

    @DrawableRes
    public int getResId() {
        return resId;
    }

    public void setResId(@DrawableRes int resId) {
        this.resId = resId;
    }
}
