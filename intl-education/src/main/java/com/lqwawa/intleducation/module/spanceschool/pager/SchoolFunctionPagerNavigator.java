package com.lqwawa.intleducation.module.spanceschool.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.module.spanceschool.SchoolFunctionStateType;

import java.io.Serializable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 点击精品资源库回调
 * @date 2018/06/29 17:40
 * @history v1.0
 * **********************************
 */
public interface SchoolFunctionPagerNavigator extends Serializable{
    // 点击精品资源库
    void onClickBookLibrary();
    // 点击校园助手和校园巡查
    void onClickCampus(@NonNull @SchoolFunctionStateType.FunctionStateRes int state);
    // 点击学校论坛
    void onClickForum();

}
