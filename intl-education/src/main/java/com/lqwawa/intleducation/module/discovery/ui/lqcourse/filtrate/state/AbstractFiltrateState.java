package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.io.Serializable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 状态设计模式
 * @date 2018/05/03 12:51
 * @history v1.0
 * **********************************
 */
public abstract class AbstractFiltrateState implements Serializable{
    // 点击分类的实体
    protected LQCourseConfigEntity mConfigEntity;
    // 当直接点击热门推荐或者其它显示的标题
    protected String mLevelName;

    // 生成标题
    public abstract String generateTitle();

}
